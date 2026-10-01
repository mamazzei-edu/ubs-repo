package br.sp.gov.fatec.ubs.backend.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import br.sp.gov.fatec.ubs.backend.dtos.AlteracaoCampo;
import br.sp.gov.fatec.ubs.backend.dtos.ResultadoUploadFicha;
import br.sp.gov.fatec.ubs.backend.model.Paciente;
import br.sp.gov.fatec.ubs.backend.services.ArmazenamentoService;
import br.sp.gov.fatec.ubs.backend.services.PacienteService;


@Controller
public class TransfArquivoController {
    private final ArmazenamentoService armazenamentoService;
    private final PacienteService pacienteService;

    @Autowired
    public TransfArquivoController(ArmazenamentoService armazenamentoService,
            PacienteService pacienteService) {
        this.armazenamentoService = armazenamentoService;
        this.pacienteService = pacienteService;
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

    /**
     * Lê a ficha em PDF e devolve o que deve aparecer no formulário de conferência.
     *
     * Quando o CPF já consta na base, a resposta traz o registro gravado com os
     * campos da ficha por cima (com o id, para a gravação atualizar em vez de
     * inserir) e a lista do que está mudando. Nada é gravado aqui — quem grava é
     * o POST em /api/pacientes, depois da conferência.
     */
    @PostMapping("/arquivos")
    @ResponseBody
    public ResponseEntity<ResultadoUploadFicha> manipularArquivo(MultipartFile ficha) {
        Paciente daFicha = armazenamentoService.armazenar(ficha);
        daFicha.setCpf(PacienteService.normalizarCpf(daFicha.getCpf()));

        Optional<Paciente> gravado = pacienteService.buscarPorCpf(daFicha.getCpf());
        if (gravado.isEmpty()) {
            return ResponseEntity.ok(new ResultadoUploadFicha(daFicha, false, List.of()));
        }

        List<AlteracaoCampo> alteracoes = pacienteService.diferencas(gravado.get(), daFicha);
        Paciente paraConferencia = pacienteService.mesclarComFicha(gravado.get(), daFicha);
        return ResponseEntity.ok(new ResultadoUploadFicha(paraConferencia, true, alteracoes));
    }
}
