import 'package:flutter/material.dart';

/// Bloco centralizado para lista vazia, erro ou aviso, com ação opcional.
class MensagemEstado extends StatelessWidget {
  const MensagemEstado({
    super.key,
    required this.icone,
    required this.titulo,
    this.mensagem,
    this.acao,
    this.rotuloAcao,
    this.erro = false,
  });

  final IconData icone;
  final String titulo;
  final String? mensagem;
  final VoidCallback? acao;
  final String? rotuloAcao;
  final bool erro;

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    final cor = erro ? tema.colorScheme.error : tema.colorScheme.outline;
    return Padding(
      padding: const EdgeInsets.all(32),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icone, size: 56, color: cor),
          const SizedBox(height: 16),
          Text(titulo, style: tema.textTheme.titleMedium, textAlign: TextAlign.center),
          if (mensagem != null) ...[
            const SizedBox(height: 8),
            Text(
              mensagem!,
              style: tema.textTheme.bodyMedium?.copyWith(color: tema.colorScheme.onSurfaceVariant),
              textAlign: TextAlign.center,
            ),
          ],
          if (acao != null && rotuloAcao != null) ...[
            const SizedBox(height: 20),
            FilledButton.tonal(onPressed: acao, child: Text(rotuloAcao!)),
          ],
        ],
      ),
    );
  }
}
