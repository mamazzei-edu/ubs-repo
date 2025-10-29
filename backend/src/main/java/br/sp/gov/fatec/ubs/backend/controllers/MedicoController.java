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
    public ResponseEntity<Medico> criarMedico(@RequestBody Map<String, String> request) {
        try {
            String nomeCompleto = request.get("nomeCompleto");
            String especialidade = request.get("especialidade");
            String crm = request.get("crm");
            String email = request.get("email");
            String telefone = request.get("telefone");
            
            Medico medico = medicoService.criarMedico(nomeCompleto, especialidade, crm, email, telefone);
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
        List<Medico> medicos = medicoService.listarTodos();
        return ResponseEntity.ok(medicos);
    }
    
    // Listar apenas médicos ativos
    @GetMapping("/ativos")
    public ResponseEntity<List<Medico>> listarAtivos() {
        List<Medico> medicos = medicoService.listarAtivos();
        return ResponseEntity.ok(medicos);
    }
    
    // Buscar médico por ID
    @GetMapping("/{id}")
    public ResponseEntity<Medico> buscarPorId(@PathVariable Long id) {
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
        List<Medico> medicos = medicoService.buscarPorEspecialidade(especialidade);
        return ResponseEntity.ok(medicos);
    }
    
    // Buscar médicos por nome
    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<Medico>> buscarPorNome(@PathVariable String nome) {
        List<Medico> medicos = medicoService.buscarPorNome(nome);
        return ResponseEntity.ok(medicos);
    }
    
    // Atualizar médico
    @PutMapping("/{id}")
    public ResponseEntity<Medico> atualizarMedico(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String nomeCompleto = request.get("nomeCompleto");
            String especialidade = request.get("especialidade");
            String crm = request.get("crm");
            String email = request.get("email");
            String telefone = request.get("telefone");
            
            Medico medico = medicoService.atualizarMedico(id, nomeCompleto, especialidade, crm, email, telefone);
            return ResponseEntity.ok(medico);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Ativar/Desativar médico
    @PutMapping("/{id}/status")
    public ResponseEntity<Medico> alterarStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        try {
            boolean ativo = request.get("ativo");
            Medico medico = medicoService.alterarStatus(id, ativo);
            return ResponseEntity.ok(medico);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    
    // Deletar médico permanentemente (endpoint administrativo)
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> deletarMedicoPermanente(@PathVariable Long id) {
        try {
            medicoService.deletarMedico(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
