// Fluxo de abertura e login contra uma API simulada (MockClient): nenhum
// servidor real é necessário para rodar estes testes.
import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:http/testing.dart';
import 'package:intl/date_symbol_data_local.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:ubs_agendamento/app.dart';
import 'package:ubs_agendamento/core/configuracao_servidor.dart';
import 'package:ubs_agendamento/core/sessao.dart';
import 'package:ubs_agendamento/services/servicos.dart';

const _base = 'https://ubs.test/diretorio-api';

Future<Servicos> _montarServicos(MockClient cliente) async {
  SharedPreferences.setMockInitialValues({'url_base_api': _base});
  FlutterSecureStorage.setMockInitialValues({});
  return Servicos(config: await ConfiguracaoServidor.carregar(), sessao: Sessao(), cliente: cliente);
}

http.Response _json(Object corpo, [int status = 200]) => http.Response(
      jsonEncode(corpo),
      status,
      headers: {'content-type': 'application/json; charset=utf-8'},
    );

void main() {
  setUpAll(() => initializeDateFormatting('pt_BR'));

  testWidgets('na abertura consulta /status e, com API e banco no ar, abre o login', (tester) async {
    final chamadas = <String>[];
    final servicos = await _montarServicos(MockClient((requisicao) async {
      chamadas.add(requisicao.url.toString());
      return _json({'api': 'ok', 'bancoDeDados': 'ok'});
    }));

    await tester.pumpWidget(UbsAgendamentoApp(servicos: servicos));
    await tester.pumpAndSettle();

    expect(chamadas, ['$_base/status']);
    expect(find.widgetWithText(FilledButton, 'Entrar'), findsOneWidget);
  });

  testWidgets('com o banco fora do ar, avisa e oferece tentar de novo', (tester) async {
    final servicos = await _montarServicos(MockClient((_) async {
      return _json({'api': 'ok', 'bancoDeDados': 'indisponivel'}, 503);
    }));

    await tester.pumpWidget(UbsAgendamentoApp(servicos: servicos));
    await tester.pumpAndSettle();

    expect(find.textContaining('sem acesso ao banco de dados'), findsOneWidget);
    expect(find.text('Tentar novamente'), findsOneWidget);
  });

  testWidgets('login envia e-mail e senha para /auth/token e abre a agenda do dia', (tester) async {
    Map<String, dynamic>? corpoLogin;
    String? autorizacaoDaAgenda;

    final servicos = await _montarServicos(MockClient((requisicao) async {
      final caminho = requisicao.url.path;
      if (caminho.endsWith('/status')) {
        return _json({'api': 'ok', 'bancoDeDados': 'ok'});
      }
      if (caminho.endsWith('/auth/token')) {
        corpoLogin = jsonDecode(requisicao.body) as Map<String, dynamic>;
        return _json({
          'token': 'token-de-teste',
          'expiresIn': DateTime.now().add(const Duration(hours: 1)).millisecondsSinceEpoch,
          'roles': ['ROLE_USER'],
          'userId': 1,
        });
      }
      if (caminho.endsWith('/api/agendamentos/periodo')) {
        autorizacaoDaAgenda = requisicao.headers['Authorization'];
        return _json(<Object>[]);
      }
      return _json({'detail': 'rota inesperada no teste: $caminho'}, 404);
    }));

    await tester.pumpWidget(UbsAgendamentoApp(servicos: servicos));
    await tester.pumpAndSettle();

    await tester.enterText(find.byType(TextFormField).at(0), 'recepcao@ubs.test');
    await tester.enterText(find.byType(TextFormField).at(1), 'segredo');
    await tester.tap(find.widgetWithText(FilledButton, 'Entrar'));
    await tester.pumpAndSettle();

    expect(corpoLogin, {'email': 'recepcao@ubs.test', 'password': 'segredo'});
    expect(autorizacaoDaAgenda, 'Bearer token-de-teste');
    expect(find.text('Agenda'), findsOneWidget);
    expect(find.text('Nenhum agendamento neste dia'), findsOneWidget);
  });

  testWidgets('login recusado mostra a mensagem do backend', (tester) async {
    final servicos = await _montarServicos(MockClient((requisicao) async {
      if (requisicao.url.path.endsWith('/status')) {
        return _json({'api': 'ok', 'bancoDeDados': 'ok'});
      }
      return _json({'title': 'Unauthorized', 'status': 401, 'detail': 'Usuário ou senha inválidos'}, 401);
    }));

    await tester.pumpWidget(UbsAgendamentoApp(servicos: servicos));
    await tester.pumpAndSettle();

    await tester.enterText(find.byType(TextFormField).at(0), 'recepcao@ubs.test');
    await tester.enterText(find.byType(TextFormField).at(1), 'errada');
    await tester.tap(find.widgetWithText(FilledButton, 'Entrar'));
    await tester.pumpAndSettle();

    expect(find.text('Usuário ou senha inválidos'), findsOneWidget);
  });
}
