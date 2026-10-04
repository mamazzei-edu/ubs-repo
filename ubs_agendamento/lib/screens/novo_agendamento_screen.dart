import 'dart:async';

import 'package:flutter/material.dart';

import '../core/api_exception.dart';
import '../models/agendamento.dart';
import '../models/medico.dart';
import '../models/paciente.dart';
import '../models/tipos_consulta.dart';
import '../services/servicos.dart';
import '../tema.dart';
import '../util/formatos.dart';
import '../widgets/campo_selecao.dart';

/// Formulário de novo agendamento — os mesmos campos da tela web:
/// paciente, tipo de consulta, médico (filtrado pelo tipo), data, hora e
/// observações. Ao salvar, devolve o [Agendamento] criado com Navigator.pop.
class NovoAgendamentoScreen extends StatefulWidget {
  const NovoAgendamentoScreen({super.key, required this.servicos, required this.diaInicial});

  final Servicos servicos;

  /// Dia aberto na agenda; vira a data sugerida (se não for passado).
  final DateTime diaInicial;

  @override
  State<NovoAgendamentoScreen> createState() => _NovoAgendamentoScreenState();
}

class _NovoAgendamentoScreenState extends State<NovoAgendamentoScreen> {
  static const _tamanhoMinimoBusca = 3;
  static const _limiteObservacoes = 500; // coluna "observacoes" tem 500 caracteres

  final _formulario = GlobalKey<FormState>();

  // Paciente
  final _busca = TextEditingController();
  Timer? _atrasoBusca;
  bool _buscando = false;
  bool _buscou = false;
  String? _erroBusca;
  List<Paciente> _resultados = const [];
  Paciente? _paciente;

  // Consulta
  String? _tipo;
  List<Medico> _medicos = const [];
  bool _carregandoMedicos = true;
  String? _erroMedicos;
  Medico? _medico;
  late DateTime _data;
  TimeOfDay? _hora;
  final _observacoes = TextEditingController();

  bool _salvando = false;
  String? _erro;

  @override
  void initState() {
    super.initState();
    final hoje = inicioDoDia(DateTime.now());
    _data = widget.diaInicial.isBefore(hoje) ? hoje : inicioDoDia(widget.diaInicial);
    _carregarMedicos();
  }

  @override
  void dispose() {
    _atrasoBusca?.cancel();
    _busca.dispose();
    _observacoes.dispose();
    super.dispose();
  }

  // ---------------------------------------------------------------- médicos

  Future<void> _carregarMedicos() async {
    try {
      final medicos = await widget.servicos.cadastros.listarMedicosAtivos();
      if (!mounted) return;
      setState(() {
        _medicos = medicos;
        _carregandoMedicos = false;
        _erroMedicos = null;
      });
    } on ApiException catch (e) {
      if (!mounted) return;
      setState(() {
        _carregandoMedicos = false;
        _erroMedicos = e.mensagem;
      });
    }
  }

  void _tentarMedicosDeNovo() {
    setState(() {
      _carregandoMedicos = true;
      _erroMedicos = null;
    });
    _carregarMedicos();
  }

  void _escolherTipo(String tipo) {
    setState(() {
      _tipo = tipo;
      _erro = null;
      // O médico escolhido antes pode não atender o novo tipo.
      if (_medico != null && !medicosParaTipo(_medicos, tipo).contains(_medico)) {
        _medico = null;
      }
    });
  }

  // --------------------------------------------------------------- paciente

  /// Espera o usuário parar de digitar antes de consultar a API.
  void _aoDigitarBusca(String texto) {
    _atrasoBusca?.cancel();
    final termo = texto.trim();
    if (termo.length < _tamanhoMinimoBusca) {
      setState(() {
        _resultados = const [];
        _buscando = false;
        _buscou = false;
        _erroBusca = null;
      });
      return;
    }
    setState(() => _buscando = true);
    _atrasoBusca = Timer(const Duration(milliseconds: 400), () => _buscarPacientes(termo));
  }

  Future<void> _buscarPacientes(String termo) async {
    try {
      final pacientes = await widget.servicos.cadastros.buscarPacientes(termo);
      // Resposta de uma busca antiga: o texto já mudou.
      if (!mounted || termo != _busca.text.trim()) return;
      setState(() {
        _resultados = pacientes;
        _buscando = false;
        _buscou = true;
        _erroBusca = null;
      });
    } on ApiException catch (e) {
      if (!mounted) return;
      setState(() {
        _buscando = false;
        _erroBusca = e.mensagem;
      });
    }
  }

  void _escolherPaciente(Paciente? paciente) {
    setState(() {
      _paciente = paciente;
      _erro = null;
      if (paciente == null) {
        _busca.clear();
        _resultados = const [];
        _buscou = false;
      }
    });
  }

  // ------------------------------------------------------------- data e hora

  Future<void> _escolherData() async {
    final hoje = inicioDoDia(DateTime.now());
    final escolhida = await showDatePicker(
      context: context,
      initialDate: _data,
      firstDate: hoje,
      lastDate: hoje.add(const Duration(days: 365)),
      helpText: 'Data da consulta',
    );
    if (escolhida != null) {
      setState(() {
        _data = escolhida;
        _erro = null;
      });
    }
  }

  Future<void> _escolherHora() async {
    final escolhida = await showTimePicker(
      context: context,
      initialTime: _hora ?? const TimeOfDay(hour: 8, minute: 0),
      helpText: 'Hora da consulta',
      builder: (context, child) => MediaQuery(
        data: MediaQuery.of(context).copyWith(alwaysUse24HourFormat: true),
        child: child!,
      ),
    );
    // A mensagem de "horário ocupado" deixa de valer quando a hora muda.
    if (escolhida != null) {
      setState(() {
        _hora = escolhida;
        _erro = null;
      });
    }
  }

  // ------------------------------------------------------------------ salvar

  Future<void> _salvar() async {
    setState(() => _erro = null);
    if (!_formulario.currentState!.validate()) return;

    final dataHora = DateTime(_data.year, _data.month, _data.day, _hora!.hour, _hora!.minute);
    if (!dataHora.isAfter(DateTime.now())) {
      setState(() => _erro = 'Escolha uma data e hora futuras.');
      return;
    }

    setState(() => _salvando = true);
    try {
      final servico = widget.servicos.agendamentos;
      // Mesmo fluxo da tela web: confere antes para dar uma mensagem clara.
      // O backend confere de novo ao gravar.
      if (!await servico.medicoDisponivel(_medico!.id, dataHora)) {
        _falhar('Dr(a). ${_medico!.nomeCompleto} já tem consulta neste horário. Escolha outro.');
        return;
      }
      final criado = await servico.criar(NovoAgendamento(
        pacienteId: _paciente!.id,
        medicoId: _medico!.id,
        dataHoraConsulta: dataHora,
        tipoConsulta: _tipo!,
        observacoes: _observacoes.text.trim(),
      ));
      if (!mounted) return;
      Navigator.of(context).pop(criado);
    } on ApiException catch (e) {
      _falhar(e.mensagem);
    }
  }

  void _falhar(String mensagem) {
    if (!mounted) return;
    setState(() {
      _salvando = false;
      _erro = mensagem;
    });
  }

  // ------------------------------------------------------------------- tela

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    final medicosDoTipo = medicosParaTipo(_medicos, _tipo);

    return Scaffold(
      appBar: AppBar(title: const Text('Novo agendamento')),
      body: SafeArea(
        child: Form(
          key: _formulario,
          child: ListView(
            padding: const EdgeInsets.fromLTRB(16, 16, 16, 32),
            children: [
              const _Secao(titulo: 'Paciente', icone: Icons.person_outline),
              if (_paciente == null) ..._campoBuscaPaciente(tema) else _pacienteEscolhido(tema),
              const SizedBox(height: 24),
              const _Secao(titulo: 'Consulta', icone: Icons.medical_services_outlined),
              CampoSelecao<String>(
                rotulo: 'Tipo de consulta',
                icone: Icons.category_outlined,
                opcoes: tiposConsulta,
                selecionado: _tipo,
                textoDaOpcao: (tipo) => tipo,
                aoSelecionar: _escolherTipo,
                habilitado: !_salvando,
                validator: (valor) => valor == null ? 'Escolha o tipo de consulta.' : null,
              ),
              const SizedBox(height: 16),
              CampoSelecao<Medico>(
                rotulo: 'Médico',
                icone: Icons.badge_outlined,
                opcoes: medicosDoTipo,
                selecionado: _medico,
                textoDaOpcao: (medico) => 'Dr(a). ${medico.nomeCompleto}',
                subtituloDaOpcao: (medico) => '${medico.especialidade} · CRM ${medico.crm}',
                aoSelecionar: (medico) => setState(() {
                  _medico = medico;
                  _erro = null;
                }),
                habilitado: !_carregandoMedicos && !_salvando,
                ajuda: _carregandoMedicos
                    ? 'Carregando médicos…'
                    : _erroMedicos ?? (_tipo == null ? 'Escolha o tipo para filtrar pela especialidade.' : null),
                mensagemSemOpcoes: _tipo == null
                    ? 'Nenhum médico ativo cadastrado.'
                    : 'Nenhum médico ativo atende "$_tipo".',
                validator: (valor) => valor == null ? 'Escolha o médico.' : null,
              ),
              if (_erroMedicos != null)
                Align(
                  alignment: Alignment.centerLeft,
                  child: TextButton.icon(
                    onPressed: _tentarMedicosDeNovo,
                    icon: const Icon(Icons.refresh),
                    label: const Text('Carregar médicos de novo'),
                  ),
                ),
              const SizedBox(height: 16),
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Expanded(
                    child: CampoAcao(
                      rotulo: 'Data',
                      icone: Icons.event_outlined,
                      valor: formatarData(_data),
                      aoTocar: _salvando ? () {} : _escolherData,
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: CampoAcao(
                      rotulo: 'Hora',
                      icone: Icons.schedule,
                      valor: _hora?.format24h(),
                      aoTocar: _salvando ? () {} : _escolherHora,
                      validator: (valor) => valor == null ? 'Escolha a hora.' : null,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _observacoes,
                enabled: !_salvando,
                maxLines: 3,
                maxLength: _limiteObservacoes,
                textCapitalization: TextCapitalization.sentences,
                decoration: decoracaoCampo('Observações (opcional)', icone: Icons.notes),
              ),
              if (_erro != null) ...[
                const SizedBox(height: 8),
                Card(
                  color: tema.colorScheme.errorContainer,
                  child: Padding(
                    padding: const EdgeInsets.all(12),
                    child: Text(_erro!, style: TextStyle(color: tema.colorScheme.onErrorContainer)),
                  ),
                ),
              ],
              const SizedBox(height: 16),
              FilledButton.icon(
                onPressed: _salvando ? null : _salvar,
                icon: _salvando
                    ? const SizedBox.square(dimension: 18, child: CircularProgressIndicator(strokeWidth: 2))
                    : const Icon(Icons.check),
                label: Text(_salvando ? 'Agendando…' : 'Agendar consulta'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  List<Widget> _campoBuscaPaciente(ThemeData tema) => [
        TextFormField(
          controller: _busca,
          enabled: !_salvando,
          autocorrect: false,
          textInputAction: TextInputAction.search,
          onChanged: _aoDigitarBusca,
          decoration: decoracaoCampo(
            'Buscar paciente',
            icone: Icons.search,
            dica: 'Nome ou CPF',
            ajuda: 'Digite ao menos $_tamanhoMinimoBusca letras do nome ou números do CPF.',
            sufixo: _buscando
                ? const Padding(
                    padding: EdgeInsets.all(14),
                    child: SizedBox.square(dimension: 18, child: CircularProgressIndicator(strokeWidth: 2)),
                  )
                : null,
          ),
          validator: (_) => _paciente == null ? 'Selecione o paciente na lista.' : null,
        ),
        if (_erroBusca != null)
          Padding(
            padding: const EdgeInsets.only(top: 8),
            child: Text(_erroBusca!, style: TextStyle(color: tema.colorScheme.error)),
          )
        else if (_buscou && _resultados.isEmpty && !_buscando)
          const Padding(
            padding: EdgeInsets.only(top: 8),
            child: Text('Nenhum paciente encontrado. O cadastro de pacientes é feito no sistema web.'),
          )
        else if (_resultados.isNotEmpty)
          Card(
            margin: const EdgeInsets.only(top: 8),
            child: Column(
              children: [
                for (final paciente in _resultados)
                  ListTile(
                    leading: const Icon(Icons.person_outline),
                    title: Text(paciente.nomeCompleto),
                    subtitle: Text(paciente.cpf == null ? 'Sem CPF' : 'CPF ${mascararCpf(paciente.cpf)}'),
                    onTap: () => _escolherPaciente(paciente),
                  ),
              ],
            ),
          ),
      ];

  Widget _pacienteEscolhido(ThemeData tema) => Card(
        color: tema.colorScheme.secondaryContainer,
        margin: EdgeInsets.zero,
        child: ListTile(
          leading: const Icon(Icons.person),
          title: Text(_paciente!.nomeCompleto),
          subtitle: Text(_paciente!.cpf == null ? 'Sem CPF' : 'CPF ${mascararCpf(_paciente!.cpf)}'),
          trailing: IconButton(
            tooltip: 'Trocar paciente',
            icon: const Icon(Icons.close),
            onPressed: _salvando ? null : () => _escolherPaciente(null),
          ),
        ),
      );
}

class _Secao extends StatelessWidget {
  const _Secao({required this.titulo, required this.icone});

  final String titulo;
  final IconData icone;

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Row(
        children: [
          Icon(icone, size: 20, color: tema.colorScheme.primary),
          const SizedBox(width: 8),
          Text(titulo, style: tema.textTheme.titleMedium?.copyWith(color: tema.colorScheme.primary)),
        ],
      ),
    );
  }
}

extension on TimeOfDay {
  /// "08:30" — independente da preferência 12/24h do aparelho.
  String format24h() => '${hour.toString().padLeft(2, '0')}:${minute.toString().padLeft(2, '0')}';
}
