package br.sp.gov.fatec.ubs.backend;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/paciente")  // Melhor organização das rotas
public class PacienteController {

    @Autowired
    private PacienteRepository bd;

    @PostMapping
    public ResponseEntity<PacienteEntity> gravar(@RequestBody PacienteEntity obj) {
        PacienteEntity pacienteSalvo = bd.save(obj);
        return ResponseEntity.ok(pacienteSalvo);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<PacienteEntity> ler(@PathVariable Long codigo) {
        Optional<PacienteEntity> obj = bd.findById(codigo);
        return obj.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> remover(@PathVariable Long codigo) {
        if (bd.existsById(codigo)) {
            bd.deleteById(codigo);
            return ResponseEntity.ok().body("Paciente " + codigo + " removido com sucesso");
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<PacienteEntity> alterar(@PathVariable Long codigo, @RequestBody PacienteEntity obj) {
        if (!bd.existsById(codigo)) {
            return ResponseEntity.notFound().build();
        }
        obj.setCodigo(codigo);
        PacienteEntity atualizado = bd.save(obj);
        return ResponseEntity.ok(atualizado);
    }

    @GetMapping
    public ResponseEntity<Iterable<PacienteEntity>> listar() {
        return ResponseEntity.ok(bd.findAll());
    }
}
