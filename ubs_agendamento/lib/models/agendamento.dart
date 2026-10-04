import 'medico.dart';
import 'paciente.dart';
import 'status_agendamento.dart';

class Agendamento {
  const Agendamento({
    required this.id,
    required this.paciente,
    required this.medico,
    required this.dataHoraConsulta,
    required this.status,
    required this.tipoConsulta,
    this.observacoes,
  });

  factory Agendamento.fromJson(Map<String, dynamic> json) => Agendamento(
        id: (json['id'] as num).toInt(),
        paciente: Paciente.fromJson(json['paciente'] as Map<String, dynamic>),
        medico: Medico.fromJson(json['medico'] as Map<String, dynamic>),
        // O backend manda "yyyy-MM-ddTHH:mm:ss" sem fuso (LocalDateTime):
        // DateTime.parse interpreta como hora local, que é o que se quer.
        dataHoraConsulta: DateTime.parse(json['dataHoraConsulta'] as String),
        status: StatusAgendamento.deValor(json['status'] as String?),
        tipoConsulta: (json['tipoConsulta'] as String?) ?? '',
        observacoes: json['observacoes'] as String?,
      );

  final int id;
  final Paciente paciente;
  final Medico medico;
  final DateTime dataHoraConsulta;
  final StatusAgendamento status;
  final String tipoConsulta;
  final String? observacoes;
}

/// Corpo do POST /api/agendamentos.
class NovoAgendamento {
  const NovoAgendamento({
    required this.pacienteId,
    required this.medicoId,
    required this.dataHoraConsulta,
    required this.tipoConsulta,
    this.observacoes = '',
  });

  final int pacienteId;
  final int medicoId;
  final DateTime dataHoraConsulta;
  final String tipoConsulta;
  final String observacoes;
}
