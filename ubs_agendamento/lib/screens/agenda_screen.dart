import 'package:flutter/material.dart';

import '../core/api_exception.dart';
import '../models/agendamento.dart';
import '../models/status_agendamento.dart';
import '../services/servicos.dart';
import '../util/formatos.dart';
import '../widgets/agendamento_card.dart';
import '../widgets/mensagem_estado.dart';
import '../widgets/status_chip.dart';
import 'login_screen.dart';
import 'novo_agendamento_screen.dart';

/// Agenda de um dia: navegação por dia, filtro por status, confirmar e
/// cancelar, e o botão de novo agendamento.
class AgendaScreen extends StatefulWidget {
  const AgendaScreen({super.key, required this.servicos});

  final Servicos servicos;

  @override
  State<AgendaScreen> createState() => _AgendaScreenState();
}

class _AgendaScreenState extends State<AgendaScreen> {
  DateTime _dia = inicioDoDia(DateTime.now());
  StatusAgendamento? _filtro;
  List<Agendamento> _agendamentos = const [];
  bool _carregando = true;
  String? _erro;

  /// Número da última busca: ao trocar de dia rápido, uma resposta atrasada
  /// do dia anterior não pode sobrescrever a do dia atual.
  int _busca = 0;

  @override
  void initState() {
    super.initState();
    _buscar();
  }

  /// Recarrega mostrando o indicador no lugar da lista.
  Future<void> _carregar() {
    setState(() {
      _carregando = true;
      _erro = null;
    });
    return _buscar();
  }

  /// Busca sem trocar a lista pelo indicador (abertura e "puxar para atualizar").
  Future<void> _buscar() async {
    final busca = ++_busca;
    try {
      final lista = await widget.servicos.agendamentos.listarDoDia(_dia);
      if (!mounted || busca != _busca) return;
      setState(() {
        _agendamentos = lista;
        _carregando = false;
        _erro = null;
      });
    } on ApiException catch (e) {
      if (!mounted || busca != _busca) return;
      setState(() {
        _erro = e.mensagem;
        _carregando = false;
      });
    }
  }

  void _irPara(DateTime dia) {
    setState(() => _dia = inicioDoDia(dia));
    _carregar();
  }

  Future<void> _escolherDia() async {
    final escolhido = await showDatePicker(
      context: context,
      initialDate: _dia,
      firstDate: DateTime(2020),
      lastDate: DateTime.now().add(const Duration(days: 730)),
    );
    if (escolhido != null) _irPara(escolhido);
  }

  Future<void> _novoAgendamento() async {
    final criado = await Navigator.of(context).push<Agendamento>(
      MaterialPageRoute(
        builder: (_) => NovoAgendamentoScreen(servicos: widget.servicos, diaInicial: _dia),
      ),
    );
    if (criado == null || !mounted) return;
    _mensagem('Agendamento criado para ${formatarData(criado.dataHoraConsulta)} às '
        '${formatarHora(criado.dataHoraConsulta)}.');
    _irPara(criado.dataHoraConsulta);
  }

  Future<void> _sair() async {
    final confirmar = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Sair do app?'),
        content: const Text('Será preciso entrar de novo com e-mail e senha.'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Ficar')),
          FilledButton(onPressed: () => Navigator.pop(context, true), child: const Text('Sair')),
        ],
      ),
    );
    if (confirmar != true) return;
    await widget.servicos.auth.sair();
    if (!mounted) return;
    Navigator.of(context).pushAndRemoveUntil(
      MaterialPageRoute<void>(builder: (_) => LoginScreen(servicos: widget.servicos)),
      (_) => false,
    );
  }

  Future<void> _alterarStatus(Agendamento agendamento, {required bool confirmar}) async {
    if (!confirmar) {
      final certeza = await showDialog<bool>(
        context: context,
        builder: (context) => AlertDialog(
          title: const Text('Cancelar agendamento?'),
          content: Text('${agendamento.paciente.nomeCompleto}\n'
              '${formatarData(agendamento.dataHoraConsulta)} às ${formatarHora(agendamento.dataHoraConsulta)}'),
          actions: [
            TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Voltar')),
            FilledButton(
              onPressed: () => Navigator.pop(context, true),
              child: const Text('Cancelar agendamento'),
            ),
          ],
        ),
      );
      if (certeza != true) return;
    }

    try {
      final servico = widget.servicos.agendamentos;
      await (confirmar ? servico.confirmar(agendamento.id) : servico.cancelar(agendamento.id));
      if (!mounted) return;
      _mensagem(confirmar ? 'Agendamento confirmado.' : 'Agendamento cancelado.');
      _buscar();
    } on ApiException catch (e) {
      _mensagem(e.mensagem);
    }
  }

  void _mostrarDetalhes(Agendamento agendamento) {
    showModalBottomSheet<void>(
      context: context,
      showDragHandle: true,
      builder: (contextoFolha) => _DetalhesAgendamento(
        agendamento: agendamento,
        aoConfirmar: () {
          Navigator.pop(contextoFolha);
          _alterarStatus(agendamento, confirmar: true);
        },
        aoCancelar: () {
          Navigator.pop(contextoFolha);
          _alterarStatus(agendamento, confirmar: false);
        },
      ),
    );
  }

  void _mensagem(String texto) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(texto)));
  }

  @override
  Widget build(BuildContext context) {
    final visiveis = _filtro == null ? _agendamentos : _agendamentos.where((a) => a.status == _filtro).toList();

    return Scaffold(
      appBar: AppBar(
        title: const Text('Agenda'),
        actions: [
          IconButton(tooltip: 'Atualizar', onPressed: _carregar, icon: const Icon(Icons.refresh)),
          IconButton(tooltip: 'Sair', onPressed: _sair, icon: const Icon(Icons.logout)),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: _novoAgendamento,
        icon: const Icon(Icons.add),
        label: const Text('Novo agendamento'),
      ),
      body: Column(
        children: [
          _SeletorDeDia(
            dia: _dia,
            aoAnterior: () => _irPara(_dia.subtract(const Duration(days: 1))),
            aoProximo: () => _irPara(_dia.add(const Duration(days: 1))),
            aoEscolher: _escolherDia,
            aoHoje: mesmoDia(_dia, DateTime.now()) ? null : () => _irPara(DateTime.now()),
          ),
          _FiltroStatus(
            selecionado: _filtro,
            agendamentos: _agendamentos,
            aoSelecionar: (status) => setState(() => _filtro = status),
          ),
          const Divider(height: 1),
          Expanded(child: _conteudo(visiveis)),
        ],
      ),
    );
  }

  Widget _conteudo(List<Agendamento> visiveis) {
    if (_carregando) {
      return const Center(child: CircularProgressIndicator());
    }
    if (_erro != null) {
      return Center(
        child: MensagemEstado(
          icone: Icons.cloud_off_outlined,
          titulo: 'Não foi possível carregar a agenda',
          mensagem: _erro,
          erro: true,
          acao: _carregar,
          rotuloAcao: 'Tentar novamente',
        ),
      );
    }

    // RefreshIndicator exige algo rolável mesmo quando vazio.
    return RefreshIndicator(
      onRefresh: _buscar,
      child: visiveis.isEmpty
          ? ListView(
              children: [
                MensagemEstado(
                  icone: Icons.event_available_outlined,
                  titulo: _filtro == null ? 'Nenhum agendamento neste dia' : 'Nenhum agendamento ${_filtro!.rotulo.toLowerCase()}',
                  mensagem: 'Toque em "Novo agendamento" para marcar uma consulta.',
                ),
              ],
            )
          : ListView.builder(
              padding: const EdgeInsets.only(top: 8, bottom: 96),
              itemCount: visiveis.length,
              itemBuilder: (context, indice) => AgendamentoCard(
                agendamento: visiveis[indice],
                onTap: () => _mostrarDetalhes(visiveis[indice]),
              ),
            ),
    );
  }
}

class _SeletorDeDia extends StatelessWidget {
  const _SeletorDeDia({
    required this.dia,
    required this.aoAnterior,
    required this.aoProximo,
    required this.aoEscolher,
    required this.aoHoje,
  });

  final DateTime dia;
  final VoidCallback aoAnterior;
  final VoidCallback aoProximo;
  final VoidCallback aoEscolher;
  final VoidCallback? aoHoje;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(4, 8, 4, 0),
      child: Row(
        children: [
          IconButton(tooltip: 'Dia anterior', onPressed: aoAnterior, icon: const Icon(Icons.chevron_left)),
          Expanded(
            child: TextButton.icon(
              onPressed: aoEscolher,
              icon: const Icon(Icons.calendar_month_outlined),
              label: Text(rotuloDoDia(dia), overflow: TextOverflow.ellipsis),
            ),
          ),
          if (aoHoje != null) TextButton(onPressed: aoHoje, child: const Text('Hoje')),
          IconButton(tooltip: 'Próximo dia', onPressed: aoProximo, icon: const Icon(Icons.chevron_right)),
        ],
      ),
    );
  }
}

class _FiltroStatus extends StatelessWidget {
  const _FiltroStatus({required this.selecionado, required this.agendamentos, required this.aoSelecionar});

  final StatusAgendamento? selecionado;
  final List<Agendamento> agendamentos;
  final ValueChanged<StatusAgendamento?> aoSelecionar;

  @override
  Widget build(BuildContext context) {
    int quantos(StatusAgendamento status) => agendamentos.where((a) => a.status == status).length;

    return SizedBox(
      height: 56,
      child: ListView(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        children: [
          ChoiceChip(
            label: Text('Todos (${agendamentos.length})'),
            selected: selecionado == null,
            onSelected: (_) => aoSelecionar(null),
          ),
          for (final status in StatusAgendamento.values)
            Padding(
              padding: const EdgeInsets.only(left: 8),
              child: ChoiceChip(
                label: Text('${status.rotulo} (${quantos(status)})'),
                selected: selecionado == status,
                onSelected: (_) => aoSelecionar(status),
              ),
            ),
        ],
      ),
    );
  }
}

class _DetalhesAgendamento extends StatelessWidget {
  const _DetalhesAgendamento({required this.agendamento, required this.aoConfirmar, required this.aoCancelar});

  final Agendamento agendamento;
  final VoidCallback aoConfirmar;
  final VoidCallback aoCancelar;

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    final observacoes = agendamento.observacoes;

    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.fromLTRB(24, 0, 24, 24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(agendamento.paciente.nomeCompleto, style: tema.textTheme.titleLarge),
            if (agendamento.paciente.cpf != null)
              Text('CPF ${mascararCpf(agendamento.paciente.cpf)}', style: tema.textTheme.bodyMedium),
            const SizedBox(height: 12),
            StatusChip(status: agendamento.status),
            const SizedBox(height: 12),
            _Linha(
              icone: Icons.schedule,
              texto: '${formatarData(agendamento.dataHoraConsulta)} às ${formatarHora(agendamento.dataHoraConsulta)}',
            ),
            _Linha(icone: Icons.medical_services_outlined, texto: agendamento.tipoConsulta),
            _Linha(
              icone: Icons.person_outline,
              texto: 'Dr(a). ${agendamento.medico.nomeCompleto} · ${agendamento.medico.especialidade}',
            ),
            if (observacoes != null && observacoes.isNotEmpty) _Linha(icone: Icons.notes, texto: observacoes),
            const SizedBox(height: 20),
            Row(
              children: [
                if (agendamento.status.podeCancelar)
                  Expanded(
                    child: OutlinedButton.icon(
                      onPressed: aoCancelar,
                      icon: const Icon(Icons.cancel_outlined),
                      label: const Text('Cancelar'),
                    ),
                  ),
                if (agendamento.status.podeCancelar && agendamento.status.podeConfirmar) const SizedBox(width: 12),
                if (agendamento.status.podeConfirmar)
                  Expanded(
                    child: FilledButton.icon(
                      onPressed: aoConfirmar,
                      icon: const Icon(Icons.check_circle_outline),
                      label: const Text('Confirmar'),
                    ),
                  ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _Linha extends StatelessWidget {
  const _Linha({required this.icone, required this.texto});

  final IconData icone;
  final String texto;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(icone, size: 20, color: Theme.of(context).colorScheme.onSurfaceVariant),
          const SizedBox(width: 12),
          Expanded(child: Text(texto)),
        ],
      ),
    );
  }
}
