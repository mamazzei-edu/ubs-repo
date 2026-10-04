import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

import 'screens/abertura_screen.dart';
import 'screens/login_screen.dart';
import 'services/servicos.dart';
import 'tema.dart';

class UbsAgendamentoApp extends StatefulWidget {
  const UbsAgendamentoApp({super.key, required this.servicos});

  final Servicos servicos;

  @override
  State<UbsAgendamentoApp> createState() => _UbsAgendamentoAppState();
}

class _UbsAgendamentoAppState extends State<UbsAgendamentoApp> {
  final _navegador = GlobalKey<NavigatorState>();
  bool _voltandoAoLogin = false;

  @override
  void initState() {
    super.initState();
    widget.servicos.api.aoExpirarSessao = _voltarAoLogin;
  }

  /// Token vencido ou recusado em qualquer tela: limpa a pilha e volta ao login.
  void _voltarAoLogin() {
    // Várias requisições podem falhar juntas com 401: navega uma vez só.
    if (_voltandoAoLogin) return;
    _voltandoAoLogin = true;
    _navegador.currentState?.pushAndRemoveUntil(
      MaterialPageRoute<void>(
        builder: (_) => LoginScreen(
          servicos: widget.servicos,
          aviso: 'Sua sessão expirou. Entre novamente.',
        ),
      ),
      (_) => false,
    );
    WidgetsBinding.instance.addPostFrameCallback((_) => _voltandoAoLogin = false);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      navigatorKey: _navegador,
      title: 'UBS Agendamento',
      debugShowCheckedModeBanner: false,
      theme: temaUbs(Brightness.light),
      darkTheme: temaUbs(Brightness.dark),
      locale: const Locale('pt', 'BR'),
      supportedLocales: const [Locale('pt', 'BR')],
      localizationsDelegates: GlobalMaterialLocalizations.delegates,
      home: AberturaScreen(servicos: widget.servicos),
    );
  }
}
