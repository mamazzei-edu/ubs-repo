package br.sp.gov.fatec.ubs.backend.controllers;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sp.gov.fatec.ubs.backend.entities.PacienteEntity;
import br.sp.gov.fatec.ubs.backend.repositories.PacienteRepository;

@RestController
@RequestMapping("/api/paciente")  
@PreAuthorize("hasAnyRole('SUPER_ADMIN')")
public class PacienteController {

    @Autowired
    private PacienteRepository bd;

    // Criar um novo paciente
    @PostMapping
    public ResponseEntity<PacienteEntity> gravar(@RequestBody PacienteEntity obj) {
        PacienteEntity pacienteSalvo = bd.save(obj);
        return ResponseEntity.ok(pacienteSalvo);
    }

    // Buscar um paciente pelo código
    @GetMapping("/{codigo}")
    public ResponseEntity<PacienteEntity> ler(@PathVariable Long codigo) {
        Optional<PacienteEntity> obj = bd.findById(codigo);
        return obj.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Remover um paciente pelo código
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> remover(@PathVariable long codigo) {
        if (!bd.existsById(codigo)) {
            return ResponseEntity.notFound().build(); 
        }
        bd.deleteById(codigo);
        return ResponseEntity.ok().body(new HashMap<String, String>() {{
            put("mensagem", "Paciente " + codigo + " removido com sucesso");
        }});
    }

    // Atualizar um paciente pelo código
    @PutMapping("/{codigo}")
    public ResponseEntity<PacienteEntity> alterar(@PathVariable Long codigo, @RequestBody PacienteEntity obj) {
        if (!bd.existsById(codigo)) {
            return ResponseEntity.notFound().build();
        }
        obj.setCodigo(codigo);
        PacienteEntity atualizado = bd.save(obj);
        return ResponseEntity.ok(atualizado);
    }

    // Listar todos os pacientes
    @GetMapping
    public ResponseEntity<Iterable<PacienteEntity>> listar() {
        return ResponseEntity.ok(bd.findAll());
    }
}
