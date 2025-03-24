package br.sp.gov.fatec.ubs.backend.armazenamento;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArmazenamentoSistemaDeArquivoService implements ArmazenamentoService {

    private final Path localArmazenamento;
    
    @Autowired
    public ArmazenamentoSistemaDeArquivoService(ArmazenamentoPropriedades armazenamentoPropriedades) {

        if(armazenamentoPropriedades.getLocalArmazenamento().trim().length() == 0) {
            throw new ArmazenamentoException("Propriedades de armazenamento não configuradas");
        }
            this.localArmazenamento = Paths.get(armazenamentoPropriedades.getLocalArmazenamento());
    }


    @Override
    public void init() {
        try {
            Files.createDirectories(localArmazenamento);
        } catch (Exception e) {
            throw new ArmazenamentoException("Não foi possível criar o diretório de armazenamento", e);
        }
    }

    @Override
    public void armazenar(MultipartFile arquivo) {
        try {
            if (arquivo.isEmpty()) {
                throw new ArmazenamentoException("Falha ao armazenar arquivo vazio " + arquivo.getOriginalFilename());
            }
            var destino = this.localArmazenamento.resolve(arquivo.getOriginalFilename()).normalize().toAbsolutePath();
            if(!destino.getParent().equals(this.localArmazenamento.toAbsolutePath())) {
                throw new ArmazenamentoException("Não é permitido armazenar fora do diretório de armazenamento " + arquivo.getOriginalFilename());
            }
            try(InputStream entrada = arquivo.getInputStream()) {
                Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
            }
            String texto = ExtraiTextoPDF.extraiTextoPDF(destino.toString());
            System.out.println(texto);
            String texto2 = ExtraiTextoPDF.extraiTextoPDFiText(destino.toString());
            System.out.println("Itext:");
            System.out.println(texto2);
            String regex = "^Ficha(.*)";
            //String regex = "(.*)?[A-Za-z0-9.]+@[A-Za-z0-9]+\\.[a-zA-Z]+(\\.[A-Za-z]+)?+(\\.[A-Za-z]+)?(.*)?";
            boolean encontrou = texto2.matches(regex);
            if (encontrou) {
                System.out.println("Encontrou email");
            } else {
                System.out.println("Não encontrou email");
            }
        } catch (Exception e) {
            throw new ArmazenamentoException("Falha ao armazenar arquivo " + arquivo.getOriginalFilename(), e);
        }
    }

    @Override
    public Stream<Path> carregarTodos() {
        try {
            return Files.walk(this.localArmazenamento, 1).filter(path -> !path.equals(this.localArmazenamento)).map(this.localArmazenamento::relativize);
        } catch (Exception e) {
            throw new ArmazenamentoException("Falha ao ler arquivos armazenados", e);
        }
    }

    @Override
    public Path carregar(String nomeArquivo) {
        return localArmazenamento.resolve(nomeArquivo);
    }

    @Override
    public Resource carregarComoRecurso(String nomeArquivo) {
        try {
            Path arquivo = carregar(nomeArquivo);
            Resource recurso = new UrlResource(arquivo.toUri());
            if (recurso.exists() || recurso.isReadable()) {
                return recurso;
            } else {
                throw new ArmazenamentoFileNotFoundException("Não foi possível ler o arquivo: " + nomeArquivo);
            }

        } catch (Exception e) {
            throw new ArmazenamentoFileNotFoundException("Não foi possível ler o arquivo: " + nomeArquivo, e);
        }

    }

    @Override
    public void deletarTodos() {
        FileSystemUtils.deleteRecursively(localArmazenamento.toFile());
    }

    
}
