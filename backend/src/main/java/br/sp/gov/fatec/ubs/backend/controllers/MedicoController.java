package br.sp.gov.fatec.ubs.backend.controllers;

import br.sp.gov.fatec.ubs.backend.model.Medico;
import br.sp.gov.fatec.ubs.backend.services.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/medicos")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MEDICO')")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    // Criar novo médico
    @PostMapping
    public ResponseEntity<Medico> criarMedico(@RequestBody Map<String, Object> request) {
        try {
            String nomeCompleto = (String) request.get("nomeCompleto");
            String crm = (String) request.get("crm");
            String email = (String) request.get("email");
            String telefone = (String) request.get("telefone");

            @SuppressWarnings("unchecked")
            List<String> especialidades = (List<String>) request.get("especialidades");

            Medico medico = medicoService.criarMedico(nomeCompleto, especialidades, crm, email, telefone);
            return ResponseEntity.ok(medico);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Listar todos os médicos
    @GetMapping
    public ResponseEntity<List<Medico>> listarTodos() {
        return ResponseEntity.ok(medicoService.listarTodos());
    }

    // Listar apenas médicos ativos
    @GetMapping("/ativos")
    public ResponseEntity<List<Medico>> listarAtivos() {
        return ResponseEntity.ok(medicoService.listarAtivos());
    }

    // Buscar médico por ID
    @GetMapping("/{id}")
    public ResponseEntity<Medico> buscarPorId(@PathVariable Integer id) {
        Optional<Medico> medico = medicoService.buscarPorId(id);
        return medico.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Buscar médico por CRM
    @GetMapping("/crm/{crm}")
    public ResponseEntity<Medico> buscarPorCrm(@PathVariable String crm) {
        Optional<Medico> medico = medicoService.buscarPorCrm(crm);
        return medico.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Buscar médicos por especialidade
    @GetMapping("/especialidade/{especialidade}")
    public ResponseEntity<List<Medico>> buscarPorEspecialidade(@PathVariable String especialidade) {
        return ResponseEntity.ok(medicoService.buscarPorEspecialidade(especialidade));
    }

    // Buscar médicos por nome
    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<Medico>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(medicoService.buscarPorNome(nome));
    }

    // Atualizar médico
    @PutMapping("/{id}")
    public ResponseEntity<Medico> atualizarMedico(@PathVariable Integer id, @RequestBody Map<String, Object> request) {
        try {
            String nomeCompleto = (String) request.get("nomeCompleto");
            String crm = (String) request.get("crm");
            String email = (String) request.get("email");
            String telefone = (String) request.get("telefone");

            @SuppressWarnings("unchecked")
            List<String> especialidades = (List<String>) request.get("especialidades");

            Medico medico = medicoService.atualizarMedico(id, nomeCompleto, especialidades, crm, email, telefone);
            return ResponseEntity.ok(medico);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Ativar/Desativar médico
    @PutMapping("/{id}/status")
    public ResponseEntity<Medico> alterarStatus(@PathVariable Integer id, @RequestBody Map<String, Boolean> request) {
        try {
            boolean ativo = request.get("ativo");
            Medico medico = medicoService.alterarStatus(id, ativo);
            return ResponseEntity.ok(medico);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Deletar médico permanentemente
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> deletarMedicoPermanente(@PathVariable Integer id) {
        try {
            medicoService.deletarMedico(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}