import 'package:flutter/material.dart';

import '../models/status_agendamento.dart';
import '../tema.dart';

class StatusChip extends StatelessWidget {
  const StatusChip({super.key, required this.status});

  final StatusAgendamento status;

  @override
  Widget build(BuildContext context) {
    final visual = visualDoStatus(status, Theme.of(context).colorScheme);
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(color: visual.fundo, borderRadius: BorderRadius.circular(20)),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(visual.icone, size: 14, color: visual.texto),
          const SizedBox(width: 4),
          Text(
            status.rotulo,
            style: Theme.of(context).textTheme.labelSmall?.copyWith(color: visual.texto),
          ),
        ],
      ),
    );
  }
}
