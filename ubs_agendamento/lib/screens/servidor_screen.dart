import 'package:flutter/material.dart';

import '../services/servicos.dart';
import '../tema.dart';
import 'login_screen.dart';

/// Endereço da API. Aparece sozinha na primeira abertura e pode ser aberta
/// depois pelo login ou pela tela de erro de conexão.
class ServidorScreen extends StatefulWidget {
  const ServidorScreen({super.key, required this.servicos, this.primeiraVez = false});

  final Servicos servicos;

  /// Na primeira vez, ao salvar segue para o login; nas demais, volta (pop).
  final bool primeiraVez;

  @override
  State<ServidorScreen> createState() => _ServidorScreenState();
}

class _ServidorScreenState extends State<ServidorScreen> {
  late final _url = TextEditingController(text: widget.servicos.config.urlBase);
  bool _testando = false;
  String? _erro;

  @override
  void dispose() {
    _url.dispose();
    super.dispose();
  }

  Future<void> _testarESalvar() async {
    FocusScope.of(context).unfocus();
    setState(() {
      _erro = null;
      _testando = true;
    });

    try {
      await widget.servicos.config.salvar(_url.text);
    } on FormatException catch (e) {
      setState(() {
        _testando = false;
        _erro = e.message;
      });
      return;
    }

    final status = await widget.servicos.status.verificar();
    if (!mounted) return;
    setState(() => _testando = false);

    if (!status.tudoOk) {
      setState(() => _erro = status.mensagem);
      return;
    }

    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Conectado ao servidor e ao banco de dados.')),
    );
    if (widget.primeiraVez) {
      Navigator.of(context).pushReplacement(
        MaterialPageRoute<void>(builder: (_) => LoginScreen(servicos: widget.servicos)),
      );
    } else {
      Navigator.of(context).pop(true);
    }
  }

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    final semCriptografia = _url.text.trim().startsWith('http://');

    return Scaffold(
      appBar: AppBar(title: const Text('Servidor da UBS')),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(24),
          children: [
            Text(
              'Informe o endereço da API do sistema da UBS. É o endereço do servidor '
              '(porta do nginx-ubs) seguido do caminho da API definido no .env.',
              style: tema.textTheme.bodyMedium,
            ),
            const SizedBox(height: 24),
            TextField(
              controller: _url,
              keyboardType: TextInputType.url,
              autocorrect: false,
              enabled: !_testando,
              onChanged: (_) => setState(() {}),
              onSubmitted: (_) => _testarESalvar(),
              decoration: decoracaoCampo(
                'Endereço da API',
                icone: Icons.dns_outlined,
                dica: 'https://servidor:8081/diretorio-api',
              ),
            ),
            if (semCriptografia) ...[
              const SizedBox(height: 12),
              Card(
                color: tema.colorScheme.tertiaryContainer,
                child: Padding(
                  padding: const EdgeInsets.all(12),
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Icon(Icons.lock_open, color: tema.colorScheme.onTertiaryContainer),
                      const SizedBox(width: 12),
                      Expanded(
                        child: Text(
                          'Conexão sem criptografia: senha, token e dados de pacientes '
                          'trafegam abertos na rede. Use apenas em testes; em produção, '
                          'https://. A versão de produção do app bloqueia http://.',
                          style: TextStyle(color: tema.colorScheme.onTertiaryContainer),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
            if (_erro != null) ...[
              const SizedBox(height: 12),
              Text(_erro!, style: TextStyle(color: tema.colorScheme.error)),
            ],
            const SizedBox(height: 24),
            FilledButton.icon(
              onPressed: _testando ? null : _testarESalvar,
              icon: _testando
                  ? const SizedBox.square(dimension: 18, child: CircularProgressIndicator(strokeWidth: 2))
                  : const Icon(Icons.wifi_tethering),
              label: Text(_testando ? 'Testando conexão…' : 'Testar conexão e salvar'),
            ),
          ],
        ),
      ),
    );
  }
}
