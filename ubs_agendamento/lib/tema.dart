import 'package:flutter/material.dart';

import 'models/status_agendamento.dart';

/// Verde-água usado no sistema web (botões "Salvar Alterações").
const corUbs = Color(0xFF50BFAD);

ThemeData temaUbs(Brightness brilho) {
  final cores = ColorScheme.fromSeed(seedColor: corUbs, brightness: brilho);
  return ThemeData(
    colorScheme: cores,
    useMaterial3: true,
    visualDensity: VisualDensity.standard,
  );
}

/// Decoração padrão dos campos de formulário do app.
InputDecoration decoracaoCampo(
  String rotulo, {
  IconData? icone,
  String? dica,
  String? ajuda,
  Widget? sufixo,
}) =>
    InputDecoration(
      labelText: rotulo,
      hintText: dica,
      helperText: ajuda,
      helperMaxLines: 3,
      prefixIcon: icone == null ? null : Icon(icone),
      suffixIcon: sufixo,
      border: const OutlineInputBorder(),
    );

/// Cores e ícone de cada status, derivados do tema (funcionam no modo escuro).
({Color fundo, Color texto, IconData icone}) visualDoStatus(StatusAgendamento status, ColorScheme cores) =>
    switch (status) {
      StatusAgendamento.agendado =>
        (fundo: cores.primaryContainer, texto: cores.onPrimaryContainer, icone: Icons.event_outlined),
      StatusAgendamento.confirmado =>
        (fundo: cores.tertiaryContainer, texto: cores.onTertiaryContainer, icone: Icons.check_circle_outline),
      StatusAgendamento.realizado =>
        (fundo: cores.secondaryContainer, texto: cores.onSecondaryContainer, icone: Icons.task_alt),
      StatusAgendamento.cancelado =>
        (fundo: cores.errorContainer, texto: cores.onErrorContainer, icone: Icons.cancel_outlined),
      StatusAgendamento.faltou =>
        (fundo: cores.outlineVariant, texto: cores.onSurface, icone: Icons.person_off_outlined),
    };
