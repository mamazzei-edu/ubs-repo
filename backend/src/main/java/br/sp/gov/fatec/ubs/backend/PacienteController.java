package br.sp.gov.fatec.ubs.backend;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class PacienteController {
    @Autowired
    PacienteRepository bd;

    @PostMapping("/api/paciente")
    public PacienteEntity gravar(@RequestBody PacienteEntity obj) {
        bd.save(obj);
        return obj;
    }

    @GetMapping("/api/paciente/{codigo}")
    public PacienteEntity ler(@PathVariable long codigo) {
        Optional<PacienteEntity> obj = bd.findById(codigo);
        return obj.orElse(null);
    }

    @DeleteMapping("/api/paciente/{codigo}")
    public ResponseEntity<?> remover(@PathVariable long codigo) {
        bd.deleteById(codigo);
        return ResponseEntity.ok().body(new HashMap<String, String>() {{
            put("mensagem", "Paciente " + codigo + " removido com sucesso");
        }});
    }

    @PutMapping("/api/paciente/{codigo}")
    public String alterar(@RequestBody PacienteEntity obj) {
        bd.save(obj);
        return "Paciente alterado com sucesso";
    }

    @GetMapping("/api/paciente")
    public Iterable<PacienteEntity> listar() {
        return bd.findAll();
    }
}
