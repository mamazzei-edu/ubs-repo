import 'medico.dart';

/// Mesma lista de TIPOS_CONSULTA do frontend web
/// (frontend/src/app/model/agendamento.model.ts). Alterou lá, altere aqui.
const tiposConsulta = <String>[
  'Consulta Cardiologia',
  'Consulta Dermatologia',
  'Consulta Endocrinologia',
  'Consulta Gastroenterologia',
  'Consulta Ginecologia',
  'Consulta Neurologia',
  'Consulta Oftalmologia',
  'Consulta Ortopedia',
  'Consulta Pediatria',
  'Consulta Psiquiatria',
  'Consulta Clínica Geral',
  'Exame de Rotina',
  'Consulta de Retorno',
];

/// Tipos que qualquer médico atende.
const _tiposSemEspecialidade = {
  'Consulta Clínica Geral',
  'Exame de Rotina',
  'Consulta de Retorno',
};

/// Médicos que atendem o tipo de consulta — a mesma regra de
/// _filtrarMedicosPorTipo na tela web: "Consulta Cardiologia" exige
/// especialidade contendo "cardiologia".
List<Medico> medicosParaTipo(List<Medico> medicos, String? tipo) {
  if (tipo == null || tipo.isEmpty || _tiposSemEspecialidade.contains(tipo)) {
    return List.of(medicos);
  }
  final chave = tipo.replaceFirst('Consulta ', '').toLowerCase();
  return medicos.where((medico) => medico.especialidade.toLowerCase().contains(chave)).toList();
}
