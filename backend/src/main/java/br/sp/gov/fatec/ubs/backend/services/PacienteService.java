package br.sp.gov.fatec.ubs.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.sp.gov.fatec.ubs.backend.model.Paciente;
import br.sp.gov.fatec.ubs.backend.repositories.PacienteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    // Método para listar todos os pacientes
    public List<Paciente> listarPacientes() {
        return pacienteRepository.findAll(); // Busca todos os pacientes no banco
    }

    // Método para listar pacientes com paginação
    public Page<Paciente> listarPacientesPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return pacienteRepository.findAll(pageable);
    }

    // Método para buscar um paciente pelo ID
    public Optional<Paciente> buscarPacientePorId(Long id) {
        return pacienteRepository.findById(id); // Busca o paciente pelo ID
    }

    // Método para buscar um paciente pelo prontuário
    public Optional<Paciente> buscarPacientePorProntuario(String prontuario) {
        try {
            if (prontuario == null || prontuario.trim().isEmpty()) {
                return Optional.empty();
            }
            return pacienteRepository.findByProntuario(prontuario);
        } catch (Exception e) {
            System.err.println("Erro ao buscar paciente por prontuário: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // Método para buscar um paciente pelo nome completo
    public Optional<Paciente> buscarPacientePorNome(String nomeCompleto) {
        try {
            if (nomeCompleto == null || nomeCompleto.trim().isEmpty()) {
                return Optional.empty();
            }
            return pacienteRepository.findByNomeCompleto(nomeCompleto);
        } catch (Exception e) {
            System.err.println("Erro ao buscar paciente por nome: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // Método para buscar pacientes por nome parcial
    public List<Paciente> buscarPorNomeParcial(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return List.of();
        }
        return pacienteRepository.findByNomeCompletoStartingWithIgnoreCase(nome);
    }

    // Método para buscar pacientes por CPF parcial
    public List<Paciente> buscarPorCpfParcial(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return List.of();
        }
        return pacienteRepository.findByCpfStartingWith(cpf);
    }

    // Método para salvar um novo paciente com validação de duplicação
    public Paciente salvarPaciente(Paciente paciente) {
        // VALIDAÇÃO DE CAMPOS OBRIGATÓRIOS
        if (paciente.getNomeCompleto() == null || paciente.getNomeCompleto().trim().isEmpty()) {
            throw new RuntimeException("O campo 'Nome Completo' é obrigatório!");
        }

        if (paciente.getProntuario() == null || paciente.getProntuario().trim().isEmpty()) {
            throw new RuntimeException("O campo 'Número de Prontuário' é obrigatório!");
        }

        if (paciente.getDataNascimento() == null || paciente.getDataNascimento().trim().isEmpty()) {
            throw new RuntimeException("O campo 'Data de Nascimento' é obrigatório!");
        }

        // Verificar se já existe paciente com mesmo prontuário (se prontuário foi
        // informado)
        if (paciente.getProntuario() != null && !paciente.getProntuario().trim().isEmpty()) {
            Optional<Paciente> existentePorProntuario = pacienteRepository.findByProntuario(paciente.getProntuario());
            if (existentePorProntuario.isPresent() && !existentePorProntuario.get().getId().equals(paciente.getId())) {
                throw new RuntimeException(
                        "Já existe um paciente cadastrado com este prontuário: " + paciente.getProntuario());
            }
        }

        // Verificar se já existe paciente com mesmo nome completo
        if (paciente.getNomeCompleto() != null && !paciente.getNomeCompleto().trim().isEmpty()) {
            Optional<Paciente> existentePorNome = pacienteRepository.findByNomeCompleto(paciente.getNomeCompleto());
            if (existentePorNome.isPresent() && !existentePorNome.get().getId().equals(paciente.getId())) {
                throw new RuntimeException(
                        "Já existe um paciente cadastrado com este nome: " + paciente.getNomeCompleto());
            }
        }

        return pacienteRepository.save(paciente); // Salva o paciente no banco
    }

    // Método para excluir paciente
    public void excluirPaciente(Long id) {
        pacienteRepository.deleteById(id); // Exclui o paciente pelo ID
    }

    // Método para editar um paciente
    public Paciente editarPaciente(Long id, Paciente pacienteAtualizado) {
        Optional<Paciente> pacienteExistente = pacienteRepository.findById(id);
        if (pacienteExistente.isPresent()) {
            Paciente paciente = pacienteExistente.get();

            // Atualizando os dados do paciente
            paciente.setNomeCompleto(pacienteAtualizado.getNomeCompleto());
            paciente.setNomeSocial(pacienteAtualizado.getNomeSocial());
            paciente.setNomeMae(pacienteAtualizado.getNomeMae());
            paciente.setNomePai(pacienteAtualizado.getNomePai());
            paciente.setDataNascimento(pacienteAtualizado.getDataNascimento());
            paciente.setSexo(pacienteAtualizado.getSexo());
            paciente.setNacionalidade(pacienteAtualizado.getNacionalidade());
            paciente.setMunicipioNascimento(pacienteAtualizado.getMunicipioNascimento());
            paciente.setRacaCor(pacienteAtualizado.getRacaCor());
            paciente.setFrequentaEscola(pacienteAtualizado.getFrequentaEscola());
            paciente.setEscolaridade(pacienteAtualizado.getEscolaridade());
            paciente.setSituacaoFamiliar(pacienteAtualizado.getSituacaoFamiliar());
            paciente.setEstabelecimentoVinculo(pacienteAtualizado.getEstabelecimentoVinculo());
            paciente.setDeficiente(pacienteAtualizado.getDeficiente());
            paciente.setTelefoneCelular(pacienteAtualizado.getTelefoneCelular());
            paciente.setTelefoneResidencial(pacienteAtualizado.getTelefoneResidencial());
            paciente.setTelefoneComercial(pacienteAtualizado.getTelefoneComercial());
            paciente.setEmail(pacienteAtualizado.getEmail());
            paciente.setCpf(pacienteAtualizado.getCpf());
            paciente.setCns(pacienteAtualizado.getCns());
            paciente.setProntuario(pacienteAtualizado.getProntuario());
            paciente.setEtnia(pacienteAtualizado.getEtnia());
            paciente.setOcupacao(pacienteAtualizado.getOcupacao());
            paciente.setVisual(pacienteAtualizado.getVisual());
            paciente.setAuditiva(pacienteAtualizado.getAuditiva());
            paciente.setMotora(pacienteAtualizado.getMotora());
            paciente.setIntelectual(pacienteAtualizado.getIntelectual());
            paciente.setOpm(pacienteAtualizado.getOpm());
            paciente.setCep(pacienteAtualizado.getCep());
            paciente.setLogradouro(pacienteAtualizado.getLogradouro());
            paciente.setNumero(pacienteAtualizado.getNumero());
            paciente.setBairro(pacienteAtualizado.getBairro());
            paciente.setComplemento(pacienteAtualizado.getComplemento());
            paciente.setUf(pacienteAtualizado.getUf());
            paciente.setMunicipioResidencia(pacienteAtualizado.getMunicipioResidencia());
            paciente.setDistritoAdministrativo(pacienteAtualizado.getDistritoAdministrativo());
            paciente.setTipoLogradouro(pacienteAtualizado.getTipoLogradouro());
            paciente.setOrigemEndereco(pacienteAtualizado.getOrigemEndereco());
            paciente.setReferencia(pacienteAtualizado.getReferencia());
            paciente.setRg(pacienteAtualizado.getRg());
            paciente.setOrgaoEmissor(pacienteAtualizado.getOrgaoEmissor());
            paciente.setPisPasepNis(pacienteAtualizado.getPisPasepNis());
            paciente.setCnh(pacienteAtualizado.getCnh());
            paciente.setCtps(pacienteAtualizado.getCtps());
            paciente.setTituloEleitor(pacienteAtualizado.getTituloEleitor());
            paciente.setPassaporte(pacienteAtualizado.getPassaporte());
            paciente.setEstabelecimentoCadastro(pacienteAtualizado.getEstabelecimentoCadastro());

            return pacienteRepository.save(paciente); // Salva as alterações no banco
        }
        return null; // Retorna null caso o paciente não seja encontrado
    }
}
