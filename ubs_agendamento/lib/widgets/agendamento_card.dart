import 'package:flutter/material.dart';

import '../models/agendamento.dart';
import '../models/status_agendamento.dart';
import '../util/formatos.dart';
import 'status_chip.dart';

/// Um horário da agenda: hora à esquerda, paciente, médico e status.
class AgendamentoCard extends StatelessWidget {
  const AgendamentoCard({super.key, required this.agendamento, this.onTap});

  final Agendamento agendamento;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    final tema = Theme.of(context);
    final inativo = agendamento.status == StatusAgendamento.cancelado ||
        agendamento.status == StatusAgendamento.faltou;

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Largura fixa para as horas ficarem alinhadas entre os cartões;
              // FittedBox reduz o texto em vez de quebrar "09:00" em duas
              // linhas quando a fonte do aparelho está aumentada.
              SizedBox(
                width: 72,
                child: FittedBox(
                  fit: BoxFit.scaleDown,
                  alignment: Alignment.centerLeft,
                  child: Text(
                    formatarHora(agendamento.dataHoraConsulta),
                    maxLines: 1,
                    softWrap: false,
                    style: tema.textTheme.titleLarge?.copyWith(
                      color: inativo ? tema.colorScheme.outline : tema.colorScheme.primary,
                      fontWeight: FontWeight.w600,
                      decoration: inativo ? TextDecoration.lineThrough : null,
                    ),
                  ),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      agendamento.paciente.nomeCompleto,
                      style: tema.textTheme.titleMedium,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(height: 2),
                    Text(
                      'Dr(a). ${agendamento.medico.nomeCompleto}',
                      style: tema.textTheme.bodyMedium,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    Text(
                      agendamento.tipoConsulta,
                      style: tema.textTheme.bodySmall?.copyWith(color: tema.colorScheme.onSurfaceVariant),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(height: 8),
                    StatusChip(status: agendamento.status),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
