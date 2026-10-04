import '../core/api_client.dart';
import '../models/medico.dart';
import '../models/paciente.dart';

/// Consultas de paciente e médico que o formulário de agendamento precisa.
class CadastrosService {
  CadastrosService(this._api);

  final ApiClient _api;

  /// Busca por parte do nome ou começo do CPF (no máximo 20 resultados), para
  /// não baixar a base inteira de pacientes no celular.
  Future<List<Paciente>> buscarPacientes(String termo) async {
    final json = await _api.get('api/pacientes/busca', parametros: {'termo': termo.trim()}) as List<dynamic>;
    return json.map((item) => Paciente.fromJson(item as Map<String, dynamic>)).toList();
  }

  /// Só os ativos: o backend recusa agendamento com médico inativo.
  Future<List<Medico>> listarMedicosAtivos() async {
    final json = await _api.get('api/medicos/ativos') as List<dynamic>;
    final medicos = json.map((item) => Medico.fromJson(item as Map<String, dynamic>)).toList();
    medicos.sort((a, b) => a.nomeCompleto.toLowerCase().compareTo(b.nomeCompleto.toLowerCase()));
    return medicos;
  }
}
