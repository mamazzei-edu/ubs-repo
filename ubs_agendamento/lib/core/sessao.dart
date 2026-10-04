import 'package:flutter_secure_storage/flutter_secure_storage.dart';

/// Sessão do usuário logado: o JWT devolvido por POST /auth/token.
///
/// O token fica no armazenamento seguro do sistema (Keystore no Android,
/// Keychain no iOS) — nunca em SharedPreferences, que é texto puro.
class Sessao {
  Sessao([FlutterSecureStorage? armazenamento])
      : _armazenamento = armazenamento ?? const FlutterSecureStorage();

  static const _chaveToken = 'jwt';
  static const _chaveExpiraEm = 'jwt_expira_em';
  static const _chavePapeis = 'papeis';

  /// Folga para não mandar um token que expira no meio da requisição.
  static const _folga = Duration(seconds: 30);

  final FlutterSecureStorage _armazenamento;

  String? _token;
  DateTime? _expiraEm;
  List<String> _papeis = const [];

  bool get estaValida =>
      _token != null && _expiraEm != null && DateTime.now().isBefore(_expiraEm!.subtract(_folga));

  /// O token, ou null se não houver sessão válida.
  String? get token => estaValida ? _token : null;

  List<String> get papeis => _papeis;

  /// Recupera a sessão gravada (app reaberto dentro da validade do token).
  Future<void> carregar() async {
    _token = await _armazenamento.read(key: _chaveToken);
    final expira = int.tryParse(await _armazenamento.read(key: _chaveExpiraEm) ?? '');
    _expiraEm = expira == null ? null : DateTime.fromMillisecondsSinceEpoch(expira);
    final papeis = await _armazenamento.read(key: _chavePapeis);
    _papeis = papeis == null || papeis.isEmpty ? const [] : papeis.split(',');
  }

  Future<void> iniciar({
    required String token,
    required DateTime expiraEm,
    required List<String> papeis,
  }) async {
    _token = token;
    _expiraEm = expiraEm;
    _papeis = papeis;
    await _armazenamento.write(key: _chaveToken, value: token);
    await _armazenamento.write(key: _chaveExpiraEm, value: expiraEm.millisecondsSinceEpoch.toString());
    await _armazenamento.write(key: _chavePapeis, value: papeis.join(','));
  }

  Future<void> encerrar() async {
    _token = null;
    _expiraEm = null;
    _papeis = const [];
    await _armazenamento.delete(key: _chaveToken);
    await _armazenamento.delete(key: _chaveExpiraEm);
    await _armazenamento.delete(key: _chavePapeis);
  }
}
