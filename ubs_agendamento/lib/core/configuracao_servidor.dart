import 'package:shared_preferences/shared_preferences.dart';

/// Endereço da API do backend, escolhido pelo usuário na primeira abertura.
///
/// É o mesmo endereço público que o navegador usa para a API: o nginx-ubs
/// (porta 8081) seguido do API_BASE_PATH do .env. Exemplo:
/// `https://servidor-da-ubs:8081/diretorio-api`.
///
/// O app NUNCA fala direto com o MySQL: toda leitura e gravação passa pela API,
/// que aplica autenticação e as regras de negócio (ver README).
class ConfiguracaoServidor {
  ConfiguracaoServidor._(this._prefs, this._urlBase);

  static const _chave = 'url_base_api';

  /// Valor padrão gravado no build, opcional:
  /// `flutter run --dart-define=API_BASE_URL=http://10.0.2.2:8081/diretorio-api`
  static const urlPadrao = String.fromEnvironment('API_BASE_URL');

  final SharedPreferences _prefs;
  String _urlBase;

  static Future<ConfiguracaoServidor> carregar() async {
    final prefs = await SharedPreferences.getInstance();
    return ConfiguracaoServidor._(prefs, prefs.getString(_chave) ?? urlPadrao);
  }

  String get urlBase => _urlBase;

  bool get configurado => _urlBase.isNotEmpty;

  /// http:// trafega token e dados de paciente sem criptografia.
  bool get semCriptografia => _urlBase.startsWith('http://');

  /// Valida, normaliza e grava. Lança [FormatException] com mensagem para a tela.
  Future<void> salvar(String url) async {
    _urlBase = normalizarUrlBase(url);
    await _prefs.setString(_chave, _urlBase);
  }

  Uri uri(String caminho, [Map<String, String>? parametros]) =>
      montarUri(_urlBase, caminho, parametros);
}

/// Confere o formato e tira as barras finais.
///
/// Exige o esquema explícito: adivinhar http ou https esconderia do usuário se
/// a conexão é criptografada.
String normalizarUrlBase(String url) {
  var valor = url.trim();
  if (valor.isEmpty) {
    throw const FormatException('Informe o endereço do servidor.');
  }
  if (!valor.startsWith('http://') && !valor.startsWith('https://')) {
    throw const FormatException('O endereço deve começar com https:// ou http://');
  }
  final uri = Uri.tryParse(valor);
  if (uri == null || uri.host.isEmpty) {
    throw const FormatException('Endereço inválido. Exemplo: https://servidor:8081/diretorio-api');
  }
  while (valor.endsWith('/')) {
    valor = valor.substring(0, valor.length - 1);
  }
  return valor;
}

/// Junta a base com a rota: ("https://h/api", "/status") -> https://h/api/status
Uri montarUri(String urlBase, String caminho, [Map<String, String>? parametros]) {
  final rota = caminho.replaceFirst(RegExp(r'^/+'), '');
  final uri = Uri.parse('$urlBase/$rota');
  return parametros == null ? uri : uri.replace(queryParameters: parametros);
}
