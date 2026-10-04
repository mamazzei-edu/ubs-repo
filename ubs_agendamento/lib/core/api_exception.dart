import 'dart:convert';

/// Erro de chamada à API, já com a mensagem pronta para mostrar ao usuário.
///
/// O backend responde os erros em ProblemDetail (RFC 9457), com o motivo em
/// "detail" — por exemplo "Médico não disponível neste horário". Quando não há
/// "detail", usa uma mensagem padrão pelo código HTTP.
class ApiException implements Exception {
  ApiException(this.mensagem, {this.statusCode, this.dados = const {}});

  factory ApiException.daResposta(int statusCode, String corpo) {
    var dados = <String, dynamic>{};
    try {
      final json = jsonDecode(corpo);
      if (json is Map<String, dynamic>) {
        dados = json;
      }
    } on FormatException {
      // Corpo vazio ou não-JSON (ex.: 401 do filtro de segurança).
    }
    final detalhe = dados['detail'];
    return ApiException(
      detalhe is String && detalhe.isNotEmpty ? detalhe : mensagemPadrao(statusCode),
      statusCode: statusCode,
      dados: dados,
    );
  }

  final String mensagem;

  /// null quando a requisição nem chegou ao servidor.
  final int? statusCode;

  /// Corpo do ProblemDetail, para campos extras como "pacienteExistenteId".
  final Map<String, dynamic> dados;

  bool get semConexao => statusCode == null;

  static String mensagemPadrao(int statusCode) => switch (statusCode) {
        400 => 'Dados inválidos. Confira os campos.',
        401 => 'Sessão expirada. Entre novamente.',
        403 => 'Seu usuário não tem permissão para esta ação.',
        404 => 'Registro não encontrado.',
        409 => 'Este registro já existe.',
        >= 500 => 'O servidor encontrou um erro. Tente novamente em instantes.',
        _ => 'Não foi possível concluir a operação (erro $statusCode).',
      };

  @override
  String toString() => mensagem;
}
