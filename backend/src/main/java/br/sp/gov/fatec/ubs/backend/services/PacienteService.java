package br.sp.gov.fatec.ubs.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.sp.gov.fatec.ubs.backend.dtos.AlteracaoCampo;
import br.sp.gov.fatec.ubs.backend.exceptions.CpfDuplicadoException;
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

    /**
     * Reduz o CPF a somente dígitos, e devolve null quando não sobra nada.
     *
     * Os dois pontos importam para a restrição de unicidade: sem normalizar,
     * "123.456.789-00" e "12345678900" seriam dois registros distintos; e o
     * MySQL aceita vários NULL num índice único, mas não várias strings vazias.
     */
    public static String normalizarCpf(String cpf) {
        if (cpf == null) {
            return null;
        }
        String digitos = cpf.replaceAll("\\D", "");
        return digitos.isEmpty() ? null : digitos;
    }

    public Optional<Paciente> buscarPorCpf(String cpf) {
        String normalizado = normalizarCpf(cpf);
        if (normalizado == null) {
            return Optional.empty();
        }
        return pacienteRepository.findByCpf(normalizado);
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
     * Caminho único de gravação: é o que garante CPF sem duplicata.
     *
     * Resolve o destino nesta ordem — id informado, CPF já existente, registro
     * novo — e grava sempre com o CPF normalizado.
     */
    public Paciente salvarOuAtualizarPorCpf(Paciente enviado) {
        enviado.setCpf(normalizarCpf(enviado.getCpf()));

        Paciente porCpf = enviado.getCpf() == null ? null
                : pacienteRepository.findByCpf(enviado.getCpf()).orElse(null);

        Paciente destino = null;
        if (enviado.getId() != null) {
            destino = pacienteRepository.findById(enviado.getId()).orElse(null);
        }

        // Editar o CPF de um registro para um que ja pertence a outro paciente
        // fundiria os dois. Melhor recusar com 409 do que perder dados.
        if (destino != null && porCpf != null && !porCpf.getId().equals(destino.getId())) {
            throw new CpfDuplicadoException(enviado.getCpf());
        }

        if (destino == null) {
            destino = porCpf != null ? porCpf : new Paciente();
        }

        aplicarTodosOsCampos(destino, enviado);
        return pacienteRepository.save(destino);
    }

    /** true quando a gravação atualizou um registro que já existia. */
    public boolean jaExistia(Paciente enviado) {
        if (enviado.getId() != null && pacienteRepository.existsById(enviado.getId())) {
            return true;
        }
        return buscarPorCpf(enviado.getCpf()).isPresent();
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
     * Mantido para quem já chamava. Passa pelo mesmo caminho de
     * salvarOuAtualizarPorCpf, para não existir rota que duplique CPF.
     */
    public Paciente salvarPaciente(Paciente paciente) {
        return salvarOuAtualizarPorCpf(paciente);
    }

    public void excluirPaciente(Long id) {
        pacienteRepository.deleteById(id);
    }

    /** Atualiza o registro de id informado com todos os campos recebidos. */
    public Paciente editarPaciente(Long id, Paciente pacienteAtualizado) {
        Optional<Paciente> existente = pacienteRepository.findById(id);
        if (existente.isEmpty()) {
            return null;
        }
        Paciente paciente = existente.get();
        pacienteAtualizado.setCpf(normalizarCpf(pacienteAtualizado.getCpf()));
        aplicarTodosOsCampos(paciente, pacienteAtualizado);
        return pacienteRepository.save(paciente);
    }
}
