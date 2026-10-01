package br.sp.gov.fatec.ubs.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.sp.gov.fatec.ubs.backend.model.Paciente;
import br.sp.gov.fatec.ubs.backend.services.PacienteService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pacientes")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MEDICO')")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    /**
     * Grava um paciente. O CPF é único na base: se já existir registro com o
     * mesmo CPF (ou se vier id), este endpoint ATUALIZA esse registro em vez de
     * inserir outro. Devolve 201 quando criou e 200 quando atualizou.
     */
    @PostMapping
    public ResponseEntity<Paciente> salvarPaciente(@RequestBody Paciente paciente) {
        boolean atualizou = pacienteService.jaExistia(paciente);
        Paciente pacienteSalvo = pacienteService.salvarOuAtualizarPorCpf(paciente);
        return new ResponseEntity<>(pacienteSalvo,
                atualizou ? HttpStatus.OK : HttpStatus.CREATED);
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

    /**
     * Atualiza o paciente de id informado. A cópia dos campos é feita no
     * serviço, por reflexão: a lista escrita à mão que existia aqui cobria 20
     * dos 48 campos, e os demais eram descartados em silêncio.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> atualizarPaciente(
            @PathVariable Long id, @RequestBody Paciente pacienteAtualizado) {
        Paciente salvo = pacienteService.editarPaciente(id, pacienteAtualizado);
        if (salvo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(salvo, HttpStatus.OK);
    }
}
