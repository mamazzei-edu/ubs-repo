package br.sp.gov.fatec.ubs.backend.armazenamento;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.sp.gov.fatec.ubs.backend.armazenamento.ArmazenamentoFileNotFoundException;
import br.sp.gov.fatec.ubs.backend.armazenamento.ArmazenamentoService;

@Controller
public class TransfArquivoController {
    private final ArmazenamentoService armazenamentoService;

    @Autowired
    public TransfArquivoController(ArmazenamentoService armazenamentoService) {
        this.armazenamentoService = armazenamentoService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }


    @GetMapping("/arquivos")
    public String listaArquivos(Model model) throws IOException {
        model.addAttribute("arquivos", armazenamentoService.carregarTodos().map(
                path -> MvcUriComponentsBuilder.fromMethodName(TransfArquivoController.class, "servirArquivo", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));

        return "listaArquivos";
    }

    @GetMapping("/arquivos/{nomeArquivo:.+}")
    @ResponseBody
    public ResponseEntity<Resource> servirArquivo(@PathVariable String nomeArquivo) {
        Resource arquivo = armazenamentoService.carregarComoRecurso(nomeArquivo);

        if (arquivo == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + arquivo.getFilename() + "\"").body(arquivo);
    }

    @PostMapping("/arquivos")
    public String manipularArquivo(@RequestParam("arquivos") MultipartFile arquivo, RedirectAttributes redirectAttributes) {
        armazenamentoService.armazenar(arquivo);
        redirectAttributes.addFlashAttribute("mensagem",
                "Você enviou com sucesso " + arquivo.getOriginalFilename() + "!");

        return "redirect:/arquivos";
    }
}
