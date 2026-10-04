/// Espelha o enum Agendamento.StatusAgendamento do backend.
enum StatusAgendamento {
  agendado('AGENDADO', 'Agendado'),
  confirmado('CONFIRMADO', 'Confirmado'),
  cancelado('CANCELADO', 'Cancelado'),
  realizado('REALIZADO', 'Realizado'),
  faltou('FALTOU', 'Faltou');

  const StatusAgendamento(this.valor, this.rotulo);

  /// Como vem e vai no JSON.
  final String valor;

  /// Como aparece na tela.
  final String rotulo;

  static StatusAgendamento deValor(String? valor) => values.firstWhere(
        (status) => status.valor == valor,
        orElse: () => throw FormatException('Status de agendamento desconhecido: $valor'),
      );

  // Mesmas regras dos botões da tela web de agendamento.
  bool get podeConfirmar => this == agendado;
  bool get podeCancelar => this == agendado || this == confirmado;
}
