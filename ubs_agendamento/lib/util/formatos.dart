import 'package:intl/intl.dart';

/// CPF para exibição: "12345678909" -> "123.456.789-09".
/// Máscara progressiva: serve também enquanto o usuário digita.
String mascararCpf(String? cpf) {
  final d = (cpf ?? '').replaceAll(RegExp(r'\D'), '');
  final digitos = d.length > 11 ? d.substring(0, 11) : d;
  if (digitos.length <= 3) return digitos;
  if (digitos.length <= 6) return '${digitos.substring(0, 3)}.${digitos.substring(3)}';
  if (digitos.length <= 9) {
    return '${digitos.substring(0, 3)}.${digitos.substring(3, 6)}.${digitos.substring(6)}';
  }
  return '${digitos.substring(0, 3)}.${digitos.substring(3, 6)}.${digitos.substring(6, 9)}-${digitos.substring(9)}';
}

/// Formato que o backend espera (LocalDateTime, sem fuso): 2026-10-05T14:30:00
String paraIsoLocal(DateTime data) {
  String dois(int n) => n.toString().padLeft(2, '0');
  return '${data.year.toString().padLeft(4, '0')}-${dois(data.month)}-${dois(data.day)}'
      'T${dois(data.hour)}:${dois(data.minute)}:${dois(data.second)}';
}

DateTime inicioDoDia(DateTime data) => DateTime(data.year, data.month, data.day);

DateTime fimDoDia(DateTime data) => DateTime(data.year, data.month, data.day, 23, 59, 59);

bool mesmoDia(DateTime a, DateTime b) => a.year == b.year && a.month == b.month && a.day == b.day;

/// "Hoje", "Amanhã", "Ontem" ou "seg., 5 de out." — cabeçalho da agenda.
String rotuloDoDia(DateTime dia, {DateTime? hoje}) {
  final referencia = inicioDoDia(hoje ?? DateTime.now());
  final diferenca = inicioDoDia(dia).difference(referencia).inDays;
  final data = DateFormat("EEE, d 'de' MMM", 'pt_BR').format(dia);
  return switch (diferenca) {
    0 => 'Hoje · $data',
    1 => 'Amanhã · $data',
    -1 => 'Ontem · $data',
    _ => data,
  };
}

String formatarData(DateTime data) => DateFormat('dd/MM/yyyy', 'pt_BR').format(data);

String formatarHora(DateTime data) => DateFormat('HH:mm', 'pt_BR').format(data);
