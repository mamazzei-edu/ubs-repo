package br.sp.gov.fatec.ubs.backend;
 
import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
 

@CrossOrigin(origins = "*")


@RestController
public class PacienteController {
    @Autowired
    PacienteRepository bd;
 
    @PostMapping("/api/paciente")
    public PacienteEntity gravar(@RequestBody PacienteEntity obj){
        bd.save(obj);
        return obj;
    }
 
    @GetMapping("/api/paciente/{codigo}")
    public PacienteEntity ler(@PathVariable long codigo){
        Optional<PacienteEntity> obj = bd.findById(codigo);
        if(obj.isPresent()){
            return obj.get();
        }else{
            return null;
        }
 
    }
 
 @DeleteMapping("/api/paciente/{codigo}")
public ResponseEntity<?> remover(@PathVariable long codigo) {
    bd.deleteById(codigo);
    // Retorne um objeto JSON com a mensagem
    return ResponseEntity.ok().body(new HashMap<String, String>() {{
        put("mensagem", "paciente " + codigo + " removido com sucesso");
    }});
}

 
    @PutMapping("/api/paciente")
    public String alterar(@RequestBody PacienteEntity obj){
    bd.save(obj);
    return "Paciente alterado com sucesso";
    }
 
    @GetMapping("/api/paciente")
    public Iterable<PacienteEntity>listar(){
        return bd.findAll();
}
 
    @PostMapping("/api/paciente/{codigo}/upload-ficha")
    public ResponseEntity<?> uploadFicha(@PathVariable long codigo, @RequestParam("file") MultipartFile file) {
        try {
            // Verifica se o arquivo não está vazio
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                    put("mensagem", "Arquivo vazio. Por favor, selecione um arquivo válido.");
                }});
            }

            // Aqui você pode salvar o arquivo no banco de dados ou em um diretório
            // Exemplo: salvar o arquivo em um diretório local
            String filePath = "uploads/ficha-" + codigo + "-" + file.getOriginalFilename();
            file.transferTo(new java.io.File(filePath)); // Salvando o arquivo no diretório "uploads"

            // Você também pode associar o arquivo com o paciente no banco de dados se necessário
            Optional<PacienteEntity> paciente = bd.findById(codigo);
            if (paciente.isPresent()) {
                // Exemplo: você poderia associar o caminho do arquivo à ficha do paciente
                PacienteEntity pacienteEntity = paciente.get();
                pacienteEntity.setFicha(filePath); // Supondo que você tenha um campo 'ficha' na sua entidade
                bd.save(pacienteEntity); // Atualizando o paciente com o novo caminho do arquivo
            }

            return ResponseEntity.ok().body(new HashMap<String, String>() {{
                put("mensagem", "Ficha carregada com sucesso.");
            }});

        } catch (IOException e) {
            return ResponseEntity.status(500).body(new HashMap<String, String>() {{
                put("mensagem", "Erro ao salvar o arquivo: " + e.getMessage());
            }});
        }
    }
}

