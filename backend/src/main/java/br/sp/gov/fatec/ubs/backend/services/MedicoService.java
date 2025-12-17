package br.sp.gov.fatec.ubs.backend.services;

import br.sp.gov.fatec.ubs.backend.model.User;
import br.sp.gov.fatec.ubs.backend.repositories.MedicoRepository;
import br.sp.gov.fatec.ubs.backend.exceptions.ValidaCRM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    // Criar novo médico
    public User criarMedico(String nomeCompleto, String especialidade, String crm, String email, String telefone) {
        // Validar CRM
        if (!ValidaCRM.isValid(crm)) {
            throw new IllegalArgumentException("CRM inválido: " + crm);
        }

        // Formatar CRM
        String crmFormatado = ValidaCRM.format(crm);

        // Verificar se CRM já existe
        if (medicoRepository.findByCrm(crmFormatado).isPresent()) {
            throw new IllegalArgumentException("CRM já cadastrado: " + crmFormatado);
        }

        // Verificar se email já existe
        if (medicoRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado: " + email);
        }

        User medico = new User();
        medico.setFullName(nomeCompleto);
        medico.setEspecialidade(especialidade);
        medico.setCrm(crmFormatado);
        medico.setEmail(email);
        medico.setTelefone(telefone);
        // Note: Password and Role setting might be needed here or handled separately

        return medicoRepository.save(medico);
    }

    // Listar todos os médicos
    public List<User> listarTodos() {
        return medicoRepository.findAll();
    }

    // Listar apenas médicos ativos
    public List<User> listarAtivos() {
        return medicoRepository.findByAtivoTrue();
    }

    // Buscar médico por ID
    public Optional<User> buscarPorId(Long id) {
        return medicoRepository.findById(id);
    }

    // Buscar médico por CRM
    public Optional<User> buscarPorCrm(String crm) {
        String crmFormatado = ValidaCRM.format(crm);
        if (crmFormatado == null) {
            return Optional.empty();
        }
        return medicoRepository.findByCrm(crmFormatado);
    }

    // Buscar médicos por especialidade
    public List<User> buscarPorEspecialidade(String especialidade) {
        return medicoRepository.findByEspecialidadeAndAtivoTrue(especialidade);
    }

    // Buscar médicos por nome
    public List<User> buscarPorNome(String nome) {
        return medicoRepository.findByFullNameContainingIgnoreCaseAndAtivoTrue(nome);
    }

    // Atualizar médico
    public User atualizarMedico(Long id, String nomeCompleto, String especialidade, String crm, String email,
            String telefone) {
        Optional<User> medicoOptional = medicoRepository.findById(id);
        if (medicoOptional.isEmpty()) {
            throw new IllegalArgumentException("Médico não encontrado");
        }

        User medico = medicoOptional.get();

        // Validar e formatar CRM se foi alterado
        if (medico.getCrm() != null && !medico.getCrm().equals(crm)) {
            // If CRM was previously null, we just set it. If it was not null and different,
            // we check validity.
            if (!ValidaCRM.isValid(crm)) {
                throw new IllegalArgumentException("CRM inválido: " + crm);
            }

            String crmFormatado = ValidaCRM.format(crm);

            // Verificar se o novo CRM já está em uso por outro médico
            if (medicoRepository.existsByCrmAndIdNot(crmFormatado, id)) {
                throw new IllegalArgumentException("CRM já cadastrado: " + crmFormatado);
            }

            medico.setCrm(crmFormatado);
        } else if (medico.getCrm() == null && crm != null) {
            if (!ValidaCRM.isValid(crm)) {
                throw new IllegalArgumentException("CRM inválido: " + crm);
            }
            String crmFormatado = ValidaCRM.format(crm);
            if (medicoRepository.existsByCrmAndIdNot(crmFormatado, id)) {
                throw new IllegalArgumentException("CRM já cadastrado: " + crmFormatado);
            }
            medico.setCrm(crmFormatado);
        }

        // Validar email se foi alterado
        if (!medico.getEmail().equals(email)) {
            if (medicoRepository.existsByEmailAndIdNot(email, id)) {
                throw new IllegalArgumentException("Email já cadastrado: " + email);
            }
            medico.setEmail(email);
        }

        medico.setFullName(nomeCompleto);
        medico.setEspecialidade(especialidade);
        medico.setTelefone(telefone);

        return medicoRepository.save(medico);
    }

    // Ativar/Desativar médico
    public User alterarStatus(Long id, boolean ativo) {
        Optional<User> medicoOptional = medicoRepository.findById(id);
        if (medicoOptional.isEmpty()) {
            throw new IllegalArgumentException("Médico não encontrado");
        }

        User medico = medicoOptional.get();
        medico.setAtivo(ativo);

        return medicoRepository.save(medico);
    }

    // Deletar médico (soft delete - apenas desativa)
    public void desativarMedico(Long id) {
        alterarStatus(id, false);
    }

    // Deletar médico permanentemente (use com cuidado!)
    public void deletarMedico(Long id) {
        if (!medicoRepository.existsById(id)) {
            throw new IllegalArgumentException("Médico não encontrado");
        }
        medicoRepository.deleteById(id);
    }
}