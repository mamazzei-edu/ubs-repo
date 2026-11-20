
package br.sp.gov.fatec.ubs.backend.controllers;

import br.sp.gov.fatec.ubs.backend.model.Paciente;
import br.sp.gov.fatec.ubs.backend.services.FilaEsperaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fila-espera")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MEDICO')")
public class FilaEsperaController {

    private final FilaEsperaService filaEsperaService;

    public FilaEsperaController(FilaEsperaService filaEsperaService) {
        this.filaEsperaService = filaEsperaService;
    }

    // Lista os pacientes aguardando atendimento.
    @GetMapping
    public ResponseEntity<List<Paciente>> listarFila() {
        return ResponseEntity.ok(filaEsperaService.listarFila());
    }

    // Adiciona um paciente à fila.
    @PostMapping("/{id}")
    public ResponseEntity<?> adicionarPaciente(@PathVariable Long id) {
        try {
            Optional<Paciente> paciente =
                    filaEsperaService.adicionarPaciente(id);

            if (paciente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(paciente.get());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    // Chama o próximo paciente.
    @PostMapping("/proximo")
    public ResponseEntity<Paciente> chamarProximo() {

        Optional<Paciente> paciente =
                filaEsperaService.chamarProximo();

        return paciente
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.noContent().build()
                );
    }

    // Remove um paciente específico da fila.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerPaciente(@PathVariable Long id) {

        boolean removido =
                filaEsperaService.removerPaciente(id);

        if (removido) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
