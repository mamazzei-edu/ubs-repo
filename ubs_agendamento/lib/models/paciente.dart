/// O que o app usa do paciente. A entidade do backend tem ~50 campos; os
/// demais são ignorados na leitura.
class Paciente {
  const Paciente({
    required this.id,
    required this.nomeCompleto,
    this.cpf,
    this.dataNascimento,
    this.telefoneCelular,
  });

  factory Paciente.fromJson(Map<String, dynamic> json) => Paciente(
        id: (json['id'] as num).toInt(),
        nomeCompleto: (json['nomeCompleto'] as String?) ?? '',
        cpf: json['cpf'] as String?,
        dataNascimento: json['dataNascimento'] as String?,
        telefoneCelular: json['telefoneCelular'] as String?,
      );

  final int id;
  final String nomeCompleto;

  /// Somente dígitos, como o backend grava. Para exibir, ver mascararCpf.
  final String? cpf;
  final String? dataNascimento;
  final String? telefoneCelular;
}
