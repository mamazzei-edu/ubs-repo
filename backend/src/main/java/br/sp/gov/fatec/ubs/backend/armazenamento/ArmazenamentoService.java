package br.sp.gov.fatec.ubs.backend.armazenamento;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import br.sp.gov.fatec.ubs.backend.entities.PacienteEntity;


public interface ArmazenamentoService {

    void init();

    PacienteEntity armazenar(MultipartFile arquivo);

    Stream<Path> carregarTodos();

    Path carregar(String nomeArquivo);

    Resource carregarComoRecurso(String nomeArquivo);

    void deletarTodos();

}
