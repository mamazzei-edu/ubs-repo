package br.sp.gov.fatec.ubs.backend.controllers;

import br.sp.gov.fatec.ubs.backend.model.Agendamento;
import br.sp.gov.fatec.ubs.backend.services.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "http://localhost:4200")
public class AgendamentoController {
    
    @Autowired
    private AgendamentoService agendamentoService;
    
    /**
     * Criar novo agendamento.
     *
     * Sem try/catch: as regras de negócio do serviço ("Médico não disponível
     * neste horário", "Não é possível agendar para horários no passado"...)
     * sobem como IllegalArgumentException e o GlobalExceptionHandler devolve
     * 400 COM a mensagem. Antes o corpo ia vazio e nenhuma tela sabia dizer
     * ao usuário o que estava errado.
     */
    @PostMapping
    public ResponseEntity<Agendamento> criarAgendamento(@RequestBody Map<String, Object> request) {
        Long pacienteId = lerId(request, "pacienteId", "O paciente é obrigatório");
        Long medicoId = lerId(request, "medicoId", "O médico é obrigatório");
        LocalDateTime dataHora = lerDataHora(request.get("dataHoraConsulta"));
        String tipoConsulta = lerTexto(request, "tipoConsulta", "O tipo de consulta é obrigatório");
        String observacoes = request.get("observacoes") != null ? request.get("observacoes").toString() : "";

        Agendamento agendamento = agendamentoService.criarAgendamento(pacienteId, medicoId, dataHora, tipoConsulta, observacoes);
        return ResponseEntity.ok(agendamento);
    }

    // Listar todos os agendamentos
    @GetMapping
    public ResponseEntity<List<Agendamento>> listarTodos() {
        List<Agendamento> agendamentos = agendamentoService.listarTodos();
        return ResponseEntity.ok(agendamentos);
    }

    /**
     * Agendamentos entre duas datas/horas, em ordem cronológica.
     * Usado pela agenda do dia do app mobile, que não precisa baixar tudo.
     *
     * Ex.: /api/agendamentos/periodo?inicio=2026-10-05T00:00:00&amp;fim=2026-10-05T23:59:59
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<Agendamento>> listarPorPeriodo(@RequestParam String inicio, @RequestParam String fim) {
        LocalDateTime de = lerDataHora(inicio);
        LocalDateTime ate = lerDataHora(fim);
        if (ate.isBefore(de)) {
            throw new IllegalArgumentException("O fim do período é anterior ao início");
        }
        return ResponseEntity.ok(agendamentoService.listarEntreDatas(de, ate));
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
    public ResponseEntity<List<Agendamento>> listarPorMedico(@PathVariable Long medicoId) {
        List<Agendamento> agendamentos = agendamentoService.listarPorMedico(medicoId);
        return ResponseEntity.ok(agendamentos);
    }
    
    // Verificar disponibilidade do médico
    @GetMapping("/medico/{medicoId}/disponibilidade")
    public ResponseEntity<Map<String, Boolean>> verificarDisponibilidade(
            @PathVariable Long medicoId, 
            @RequestParam String dataHora) {
        try {
            LocalDateTime data = LocalDateTime.parse(dataHora);
            boolean disponivel = agendamentoService.verificarDisponibilidade(medicoId, data);
            return ResponseEntity.ok(Map.of("disponivel", disponivel));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
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
    
    // Marcar como realizado
    @PutMapping("/{id}/realizado")
    public ResponseEntity<Agendamento> marcarComoRealizado(@PathVariable Long id) {
        try {
            Agendamento agendamento = agendamentoService.marcarComoRealizado(id);
            return ResponseEntity.ok(agendamento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Marcar falta
    @PutMapping("/{id}/falta")
    public ResponseEntity<Agendamento> marcarFalta(@PathVariable Long id) {
        try {
            Agendamento agendamento = agendamentoService.marcarFalta(id);
            return ResponseEntity.ok(agendamento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Reagendar consulta. Erros de regra sobem com mensagem (400), como no POST.
    @PutMapping("/{id}/reagendar")
    public ResponseEntity<Agendamento> reagendar(@PathVariable Long id, @RequestBody Map<String, String> request) {
        LocalDateTime novaDataHora = lerDataHora(request.get("dataHoraConsulta"));
        Agendamento agendamento = agendamentoService.reagendar(id, novaDataHora);
        return ResponseEntity.ok(agendamento);
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
    
    // Buscar próximos agendamentos do médico
    @GetMapping("/medico/{medicoId}/proximos")
    public ResponseEntity<List<Agendamento>> buscarProximosAgendamentosMedico(@PathVariable Long medicoId) {
        List<Agendamento> agendamentos = agendamentoService.buscarProximosAgendamentosMedico(medicoId);
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

    // ---- leitura do corpo, com mensagem clara em vez de NullPointerException ----

    private static Long lerId(Map<String, Object> request, String campo, String mensagemSeAusente) {
        Object valor = request.get(campo);
        if (valor == null || valor.toString().isBlank()) {
            throw new IllegalArgumentException(mensagemSeAusente);
        }
        try {
            return Long.valueOf(valor.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor inválido para " + campo + ": " + valor);
        }
    }

    private static String lerTexto(Map<String, Object> request, String campo, String mensagemSeAusente) {
        Object valor = request.get(campo);
        if (valor == null || valor.toString().isBlank()) {
            throw new IllegalArgumentException(mensagemSeAusente);
        }
        return valor.toString().trim();
    }

    /** Aceita "2026-10-05T14:30" e "2026-10-05T14:30:00". */
    private static LocalDateTime lerDataHora(Object valor) {
        if (valor == null || valor.toString().isBlank()) {
            throw new IllegalArgumentException("A data e a hora da consulta são obrigatórias");
        }
        try {
            return LocalDateTime.parse(valor.toString().trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data/hora inválida: " + valor + " (formato esperado: aaaa-MM-ddTHH:mm)");
        }
    }
}