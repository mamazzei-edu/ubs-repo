import '../core/api_client.dart';
import '../models/agendamento.dart';
import '../util/formatos.dart';

/// Rotas de /api/agendamentos usadas pelo app.
class AgendamentoService {
  AgendamentoService(this._api);

  final ApiClient _api;

  /// Agenda de um dia, em ordem de horário.
  Future<List<Agendamento>> listarDoDia(DateTime dia) async {
    final json = await _api.get('api/agendamentos/periodo', parametros: {
      'inicio': paraIsoLocal(inicioDoDia(dia)),
      'fim': paraIsoLocal(fimDoDia(dia)),
    }) as List<dynamic>;
    return json.map((item) => Agendamento.fromJson(item as Map<String, dynamic>)).toList();
  }

  Future<bool> medicoDisponivel(int medicoId, DateTime dataHora) async {
    final json = await _api.get(
      'api/agendamentos/medico/$medicoId/disponibilidade',
      parametros: {'dataHora': paraIsoLocal(dataHora)},
    ) as Map<String, dynamic>;
    return json['disponivel'] == true;
  }

  /// As regras (horário livre, médico ativo, data futura) são do backend: se
  /// alguma falhar, vem ApiException com o motivo.
  Future<Agendamento> criar(NovoAgendamento novo) async {
    final json = await _api.post('api/agendamentos', corpo: {
      'pacienteId': novo.pacienteId,
      'medicoId': novo.medicoId,
      'dataHoraConsulta': paraIsoLocal(novo.dataHoraConsulta),
      'tipoConsulta': novo.tipoConsulta,
      'observacoes': novo.observacoes,
    }) as Map<String, dynamic>;
    return Agendamento.fromJson(json);
  }

  Future<Agendamento> confirmar(int id) async =>
      Agendamento.fromJson(await _api.put('api/agendamentos/$id/confirmar') as Map<String, dynamic>);

  Future<Agendamento> cancelar(int id) async =>
      Agendamento.fromJson(await _api.put('api/agendamentos/$id/cancelar') as Map<String, dynamic>);
}
