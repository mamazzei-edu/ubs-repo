package br.sp.gov.fatec.ubs.backend.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sp.gov.fatec.ubs.backend.dtos.RegisterUserDto;
import br.sp.gov.fatec.ubs.backend.dtos.UsuarioDto;
import br.sp.gov.fatec.ubs.backend.exceptions.CrmDuplicadoException;
import br.sp.gov.fatec.ubs.backend.exceptions.ValidaCRM;
import br.sp.gov.fatec.ubs.backend.model.Medico;
import br.sp.gov.fatec.ubs.backend.model.Role;
import br.sp.gov.fatec.ubs.backend.model.RoleEnum;
import br.sp.gov.fatec.ubs.backend.model.User;
import br.sp.gov.fatec.ubs.backend.repositories.MedicoRepository;
import br.sp.gov.fatec.ubs.backend.repositories.RoleRepository;
import br.sp.gov.fatec.ubs.backend.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MedicoRepository medicoRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
            MedicoRepository medicoRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.medicoRepository = medicoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =====================================================================
    // CONSULTAS
    // =====================================================================

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /** Usuários no formato da tela de cadastro, já com os dados do médico. */
    public List<UsuarioDto> listarDtos() {
        List<UsuarioDto> lista = new ArrayList<>();
        for (User user : findAll()) {
            lista.add(paraDto(user));
        }
        return lista;
    }

    public UsuarioDto buscarDto(Long id) {
        User user = findById(id);
        return user == null ? null : paraDto(user);
    }

    /**
     * Converte o usuário para o contrato da tela, acrescentando os campos do
     * médico vinculado quando existir. A senha nunca vai no retorno.
     */
    public UsuarioDto paraDto(User user) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setMatricula(user.getMatricula());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getNomeUsuario());
        dto.setRole(user.getRole());
        dto.setCrm(user.getCrm());

        medicoRepository.findByUserId(user.getId()).ifPresent(medico -> {
            dto.setCrm(medico.getCrm());
            dto.setEspecialidade(medico.getEspecialidade());
            dto.setTelefone(medico.getTelefone());
            dto.setAtivo(medico.isAtivo());
        });
        return dto;
    }

    // =====================================================================
    // GRAVAÇÃO
    // =====================================================================

    /**
     * Grava o usuário da tela de cadastro.
     *
     * Quando a função é MEDICO, cria ou atualiza também o registro em "medico"
     * — é ele que o Agendamento referencia. Sem isso, um usuário com função
     * MEDICO não apareceria como médico agendável em lugar nenhum.
     *
     * @param id null para criação; preenchido para edição
     */
    @Transactional
    public UsuarioDto salvarUsuario(UsuarioDto dto, Long id) {
        Role role = resolverRole(dto);
        boolean ehMedico = role != null && role.getName() == RoleEnum.MEDICO;

        exigir(dto.getFullName(), "O nome completo é obrigatório.");
        exigir(dto.getEmail(), "O e-mail é obrigatório.");
        exigir(dto.getMatricula(), "A matrícula é obrigatória.");
        exigir(dto.getUsername(), "O nome de usuário é obrigatório.");
        String crmNormalizado = null;
        if (ehMedico) {
            exigir(dto.getCrm(), "O CRM é obrigatório para a função MEDICO.");
            exigir(dto.getEspecialidade(), "A especialidade é obrigatória para a função MEDICO.");

            // ValidaCRM confere o formato (4 a 7 dígitos + sigla) E se a sigla
            // corresponde a um estado existente, devolvendo o valor já em
            // maiúsculo e sem espaços. Validar aqui, e não apenas pelas
            // anotações da entidade, é o que permite recusar com 400 e uma
            // mensagem útil, em vez de deixar o Hibernate estourar no INSERT.
            crmNormalizado = ValidaCRM.format(dto.getCrm());
            if (crmNormalizado == null) {
                throw new IllegalArgumentException(
                        "CRM inválido: \"" + dto.getCrm().trim() + "\". Informe de 4 a 7 dígitos "
                        + "seguidos da sigla do estado, sem espaços nem pontuação. Exemplo: 12345SP.");
            }
        }

        User user = id == null ? new User() : findById(id);
        if (user == null) {
            return null;
        }

        boolean novo = user.getId() == null;
        if (novo) {
            exigir(dto.getPassword(), "A senha é obrigatória no cadastro de usuário.");
        }

        user.setFullName(dto.getFullName().trim())
            .setEmail(dto.getEmail().trim().toLowerCase())
            .setMatricula(dto.getMatricula().trim())
            .setUsername(dto.getUsername().trim())
            .setRole(role);

        // Na edição, senha em branco significa "manter a atual".
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        user.setCrm(crmNormalizado);

        User salvo = userRepository.save(user);
        sincronizarMedico(salvo, dto, crmNormalizado);
        return paraDto(salvo);
    }

    /**
     * Mantém a tabela "medico" em dia com a função do usuário.
     *
     * Deixar de ser médico NÃO apaga o registro: ele pode estar referenciado
     * por agendamentos. O médico é apenas marcado como inativo.
     */
    private void sincronizarMedico(User user, UsuarioDto dto, String crm) {
        Optional<Medico> vinculado = medicoRepository.findByUserId(user.getId());

        if (crm == null) {
            vinculado.ifPresent(medico -> {
                medico.setAtivo(false);
                medicoRepository.save(medico);
            });
            return;
        }

        // O crm chega pronto de salvarUsuario: já validado por ValidaCRM e
        // normalizado (sem espaços, em maiúsculo).
        Medico medico = vinculado.orElseGet(
                () -> medicoRepository.findByCrm(crm).orElseGet(Medico::new));

        // CRM é único em "medico": recusar antes de o banco recusar.
        Long idMedico = medico.getId() == null ? -1L : medico.getId();
        if (medicoRepository.existsByCrmAndIdNot(crm, idMedico)) {
            throw new CrmDuplicadoException(crm);
        }

        medico.setUserId(user.getId());
        medico.setNomeCompleto(user.getFullName());
        medico.setEmail(user.getEmail());
        medico.setCrm(crm);
        medico.setEspecialidade(dto.getEspecialidade().trim());
        medico.setTelefone(dto.getTelefone() == null ? null : dto.getTelefone().trim());
        medico.setAtivo(dto.getAtivo() == null || dto.getAtivo());
        medicoRepository.save(medico);
    }

    private Role resolverRole(UsuarioDto dto) {
        if (dto.getRole() == null || dto.getRole().getId() == null) {
            throw new IllegalArgumentException("A função do usuário é obrigatória.");
        }
        return roleRepository.findById(dto.getRole().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Função não encontrada com id " + dto.getRole().getId()));
    }

    private void exigir(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    // =====================================================================
    // DEMAIS OPERAÇÕES (mantidas)
    // =====================================================================

    @Transactional
    public void deleteUserById(Long id) {
        User user = findById(id);
        if (user != null) {
            // O médico vinculado é apenas inativado: agendamentos apontam para ele.
            medicoRepository.findByUserId(user.getId()).ifPresent(medico -> {
                medico.setAtivo(false);
                medico.setUserId(null);
                medicoRepository.save(medico);
            });
        }
        userRepository.deleteById(id);
    }

    /** Cria administrador a partir do cadastro de admin. */
    public User createAdministrator(RegisterUserDto input) {
        if (input.getPassword() == null || input.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória para criar um administrador.");
        }

        Role role = roleRepository.findByName(RoleEnum.ADMIN)
                .orElseThrow(() -> new IllegalStateException("Role ADMIN não encontrada no banco de dados."));

        var user = new User()
                .setFullName(input.getFullName())
                .setEmail(input.getEmail())
                .setPassword(passwordEncoder.encode(input.getPassword()))
                .setMatricula(input.getMatricula())
                .setUsername(input.getUsername())
                .setRole(role);

        if (input.getCrm() != null) {
            user.setCrm(input.getCrm());
        }

        return userRepository.save(user);
    }

    /** Gravação direta da entidade, usada por telas que já montam o User. */
    public User save(User input) {
        if (input.getPassword() == null || input.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("A senha não pode ser nula ou vazia.");
        }

        Role role = roleRepository.findById(input.getRole().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Role não encontrada com ID: " + input.getRole().getId()));
        input.setRole(role);

        if (!input.getPassword().startsWith("$2a")) {
            input.setPassword(passwordEncoder.encode(input.getPassword()));
        }

        return userRepository.save(input);
    }
}
