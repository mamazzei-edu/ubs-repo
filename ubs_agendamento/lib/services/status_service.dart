import '../core/api_client.dart';
import '../core/api_exception.dart';

/// Resultado da verificação feita na abertura do app.
class StatusServidor {
  const StatusServidor({required this.apiOk, required this.bancoOk, this.mensagem});

  final bool apiOk;
  final bool bancoOk;

  /// Motivo, quando algo falhou.
  final String? mensagem;

  bool get tudoOk => apiOk && bancoOk;
}

/// GET /status: confirma que a API responde e que ela alcança o banco.
class StatusService {
  StatusService(this._api);

  final ApiClient _api;

  Future<StatusServidor> verificar() async {
    try {
      final json = await _api.get('status', autenticado: false) as Map<String, dynamic>;
      final bancoOk = json['bancoDeDados'] == 'ok';
      return StatusServidor(
        apiOk: true,
        bancoOk: bancoOk,
        mensagem: bancoOk ? null : 'O servidor está no ar, mas sem acesso ao banco de dados.',
      );
    } on ApiException catch (e) {
      return switch (e.statusCode) {
        // 503 = API no ar, banco fora.
        503 => const StatusServidor(
            apiOk: true,
            bancoOk: false,
            mensagem: 'O servidor está no ar, mas sem acesso ao banco de dados. '
                'Avise o suporte da UBS.',
          ),
        // Backend anterior a esta versão: não tem a rota /status.
        401 || 404 => const StatusServidor(
            apiOk: false,
            bancoOk: false,
            mensagem: 'O servidor respondeu, mas não reconhece o app. Confira se o endereço '
                'termina no caminho da API (ex.: /diretorio-api) e se o backend está atualizado.',
          ),
        _ => StatusServidor(apiOk: false, bancoOk: false, mensagem: e.mensagem),
      };
    }
  }
}
