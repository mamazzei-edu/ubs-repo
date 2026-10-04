/// Médico agendável (entidade Medico do backend).
class Medico {
  const Medico({
    required this.id,
    required this.nomeCompleto,
    required this.especialidade,
    required this.crm,
    this.ativo = true,
  });

  factory Medico.fromJson(Map<String, dynamic> json) => Medico(
        id: (json['id'] as num).toInt(),
        nomeCompleto: (json['nomeCompleto'] as String?) ?? '',
        especialidade: (json['especialidade'] as String?) ?? '',
        crm: (json['crm'] as String?) ?? '',
        ativo: (json['ativo'] as bool?) ?? true,
      );

  final int id;
  final String nomeCompleto;
  final String especialidade;
  final String crm;
  final bool ativo;
}
