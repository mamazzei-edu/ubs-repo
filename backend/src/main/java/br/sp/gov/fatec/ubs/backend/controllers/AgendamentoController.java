package br.sp.gov.fatec.ubs.backend.controllers;

import br.sp.gov.fatec.ubs.backend.model.Agendamento;
import br.sp.gov.fatec.ubs.backend.services.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "http://localhost:4200")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    // Criar novo agendamento
    @PostMapping
    public ResponseEntity<Agendamento> criarAgendamento(@RequestBody Map<String, Object> request) {
        try {
            Long pacienteId = Long.valueOf(request.get("pacienteId").toString());
            Integer medicoId = Integer.valueOf(request.get("medicoId").toString());
            LocalDateTime dataHora = LocalDateTime.parse(request.get("dataHoraConsulta").toString());
            String tipoConsulta = request.get("tipoConsulta").toString();
            String observacoes = request.get("observacoes") != null ? request.get("observacoes").toString() : "";

            Agendamento agendamento = agendamentoService.criarAgendamento(pacienteId, medicoId, dataHora, tipoConsulta, observacoes);
            return ResponseEntity.ok(agendamento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Listar todos os agendamentos
    @GetMapping
    public ResponseEntity<List<Agendamento>> listarTodos() {
        List<Agendamento> agendamentos = agendamentoService.listarTodos();
        return ResponseEntity.ok(agendamentos);
    }

    // Buscar agendamento por ID
    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable Long id) {
        Optional<Agendamento> agendamento = agendamentoService.buscarPorId(id);
        return agendamento.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Listar agendamentos por paciente
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<Agendamento>> listarPorPaciente(@PathVariable Long pacienteId) {
        List<Agendamento> agendamentos = agendamentoService.listarPorPaciente(pacienteId);
        return ResponseEntity.ok(agendamentos);
    }

    // Listar agendamentos por médico
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<Agendamento>> listarPorMedico(@PathVariable Integer medicoId) {
        List<Agendamento> agendamentos = agendamentoService.listarPorMedico(medicoId);
        return ResponseEntity.ok(agendamentos);
    }

    // Atualizar status do agendamento
    @PutMapping("/{id}/status")
    public ResponseEntity<Agendamento> atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            Agendamento.StatusAgendamento novoStatus = Agendamento.StatusAgendamento.valueOf(status);
            Agendamento agendamento = agendamentoService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok(agendamento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Cancelar agendamento
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Agendamento> cancelarAgendamento(@PathVariable Long id) {
        try {
            Agendamento agendamento = agendamentoService.cancelarAgendamento(id);
            return ResponseEntity.ok(agendamento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Confirmar agendamento
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Agendamento> confirmarAgendamento(@PathVariable Long id) {
        try {
            Agendamento agendamento = agendamentoService.confirmarAgendamento(id);
            return ResponseEntity.ok(agendamento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Atualizar observações
    @PutMapping("/{id}/observacoes")
    public ResponseEntity<Agendamento> atualizarObservacoes(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String observacoes = request.get("observacoes");
            Agendamento agendamento = agendamentoService.atualizarObservacoes(id, observacoes);
            return ResponseEntity.ok(agendamento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Buscar próximos agendamentos do paciente
    @GetMapping("/paciente/{pacienteId}/proximos")
    public ResponseEntity<List<Agendamento>> buscarProximosAgendamentos(@PathVariable Long pacienteId) {
        List<Agendamento> agendamentos = agendamentoService.buscarProximosAgendamentos(pacienteId);
        return ResponseEntity.ok(agendamentos);
    }

    // Deletar agendamento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAgendamento(@PathVariable Long id) {
        try {
            agendamentoService.deletarAgendamento(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}