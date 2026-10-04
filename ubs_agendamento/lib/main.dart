import 'package:flutter/material.dart';
import 'package:intl/date_symbol_data_local.dart';
import 'package:intl/intl.dart';

import 'app.dart';
import 'core/configuracao_servidor.dart';
import 'core/sessao.dart';
import 'services/servicos.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Nomes de dias e meses em português ("seg., 5 de out.").
  Intl.defaultLocale = 'pt_BR';
  await initializeDateFormatting('pt_BR');

  final servicos = Servicos(
    config: await ConfiguracaoServidor.carregar(),
    sessao: Sessao(),
  );

  runApp(UbsAgendamentoApp(servicos: servicos));
}
