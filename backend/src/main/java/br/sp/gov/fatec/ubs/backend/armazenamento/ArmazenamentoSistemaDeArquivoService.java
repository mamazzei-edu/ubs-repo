package br.sp.gov.fatec.ubs.backend.armazenamento;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import br.sp.gov.fatec.ubs.backend.model.Paciente;

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
    public Paciente armazenar(MultipartFile arquivo) {
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
            // mascaras é um dicionário que armazena as expressões regulares
            // e os nomes das propriedades correspondentes
            // Exemplo: mascaras.put("nomeMae", "Nome da Mãe: (.*)");
            HashMap<String, String> mascaras = new HashMap<String, String>();
            // Coincide com valores começando com "CNS" captura o valor com (.*) em grupo1            
            mascaras.put("cns","^CNS\\s*:\\s*(.*)$");
            // Coincide com valores terminando com "-CSE GERALDO DE PAULA SOUZA" captura o valor com (\\d+*) em grupo1
            mascaras.put("prontuario", "^(\\d+)\\s*-CSE GERALDO DE PAULA SOUZA$");
            // Coincide com valores começando com "Usuário:" captura o valor com (.*) em grupo1 
            // seguida por espaço \\s* e Nome Social: e se existir algum valor após (.*?) coloca e
            // em grupo2
            // Aqui é necessário colocar a ? após o * para que a expressão não consuma a próxima linha
            mascaras.put("nomeCompleto","^Usuário:\\s*(.*)\\s*Nome Social:\\s*?(.*?)$");
            // Coincide com valores começando com "Mãe:" captura o valor com (.*) em grupo1
            // seguida por espaço \\s* Pai: e se existir algum valor após (.*?) colocar
            // em grupo2
            mascaras.put("nomeMae", "^Mãe:\\s*(.*)\\s*Pai:\\s*?(.*?)$");
            // Adicione mascaras para cada um dos valores adicionais que você deseja extrair

            Paciente paciente = new Paciente();
            for (String propriedade : mascaras.keySet()) {
                // Compila cada expressão regular e procura no texto
                String regexString = mascaras.get(propriedade);
                Pattern pattern = Pattern.compile(regexString, Pattern.MULTILINE);
                Matcher matcher = pattern.matcher(texto2);
                if (matcher.find()) {
                    // Isso é para debugging, para ver o que foi encontrado
                    // Deve ser tirado ao final
                    System.out.println("Linha encontrada: " + matcher.group(0));
                    System.out.println("Propriedade: " + propriedade);
                    // Se encontrou 2 propriedades, imprime os dois valores
                    if (matcher.groupCount() == 2) {
                        System.out.println("Valor1: " + matcher.group(1));
                        System.out.println("Valor2: " + matcher.group(2));
                    } else {
                        // Se encontrou apenas 1 propriedade, imprime o valor
                        System.out.println("Valor1: " + matcher.group(1));
                    }
                    switch (propriedade) {
                        // Para cada mascara, seta a propriedade correspondente no objeto Paciente
                        // Se a mascara tiver 2 grupos, seta os dois valores    
                        case "cns":
                            paciente.cns = matcher.group(1);
                            break;
                        case "prontuario":
                            paciente.prontuario = matcher.group(1);
                            break;
                        case "nomeCompleto":
                            paciente.nomeCompleto = matcher.group(1);
                            paciente.nomeSocial = matcher.group(2);
                            break;
                        case "nomeMae":
                            paciente.nomeMae = matcher.group(1);
                            paciente.nomePai = matcher.group(2);
                            break;
                        default:
                            break;
                    }
                }
            }
            return paciente;
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
