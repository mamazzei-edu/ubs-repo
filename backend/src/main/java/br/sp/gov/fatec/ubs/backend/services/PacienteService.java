package br.sp.gov.fatec.ubs.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import br.sp.gov.fatec.ubs.backend.dtos.AlteracaoCampo;
import br.sp.gov.fatec.ubs.backend.exceptions.CpfDuplicadoException;
import br.sp.gov.fatec.ubs.backend.exceptions.ValidaCPF;
import br.sp.gov.fatec.ubs.backend.model.Paciente;
import br.sp.gov.fatec.ubs.backend.repositories.PacienteRepository;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    // Rótulos dos campos cujo nome em camelCase não vira um título legível sozinho.
    // Os demais são derivados do próprio nome (ver rotulo()).
    private static final Map<String, String> ROTULOS = Map.ofEntries(
            Map.entry("cpf", "CPF"),
            Map.entry("cns", "CNS"),
            Map.entry("rg", "RG"),
            Map.entry("uf", "UF"),
            Map.entry("cep", "CEP"),
            Map.entry("cnh", "CNH"),
            Map.entry("ctps", "CTPS"),
            Map.entry("opm", "Utiliza alguma OPM?"),
            Map.entry("pisPasepNis", "PIS/PASEP/NIS"),
            Map.entry("email", "E-mail"),
            Map.entry("racaCor", "Raça/Cor"),
            Map.entry("nomeMae", "Nome da Mãe"),
            Map.entry("nomePai", "Nome do Pai"),
            Map.entry("dataNascimento", "Data de Nascimento"),
            Map.entry("municipioNascimento", "Município de Nascimento"),
            Map.entry("municipioResidencia", "Município de Residência"),
            Map.entry("tipoLogradouro", "Tipo de Logradouro"),
            Map.entry("origemEndereco", "Origem do Endereço"),
            Map.entry("tituloEleitor", "Título de Eleitor"),
            Map.entry("orgaoEmissor", "Órgão Emissor"),
            Map.entry("situacaoFamiliar", "Situação Familiar"),
            Map.entry("estabelecimentoVinculo", "Estabelecimento de Vínculo"),
            Map.entry("estabelecimentoCadastro", "Estabelecimento de Cadastro"));

    // =====================================================================
    // CPF
    // =====================================================================

    /** Ver ValidaCPF.normalizar — mantido aqui para quem já chamava. */
    public static String normalizarCpf(String cpf) {
        return ValidaCPF.normalizar(cpf);
    }

    /**
     * Normaliza e valida o CPF de um cadastro. O CPF é obrigatório: é ele que
     * impede o mesmo paciente de ser cadastrado duas vezes.
     *
     * @return o CPF somente com dígitos
     * @throws IllegalArgumentException (400) se vazio ou com dígito verificador errado
     */
    public static String validarCpf(String cpf) {
        String normalizado = ValidaCPF.normalizar(cpf);
        if (normalizado == null) {
            throw new IllegalArgumentException("O CPF é obrigatório");
        }
        if (!ValidaCPF.isValid(normalizado)) {
            throw new IllegalArgumentException("CPF inválido: " + cpf.trim());
        }
        return normalizado;
    }

    public Optional<Paciente> buscarPorCpf(String cpf) {
        String normalizado = normalizarCpf(cpf);
        if (normalizado == null) {
            return Optional.empty();
        }
        return pacienteRepository.findFirstByCpfOrderByIdAsc(normalizado);
    }

    /** Busca para autocompletar (agendamento): parte do nome ou começo do CPF. */
    public List<Paciente> buscarPorNomeOuCpf(String termo, int limite) {
        String nome = termo == null ? "" : termo.trim();
        if (nome.isEmpty()) {
            return List.of();
        }
        String cpf = nome.replaceAll("\\D", "");
        return pacienteRepository.buscarPorNomeOuCpf(nome, cpf, PageRequest.of(0, limite));
    }

    // =====================================================================
    // COMPARAÇÃO E CÓPIA DE CAMPOS
    // =====================================================================

    /**
     * Os campos de texto da entidade, descobertos por reflexão.
     *
     * Feito assim de propósito: as listas escritas à mão em editarPaciente e
     * em PacienteController.atualizarPaciente cobriam 20 dos 48 campos, e os
     * outros 28 eram silenciosamente descartados na atualização. Por reflexão,
     * acrescentar um campo na entidade basta — nada mais precisa ser alterado.
     */
    private static List<Field> camposDeTexto() {
        List<Field> campos = new ArrayList<>();
        for (Field campo : Paciente.class.getDeclaredFields()) {
            if (campo.getType() != String.class || Modifier.isStatic(campo.getModifiers())) {
                continue;
            }
            campo.setAccessible(true);
            campos.add(campo);
        }
        return campos;
    }

    private static String texto(Field campo, Paciente paciente) {
        try {
            String valor = (String) campo.get(paciente);
            return valor == null ? null : valor.trim();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Falha ao ler o campo " + campo.getName(), e);
        }
    }

    private static void gravar(Field campo, Paciente paciente, String valor) {
        try {
            campo.set(paciente, valor);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Falha ao gravar o campo " + campo.getName(), e);
        }
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }

    private static String rotulo(String campo) {
        String pronto = ROTULOS.get(campo);
        if (pronto != null) {
            return pronto;
        }
        // nomeCompleto -> "Nome Completo"
        String comEspacos = campo.replaceAll("([a-z])([A-Z])", "$1 $2");
        return Character.toUpperCase(comEspacos.charAt(0)) + comEspacos.substring(1);
    }

    /**
     * O que a ficha muda em relação ao que já está gravado.
     *
     * Campo vazio na ficha não entra na lista: a ficha não apaga dado existente.
     */
    public List<AlteracaoCampo> diferencas(Paciente gravado, Paciente daFicha) {
        List<AlteracaoCampo> alteracoes = new ArrayList<>();
        for (Field campo : camposDeTexto()) {
            String valorNovo = texto(campo, daFicha);
            if (vazio(valorNovo)) {
                continue;
            }
            String valorAtual = texto(campo, gravado);
            if (Objects.equals(valorAtual, valorNovo)) {
                continue;
            }
            alteracoes.add(new AlteracaoCampo(campo.getName(), rotulo(campo.getName()),
                    valorAtual, valorNovo));
        }
        return alteracoes;
    }

    /** Sobrepõe em destino apenas os campos preenchidos de origem. */
    private void aplicarCamposPreenchidos(Paciente destino, Paciente origem) {
        for (Field campo : camposDeTexto()) {
            String valor = texto(campo, origem);
            if (!vazio(valor)) {
                gravar(campo, destino, valor);
            }
        }
    }

    /** Copia todos os campos de origem para destino, inclusive os vazios. */
    private void aplicarTodosOsCampos(Paciente destino, Paciente origem) {
        for (Field campo : camposDeTexto()) {
            gravar(campo, destino, texto(campo, origem));
        }
    }

    /**
     * Monta o objeto que vai para o formulário de conferência: o registro
     * gravado com os campos preenchidos da ficha por cima, carregando o id.
     *
     * Devolve uma instância NOVA de propósito. Alterar a entidade carregada do
     * banco deixaria a gravação à mercê do ciclo de vida do EntityManager —
     * o usuário ainda não confirmou nada neste ponto.
     */
    public Paciente mesclarComFicha(Paciente gravado, Paciente daFicha) {
        Paciente paraConferencia = new Paciente();
        paraConferencia.setId(gravado.getId());
        aplicarTodosOsCampos(paraConferencia, gravado);
        aplicarCamposPreenchidos(paraConferencia, daFicha);
        return paraConferencia;
    }

    // =====================================================================
    // GRAVAÇÃO
    // =====================================================================

    /**
     * Gravação vinda do POST /api/pacientes.
     *
     * SEM id é um cadastro novo: se o CPF já existe, recusa com 409 e devolve
     * o id do paciente já cadastrado. Antes, o POST "atualizava por CPF" — um
     * segundo cadastro com o mesmo CPF sobrescrevia em silêncio todos os
     * campos do paciente existente (inclusive apagando os que vinham vazios),
     * e a tela ainda mostrava "salvo com sucesso".
     *
     * COM id é a confirmação da conferência da ficha em PDF: o upload já
     * identificou o registro pelo CPF e devolveu o id, então é uma edição.
     */
    public Paciente cadastrar(Paciente enviado) {
        if (enviado.getId() != null) {
            Paciente atualizado = editarPaciente(enviado.getId(), enviado);
            if (atualizado == null) {
                throw new IllegalArgumentException("Paciente não encontrado: id " + enviado.getId());
            }
            return atualizado;
        }

        String cpf = validarCpf(enviado.getCpf());
        pacienteRepository.findFirstByCpfOrderByIdAsc(cpf).ifPresent(existente -> {
            throw new CpfDuplicadoException(cpf, existente.getId());
        });

        enviado.setCpf(cpf);
        Paciente novo = new Paciente();
        aplicarTodosOsCampos(novo, enviado);
        return pacienteRepository.save(novo);
    }

    // =====================================================================
    // CONSULTAS E REMOÇÃO
    // =====================================================================

    public List<Paciente> listarPacientes() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> buscarPacientePorId(Long id) {
        return pacienteRepository.findById(id);
    }

    /**
     * Mantido para quem já chamava. Passa pelo mesmo caminho de cadastrar,
     * para não existir rota que duplique CPF.
     */
    public Paciente salvarPaciente(Paciente paciente) {
        return cadastrar(paciente);
    }

    public void excluirPaciente(Long id) {
        pacienteRepository.deleteById(id);
    }

    /**
     * Atualiza o registro de id informado com todos os campos recebidos.
     *
     * Trocar o CPF para um que já pertence a outro paciente fundiria os dois
     * cadastros: recusa com 409 em vez de deixar o índice único devolver 500.
     *
     * @return null se não existir paciente com o id
     */
    public Paciente editarPaciente(Long id, Paciente pacienteAtualizado) {
        Optional<Paciente> existente = pacienteRepository.findById(id);
        if (existente.isEmpty()) {
            return null;
        }

        String cpf = validarCpf(pacienteAtualizado.getCpf());
        pacienteRepository.findFirstByCpfAndIdNotOrderByIdAsc(cpf, id).ifPresent(outro -> {
            throw new CpfDuplicadoException(cpf, outro.getId());
        });

        pacienteAtualizado.setCpf(cpf);
        Paciente paciente = existente.get();
        aplicarTodosOsCampos(paciente, pacienteAtualizado);
        return pacienteRepository.save(paciente);
    }
}
