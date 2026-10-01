package br.sp.gov.fatec.ubs.backend.services;

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

import br.sp.gov.fatec.ubs.backend.armazenamento.ExtraiTextoPDF;
import br.sp.gov.fatec.ubs.backend.configs.ArmazenamentoPropriedades;
import br.sp.gov.fatec.ubs.backend.exceptions.ArmazenamentoException;
import br.sp.gov.fatec.ubs.backend.exceptions.ArmazenamentoFileNotFoundException;
import br.sp.gov.fatec.ubs.backend.model.Paciente;

@Service
public class ArmazenamentoSistemaDeArquivoService implements ArmazenamentoService {

    private final Path localArmazenamento;

    @Autowired
    public ArmazenamentoSistemaDeArquivoService(ArmazenamentoPropriedades armazenamentoPropriedades) {

        if (armazenamentoPropriedades.getLocalArmazenamento().trim().length() == 0) {
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
            if (!destino.getParent().equals(this.localArmazenamento.toAbsolutePath())) {
                throw new ArmazenamentoException("Não é permitido armazenar fora do diretório de armazenamento "
                        + arquivo.getOriginalFilename());
            }
            try (InputStream entrada = arquivo.getInputStream()) {
                Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
            }
            String texto2 = ExtraiTextoPDF.extraiTextoPDFiText(destino.toString());
            // A extracao de PDF produz NBSP (\u00A0) e outros espacos Unicode que
            // nem \s nem [ \t] reconhecem, e que por isso entravam nos valores
            // capturados. Normalizando aqui, TODAS as mascaras abaixo passam a
            // funcionar, e o trim() dos valores volta a ter efeito.
            texto2 = texto2.replaceAll("\\h", " ");
            // System.out.println("Itext:");
            // System.out.println(texto2);
            // mascaras é um dicionário que armazena as expressões regulares
            // e os nomes das propriedades correspondentes
            // Exemplo: mascaras.put("nomeMae", "Nome da Mãe: (.*)");
            HashMap<String, String> mascaras = new HashMap<String, String>();
            mascaras.put("serieProntuario", "^([A-Z]\\-[0-9]{4})$");
            // Coincide com valores começando com "CNS" captura o valor com (.*) em grupo1
            mascaras.put("cns", "^CNS\\h*:\\h*(.*)$");
            // Coincide com valores terminando com "-CSE GERALDO DE PAULA SOUZA" captura o
            // valor com (\\d+*) em grupo1
            mascaras.put("prontuario", "^(\\d+)\\h*-CSE GERALDO DE PAULA SOUZA$");
            // Coincide com valores começando com "Usuário:" captura o valor com (.*) em
            // grupo1
            // seguida por espaço \\s* e Nome Social: e se existir algum valor após (.*?)
            // coloca e
            // em grupo2
            // Aqui é necessário colocar a ? após o * para que a expressão não consuma a
            // próxima linha
            mascaras.put("nomeCompleto", "^Usuário:\\h*(.*?)\\h*Nome Social:\\h*(.*?)$");
            // Coincide com valores começando com "Mãe:" captura o valor com (.*) em grupo1
            // seguida por espaço \\s* Pai: e se existir algum valor após (.*?) colocar
            // em grupo2
            mascaras.put("nomeMae", "^Mãe:\\h*(.*?)\\h*Pai:\\h*(.*?)$");
            // Adicione mascaras para cada um dos valores adicionais que você deseja extrair
            mascaras.put("nascimento", "^Nascimento:\\h*(.*?)\\h*Sexo:\\h*(.*?)$");
            // mascaras.put("nacionalidade", "^Nacionalidade:\\s*([^\\r\\n]+)$");
            // mascaras.put("municipioNascimento", "^Munic[ií]pio de
            // Nascimento:\\s*([^\\r\\n]+)$");
            mascaras.put("nacionalidade", "^Nacionalidade:\\h*(.*?)\\h*Munic[ií]pio de Nascimento:\\h*(.*?)$");

            mascaras.put("racaCorEtnia", "^Raça/Cor:\\h*(.*?)\\h*Etnia:\\h*(.*)$");
            mascaras.put("frequentaEscolaEscolaridade", "^Frequenta Escola\\?:\\h*(Sim|Não)\\h*Escolaridade:\\h*(.*)$");
            // mascaras.put("situacaoFamiliar", "^Situação Familiar:\\s*(.*)$");
            // mascaras.put("ocupacao", "^Ocupação:\\s*(.*)$");
            mascaras.put("situacaoFamiliar", "^Situação Familiar:\\h*(.*?)\\h*Ocupação:\\h*(.*?)$");

            mascaras.put("estabelecimentoVinculoCadastro",
                    "^Estabelecimento de Vínculo:\\h*(.*?)\\h*Estabelecimento de Cadastro:\\h*(.*?)$");

            mascaras.put("deficiente", "^Pessoa com Deficiência:\\h*(Sim|Não)$");
            mascaras.put("telefones", "^Telefone Celular:\\h*(.*?)\\h*Telefone Residencial:\\h*(.*?)$");

            // Origem do Endereço + CEP
            mascaras.put("origemEnderecoCep", "^Origem do Endere[cç]o:\\h*(.*?)\\h+CEP:\\h*(\\d{5}-?\\d{3})$");
            // mascaras.put("municipioDistrito", "^Munic[ií]pio de
            // Resid[êe]ncia:\\s*(.*?)\\s*Distrito Administrativo:\\s*(.*?)$");

            // Município de Residência + Distrito Administrativo
            // Mesmo formato do email: \h no lugar de [ \t], captura preguicosa e
            // segundo campo opcional. grupo 1 = municipio, grupo 2 = distrito (pode ser null).
            mascaras.put("municipioDistrito",
                    "(?iu)^\\h*Munic[ií]pio de Resid[êe]ncia\\h*:\\h*(.*?)\\h*(?:Distrito Administrativo\\h*:\\h*(.*?)\\h*)?$");
            mascaras.put("tipoLogradouroLogradouro", "^Tipo Logradouro:\\h*(.*?)\\h*Logradouro:\\h*(.*?)$");
            mascaras.put("numeroBairro", "^Número:\\h*(.*?)\\h*Bairro:\\h*(.*?)$");
            mascaras.put("complemento", "^Complemento:\\h*(.*)$");
            mascaras.put("referencia", "^Refer[êe]ncia:\\h*(.*)$");

            mascaras.put("telefoneComercial", "^Telefone Comercial:\\h*(.*)$");
            // (?i) ignora caixa; (?u) trata acentos; \h cobre os espacos que o PDF
            // produz (tab, NBSP \u00A0 e afins), que [ \t] deixava entrar no valor.
            // O rotulo usa E-?mail SEM grupo capturante: com "(E-mail|Email)" o grupo 1
            // era o proprio rotulo, e o e-mail caia no grupo 2.
            // (.*?) preguicoso descarta o preenchimento da celula, e o trecho do Contato
            // e opcional para a linha ainda casar quando ele vem vazio ou em outra linha.
            // grupo 1 = e-mail, grupo 2 = contato (pode ser null).
            mascaras.put("email", "(?iu)^\\h*E-?mail\\h*:\\h*(.*?)\\h*(?:Contato\\h*:\\h*(.*?)\\h*)?$");

            mascaras.put("uf", "^UF:\\h*(\\w{2})$");
            mascaras.put("rg", "(?is)Identidade.*?N\\S*mero\\s*:\\s*(\\d+)(?=\\s*(?:Data|$))");
            mascaras.put("orgaoEmissorUf", "^Órgão Emissor:\\h*(.*?)\\h+UF:\\h*(\\w{2})$");
            // mascaras.put("cpf",
            // "(^CPF:\\s*(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$)||(^CPF:\\s*(\\d{11})$)");
            mascaras.put("cpf", "(?is)CPF.*?N\\S*mero\\s*:\\s*(\\d+)$");

            mascaras.put("pisPasepNis", "^PIS/PASEP/NIS:\\h*(.*)$");
            mascaras.put("cnh", "^CNH:\\h*(.*)$");
            mascaras.put("ctps", "^CTPS:\\h*(.*)$");
            mascaras.put("tituloEleitor", "^Título de Eleitor:\\h*(.*)$");
            mascaras.put("passaporte", "^Passaporte:\\h*(.*)$");

            Paciente paciente = new Paciente();
            String serieProntuario = "";
            String regexString = mascaras.get("serieProntuario");
            Pattern pattern = Pattern.compile(regexString, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(texto2);
            if (matcher.find()) {
                // Isso é para debugging, para ver o que foi encontrado
                // Deve ser tirado ao final
                System.out.println("Linha encontrada agora: " + matcher.group(0));
                System.out.println("Propriedade: " + "serieProntuario");
                // Se encontrou 2 propriedades, imprime os dois valores
                if (matcher.groupCount() == 2) {
                    System.out.println("Valor1: " + matcher.group(1));
                    System.out.println("Valor2: " + matcher.group(2));
                } else {
                    // Se encontrou apenas 1 propriedade, imprime o valor
                    System.out.println("Valor1: " + matcher.group(1));
                }
                serieProntuario = matcher.group(1);
            }
            for (String propriedade : mascaras.keySet()) {
                // Compila cada expressão regular e procura no texto
                regexString = mascaras.get(propriedade);
                pattern = Pattern.compile(regexString, Pattern.MULTILINE);
                matcher = pattern.matcher(texto2);
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
                            paciente.setCns(matcher.group(1));
                            break;
                        case "prontuario":
                            paciente.setProntuario(serieProntuario + "-" + matcher.group(1));
                            break;
                        case "nomeCompleto":
                            paciente.setNomeCompleto(matcher.group(1));
                            paciente.setNomeSocial(matcher.group(2));
                            break;
                        case "nomeMae":
                            paciente.setNomeMae(matcher.group(1));
                            paciente.setNomePai(matcher.group(2));
                            break;

                        case "nascimento":
                            String dataTexto = matcher.group(1).trim();
                            paciente.setDataNascimento(dataTexto);
                            System.out.println(paciente.getDataNascimento());
                            paciente.setSexo(matcher.group(2).trim());
                            break;

                        case "nacionalidade":
                            paciente.setNacionalidade(matcher.group(1));
                            paciente.setMunicipioNascimento(matcher.group(2));
                            break;

                        case "municipioDistrito":
                            paciente.setMunicipioResidencia(matcher.group(1));
                            paciente.setDistritoAdministrativo(matcher.group(2));
                            break;

                        case "racaCorEtnia":
                            paciente.setRacaCor(matcher.group(1));
                            paciente.setEtnia(matcher.group(2));
                            break;

                        case "frequentaEscolaEscolaridade":
                            paciente.setFrequentaEscola(matcher.group(1));
                            paciente.setEscolaridade(matcher.group(2));
                            break;

                        case "situacaoFamiliar":
                            paciente.setSituacaoFamiliar(matcher.group(1));
                            paciente.setOcupacao(matcher.group(2));
                            break;

                        case "estabelecimentoVinculoCadastro":
                            paciente.setEstabelecimentoVinculo(matcher.group(1));
                            paciente.setEstabelecimentoCadastro(matcher.group(2));
                            break;

                        case "deficiente":
                            paciente.setDeficiente(matcher.group(1));
                            break;

                        case "visual":
                            paciente.setVisual(matcher.group(1));
                            break;

                        case "auditiva":
                            paciente.setAuditiva(matcher.group(1));
                            break;
                        case "motora":
                            paciente.setMotora(matcher.group(1));
                            break;
                        case "intelectual":
                            paciente.setIntelectual(matcher.group(1));
                            break;

                        case "telefones":
                            paciente.setTelefoneCelular(matcher.group(1));
                            paciente.setTelefoneResidencial(matcher.group(2));
                            break;

                        case "telefoneComercial":
                            paciente.setTelefoneComercial(matcher.group(1));
                            break;

                        case "email":
                            paciente.setEmail(matcher.group(1));
                            // O PDF traz "Contato:" na celula ao lado; o grupo 2 pode ser
                            // null quando a celula vem vazia ou em outra linha.
                            paciente.setContato(matcher.group(2));
                            break;

                        case "complemento":
                            paciente.setComplemento(matcher.group(1));
                            break;

                        case "referencia":
                            paciente.setReferencia(matcher.group(1));
                            break;

                        case "origemEnderecoCep":
                            paciente.setOrigemEndereco(matcher.group(1));
                            paciente.setCep(matcher.group(2));
                            break;

                        case "tipoLogradouroLogradouro":
                            paciente.setTipoLogradouro(matcher.group(1));
                            paciente.setLogradouro(matcher.group(2));
                            break;

                        case "numeroBairro":
                            paciente.setNumero(matcher.group(1));
                            paciente.setBairro(matcher.group(2));
                            break;

                        case "cpf":
                            paciente.setCpf(matcher.group(1));
                            break;

                        case "orgaoEmissorUf":
                            paciente.setOrgaoEmissor(matcher.group(1));
                            paciente.setUf(matcher.group(2));
                            break;

                        case "rg":
                            paciente.setRg(matcher.group(1));
                            break;

                        case "pisPasepNis":
                            paciente.setPisPasepNis(matcher.group(1));
                            break;

                        case "cnh":
                            paciente.setCnh(matcher.group(1));
                            break;

                        case "ctps":

                            paciente.setCtps(matcher.group(1));
                            break;

                        case "tituloEleitor":
                            paciente.setTituloEleitor(matcher.group(1));
                            break;

                        case "passaporte":
                            paciente.setPassaporte(matcher.group(1));
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
            return Files.walk(this.localArmazenamento, 1).filter(path -> !path.equals(this.localArmazenamento))
                    .map(this.localArmazenamento::relativize);
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
