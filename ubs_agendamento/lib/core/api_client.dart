import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:http/http.dart' as http;

import 'api_exception.dart';
import 'configuracao_servidor.dart';
import 'sessao.dart';

/// Cliente HTTP da API do backend.
///
/// Concentra o que toda chamada precisa: URL base, cabeçalho
/// "Authorization: Bearer", JSON, tempo limite e tradução de erros em
/// [ApiException] com mensagem em português.
class ApiClient {
  ApiClient({required this.config, required this.sessao, http.Client? cliente})
      : _cliente = cliente ?? http.Client();

  static const _tempoLimite = Duration(seconds: 15);

  final ConfiguracaoServidor config;
  final Sessao sessao;
  final http.Client _cliente;

  /// Chamado quando o servidor recusa o token (401). O app volta ao login.
  void Function()? aoExpirarSessao;

  Future<dynamic> get(String caminho, {Map<String, String>? parametros, bool autenticado = true}) =>
      _enviar('GET', caminho, parametros: parametros, autenticado: autenticado);

  Future<dynamic> post(String caminho, {Object? corpo, bool autenticado = true}) =>
      _enviar('POST', caminho, corpo: corpo, autenticado: autenticado);

  Future<dynamic> put(String caminho, {Object? corpo, bool autenticado = true}) =>
      _enviar('PUT', caminho, corpo: corpo, autenticado: autenticado);

  Future<dynamic> _enviar(
    String metodo,
    String caminho, {
    Map<String, String>? parametros,
    Object? corpo,
    required bool autenticado,
  }) async {
    final requisicao = http.Request(metodo, config.uri(caminho, parametros))
      ..headers['Accept'] = 'application/json';

    if (corpo != null) {
      requisicao.headers['Content-Type'] = 'application/json; charset=utf-8';
      requisicao.body = jsonEncode(corpo);
    }

    if (autenticado) {
      final token = sessao.token;
      if (token == null) {
        aoExpirarSessao?.call();
        throw ApiException(ApiException.mensagemPadrao(401), statusCode: 401);
      }
      requisicao.headers['Authorization'] = 'Bearer $token';
    }

    final http.Response resposta;
    try {
      final enviada = await _cliente.send(requisicao).timeout(_tempoLimite);
      resposta = await http.Response.fromStream(enviada).timeout(_tempoLimite);
    } on TimeoutException {
      throw ApiException('O servidor demorou para responder. Verifique a conexão.');
    } on HandshakeException {
      throw ApiException('Falha na conexão segura (certificado do servidor não aceito).');
    } on SocketException {
      throw ApiException('Sem conexão com o servidor. Verifique a rede e o endereço configurado.');
    } on http.ClientException {
      throw ApiException('Sem conexão com o servidor. Verifique a rede e o endereço configurado.');
    }

    final texto = utf8.decode(resposta.bodyBytes);

    if (resposta.statusCode == 401 && autenticado) {
      await sessao.encerrar();
      aoExpirarSessao?.call();
    }
    if (resposta.statusCode < 200 || resposta.statusCode >= 300) {
      throw ApiException.daResposta(resposta.statusCode, texto);
    }
    return texto.isEmpty ? null : jsonDecode(texto);
  }
}
