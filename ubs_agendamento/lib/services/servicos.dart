import 'package:http/http.dart' as http;

import '../core/api_client.dart';
import '../core/configuracao_servidor.dart';
import '../core/sessao.dart';
import 'agendamento_service.dart';
import 'auth_service.dart';
import 'cadastros_service.dart';
import 'status_service.dart';

/// Tudo que as telas usam, montado uma vez em main.dart e repassado pelos
/// construtores. Nos testes, basta montar com um http.Client falso.
class Servicos {
  Servicos({required this.config, required this.sessao, http.Client? cliente})
      : api = ApiClient(config: config, sessao: sessao, cliente: cliente) {
    status = StatusService(api);
    auth = AuthService(api, sessao);
    agendamentos = AgendamentoService(api);
    cadastros = CadastrosService(api);
  }

  final ConfiguracaoServidor config;
  final Sessao sessao;
  final ApiClient api;

  late final StatusService status;
  late final AuthService auth;
  late final AgendamentoService agendamentos;
  late final CadastrosService cadastros;
}
