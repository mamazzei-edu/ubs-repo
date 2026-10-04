import 'package:flutter/material.dart';

import '../core/api_exception.dart';
import '../services/servicos.dart';
import '../tema.dart';
import 'agenda_screen.dart';
import 'servidor_screen.dart';

/// Login com o mesmo e-mail e senha do sistema web.
class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key, required this.servicos, this.aviso});

  final Servicos servicos;

  /// Mensagem exibida ao chegar aqui (ex.: sessão expirada).
  final String? aviso;

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formulario = GlobalKey<FormState>();
  final _email = TextEditingController();
  final _senha = TextEditingController();
  bool _senhaVisivel = false;
  bool _entrando = false;
  String? _erro;

  @override
  void dispose() {
    _email.dispose();
    _senha.dispose();
    super.dispose();
  }

  Future<void> _entrar() async {
    if (!_formulario.currentState!.validate()) return;
    FocusScope.of(context).unfocus();
    setState(() {
      _entrando = true;
      _erro = null;
    });

    try {
      await widget.servicos.auth.entrar(email: _email.text, senha: _senha.text);
      if (!mounted) return;
      Navigator.of(context).pushAndRemoveUntil(
        MaterialPageRoute<void>(builder: (_) => AgendaScreen(servicos: widget.servicos)),
        (_) => false,
      );
    } on ApiException catch (e) {
      _mostrarErro(e.mensagem);
    } catch (_) {
      // Ex.: falha do armazenamento seguro ao gravar o token.
      _mostrarErro('Não foi possível guardar a sessão neste aparelho. Tente novamente.');
    }
  }

  void _mostrarErro(String mensagem) {
    if (!mounted) return;
    setState(() {
      _entrando = false;
      _erro = mensagem;
    });
  }

  Future<void> _alterarServidor() async {
    await Navigator.of(context).push<bool>(
      MaterialPageRoute(builder: (_) => ServidorScreen(servicos: widget.servicos)),
    );
    if (mounted) setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 420),
              child: Form(
                key: _formulario,
                child: AutofillGroup(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      Icon(Icons.local_hospital_rounded, size: 64, color: tema.colorScheme.primary),
                      const SizedBox(height: 12),
                      Text('UBS Agendamento', style: tema.textTheme.headlineSmall, textAlign: TextAlign.center),
                      const SizedBox(height: 4),
                      Text(
                        'Entre com o mesmo usuário do sistema web.',
                        style: tema.textTheme.bodyMedium,
                        textAlign: TextAlign.center,
                      ),
                      if (widget.aviso != null) ...[
                        const SizedBox(height: 16),
                        Card(
                          color: tema.colorScheme.secondaryContainer,
                          child: Padding(
                            padding: const EdgeInsets.all(12),
                            child: Text(
                              widget.aviso!,
                              style: TextStyle(color: tema.colorScheme.onSecondaryContainer),
                            ),
                          ),
                        ),
                      ],
                      const SizedBox(height: 24),
                      TextFormField(
                        controller: _email,
                        keyboardType: TextInputType.emailAddress,
                        autofillHints: const [AutofillHints.email],
                        autocorrect: false,
                        textInputAction: TextInputAction.next,
                        enabled: !_entrando,
                        decoration: decoracaoCampo('E-mail', icone: Icons.alternate_email),
                        validator: (valor) =>
                            valor == null || !valor.contains('@') ? 'Informe o e-mail.' : null,
                      ),
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _senha,
                        obscureText: !_senhaVisivel,
                        autofillHints: const [AutofillHints.password],
                        textInputAction: TextInputAction.done,
                        enabled: !_entrando,
                        onFieldSubmitted: (_) => _entrar(),
                        decoration: decoracaoCampo(
                          'Senha',
                          icone: Icons.lock_outline,
                          sufixo: IconButton(
                            tooltip: _senhaVisivel ? 'Ocultar senha' : 'Mostrar senha',
                            icon: Icon(_senhaVisivel ? Icons.visibility_off : Icons.visibility),
                            onPressed: () => setState(() => _senhaVisivel = !_senhaVisivel),
                          ),
                        ),
                        validator: (valor) => valor == null || valor.isEmpty ? 'Informe a senha.' : null,
                      ),
                      if (_erro != null) ...[
                        const SizedBox(height: 12),
                        Text(_erro!, style: TextStyle(color: tema.colorScheme.error)),
                      ],
                      const SizedBox(height: 24),
                      FilledButton(
                        onPressed: _entrando ? null : _entrar,
                        child: _entrando
                            ? const SizedBox.square(dimension: 20, child: CircularProgressIndicator(strokeWidth: 2))
                            : const Text('Entrar'),
                      ),
                      const SizedBox(height: 24),
                      TextButton.icon(
                        onPressed: _entrando ? null : _alterarServidor,
                        icon: const Icon(Icons.dns_outlined, size: 18),
                        label: Text(
                          widget.servicos.config.urlBase,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
