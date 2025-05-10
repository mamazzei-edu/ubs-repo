package br.sp.gov.fatec.ubs.backend.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    // Método para listar todos os pacientes
    public List<Paciente> listarPacientes() {
        return pacienteRepository.findAll();  // Busca todos os pacientes no banco
    }

    // Método para buscar um paciente pelo ID
    public Optional<Paciente> buscarPacientePorId(Long id) {
        return pacienteRepository.findById(id);  // Busca o paciente pelo ID
    }

    // Método para salvar um novo paciente
    public Paciente salvarPaciente(Paciente paciente) {
        return pacienteRepository.save(paciente);  // Salva o paciente no banco
    }

    // Método para excluir paciente
    public void excluirPaciente(Long id) {
        pacienteRepository.deleteById(id);  // Exclui o paciente pelo ID
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
            paciente.setVinculoEstabelecimento(pacienteAtualizado.getVinculoEstabelecimento());
            paciente.setDeficiencia(pacienteAtualizado.getDeficiencia());
            paciente.setContatoCelular(pacienteAtualizado.getContatoCelular());
            paciente.setContatoResidencial(pacienteAtualizado.getContatoResidencial());
            paciente.setContatoComercial(pacienteAtualizado.getContatoComercial());
            paciente.setContatoEmail(pacienteAtualizado.getContatoEmail());
            paciente.setCpf(pacienteAtualizado.getCpf());
            
            return pacienteRepository.save(paciente);  // Salva as alterações no banco
        }
        return null;  // Retorna null caso o paciente não seja encontrado
    }
}
