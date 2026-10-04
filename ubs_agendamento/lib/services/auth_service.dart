import '../core/api_client.dart';
import '../core/api_exception.dart';
import '../core/sessao.dart';

/// Login pelo POST /auth/token, a variante do login que devolve o JWT no
/// corpo (a /auth/login do site grava cookie httpOnly, que não serve ao app).
class AuthService {
  AuthService(this._api, this._sessao);

  final ApiClient _api;
  final Sessao _sessao;

  Future<void> entrar({required String email, required String senha}) async {
    final json = await _api.post(
      'auth/token',
      corpo: {'email': email.trim(), 'password': senha},
      autenticado: false,
    ) as Map<String, dynamic>;

    final token = json['token'] as String?;
    if (token == null || token.isEmpty) {
      throw ApiException('O servidor não devolveu o token de acesso. Atualize o backend.');
    }

    await _sessao.iniciar(
      token: token,
      // Apesar do nome, "expiresIn" é o instante de expiração em ms (epoch).
      expiraEm: DateTime.fromMillisecondsSinceEpoch((json['expiresIn'] as num).toInt()),
      papeis: ((json['roles'] as List?) ?? const []).map((papel) => papel.toString()).toList(),
    );
  }

  /// O JWT é sem estado no servidor: sair é descartar o token do aparelho.
  Future<void> sair() => _sessao.encerrar();
}
