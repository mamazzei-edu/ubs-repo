import 'package:flutter/material.dart';

import '../services/servicos.dart';
import '../widgets/mensagem_estado.dart';
import 'agenda_screen.dart';
import 'login_screen.dart';
import 'servidor_screen.dart';

/// Primeira tela. Antes de qualquer coisa, confirma que o app alcança a API e
/// que a API alcança o banco de dados (GET /status). Só então segue para o
/// login — ou direto para a agenda, se ainda houver sessão válida.
class AberturaScreen extends StatefulWidget {
  const AberturaScreen({super.key, required this.servicos});

  final Servicos servicos;

  @override
  State<AberturaScreen> createState() => _AberturaScreenState();
}

class _AberturaScreenState extends State<AberturaScreen> {
  bool _verificando = true;
  String? _erro;

  @override
  void initState() {
    super.initState();
    // Depois do primeiro quadro: a verificação pode navegar, e navegar
    // durante o initState não é permitido.
    WidgetsBinding.instance.addPostFrameCallback((_) => _verificar());
  }

  Future<void> _verificar() async {
    final servicos = widget.servicos;

    if (!servicos.config.configurado) {
      _abrir(ServidorScreen(servicos: servicos, primeiraVez: true));
      return;
    }

    final status = await servicos.status.verificar();
    if (!mounted) return;
    if (!status.tudoOk) {
      setState(() {
        _verificando = false;
        _erro = status.mensagem;
      });
      return;
    }

    try {
      await servicos.sessao.carregar();
    } catch (_) {
      // Armazenamento seguro ilegível (ex.: app reinstalado com backup antigo):
      // descarta a sessão e pede login de novo.
      await servicos.sessao.encerrar();
    }
    if (!mounted) return;

    _abrir(servicos.sessao.estaValida ? AgendaScreen(servicos: servicos) : LoginScreen(servicos: servicos));
  }

  void _tentarDeNovo() {
    setState(() {
      _verificando = true;
      _erro = null;
    });
    _verificar();
  }

  Future<void> _alterarServidor() async {
    final alterou = await Navigator.of(context).push<bool>(
      MaterialPageRoute(builder: (_) => ServidorScreen(servicos: widget.servicos)),
    );
    if (alterou == true && mounted) {
      _tentarDeNovo();
    }
  }

  void _abrir(Widget tela) {
    Navigator.of(context).pushReplacement(MaterialPageRoute<void>(builder: (_) => tela));
  }

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Icon(Icons.local_hospital_rounded, size: 72, color: tema.colorScheme.primary),
                const SizedBox(height: 12),
                Text('UBS Agendamento', style: tema.textTheme.headlineSmall),
                const SizedBox(height: 32),
                if (_verificando) ...[
                  const CircularProgressIndicator(),
                  const SizedBox(height: 16),
                  Text(
                    'Conectando ao servidor e ao banco de dados…',
                    style: tema.textTheme.bodyMedium,
                    textAlign: TextAlign.center,
                  ),
                ] else ...[
                  MensagemEstado(
                    icone: Icons.cloud_off_outlined,
                    titulo: 'Não foi possível iniciar',
                    mensagem: _erro,
                    erro: true,
                  ),
                  Text(
                    'Servidor: ${widget.servicos.config.urlBase}',
                    style: tema.textTheme.bodySmall,
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 16),
                  FilledButton.icon(
                    onPressed: _tentarDeNovo,
                    icon: const Icon(Icons.refresh),
                    label: const Text('Tentar novamente'),
                  ),
                  const SizedBox(height: 8),
                  TextButton.icon(
                    onPressed: _alterarServidor,
                    icon: const Icon(Icons.dns_outlined),
                    label: const Text('Alterar servidor'),
                  ),
                ],
              ],
            ),
          ),
        ),
      ),
    );
  }
}
