package br.sp.gov.fatec.ubs.backend.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    // Endpoint para salvar um novo paciente
    @PostMapping
    public ResponseEntity<Paciente> salvarPaciente(@RequestBody Paciente paciente) {
        Paciente pacienteSalvo = pacienteService.salvarPaciente(paciente);
        return new ResponseEntity<>(pacienteSalvo, HttpStatus.CREATED);
    }

    // Endpoint para listar todos os pacientes
    @GetMapping
    public ResponseEntity<List<Paciente>> listarPacientes() {
        List<Paciente> pacientes = pacienteService.listarPacientes();
        return new ResponseEntity<>(pacientes, HttpStatus.OK);
    }

    // Endpoint para buscar um paciente pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPaciente(@PathVariable Long id) {
        Optional<Paciente> paciente = pacienteService.buscarPacientePorId(id);
        if (paciente.isPresent()) {
            return new ResponseEntity<>(paciente.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint para excluir paciente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirPaciente(@PathVariable Long id) {
        Optional<Paciente> paciente = pacienteService.buscarPacientePorId(id);
        if (paciente.isPresent()) {
            pacienteService.excluirPaciente(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint para atualizar um paciente
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> atualizarPaciente(
            @PathVariable Long id, @RequestBody Paciente pacienteAtualizado) {
        Optional<Paciente> pacienteOpt = pacienteService.buscarPacientePorId(id);

        if (pacienteOpt.isPresent()) {
            Paciente paciente = pacienteOpt.get();

            // Atualizando os campos
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
            // Atualize outros campos conforme necessário...

            Paciente pacienteAtualizadoSalvo = pacienteService.salvarPaciente(paciente);
            return new ResponseEntity<>(pacienteAtualizadoSalvo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
