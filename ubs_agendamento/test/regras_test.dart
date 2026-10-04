// Regras sem interface: formatos, filtro de médicos, leitura do JSON da API
// e tradução de erros.
import 'package:flutter_test/flutter_test.dart';
import 'package:intl/date_symbol_data_local.dart';
import 'package:ubs_agendamento/core/api_exception.dart';
import 'package:ubs_agendamento/core/configuracao_servidor.dart';
import 'package:ubs_agendamento/models/agendamento.dart';
import 'package:ubs_agendamento/models/medico.dart';
import 'package:ubs_agendamento/models/status_agendamento.dart';
import 'package:ubs_agendamento/models/tipos_consulta.dart';
import 'package:ubs_agendamento/util/formatos.dart';

void main() {
  setUpAll(() => initializeDateFormatting('pt_BR'));

  group('mascararCpf', () {
    test('formata os 11 dígitos', () {
      expect(mascararCpf('12345678909'), '123.456.789-09');
    });
    test('é progressiva enquanto se digita', () {
      expect(mascararCpf('123'), '123');
      expect(mascararCpf('1234'), '123.4');
      expect(mascararCpf('1234567'), '123.456.7');
    });
    test('ignora o que não é dígito e o excesso', () {
      expect(mascararCpf('123.456.789-0999'), '123.456.789-09');
      expect(mascararCpf(null), '');
    });
  });

  group('datas', () {
    test('paraIsoLocal gera o LocalDateTime que o backend espera', () {
      expect(paraIsoLocal(DateTime(2026, 10, 5, 8, 3)), '2026-10-05T08:03:00');
    });
    test('fimDoDia fecha o período da agenda', () {
      expect(paraIsoLocal(fimDoDia(DateTime(2026, 10, 5, 14))), '2026-10-05T23:59:59');
    });
    test('rotuloDoDia destaca hoje e amanhã', () {
      final hoje = DateTime(2026, 10, 5);
      expect(rotuloDoDia(hoje, hoje: hoje), startsWith('Hoje'));
      expect(rotuloDoDia(DateTime(2026, 10, 6), hoje: hoje), startsWith('Amanhã'));
      expect(rotuloDoDia(DateTime(2026, 10, 9), hoje: hoje), isNot(startsWith('Hoje')));
    });
  });

  group('medicosParaTipo (mesma regra da tela web)', () {
    const cardio = Medico(id: 1, nomeCompleto: 'Ana', especialidade: 'Cardiologia', crm: '123456SP');
    const pediatra = Medico(id: 2, nomeCompleto: 'Bruno', especialidade: 'Pediatria', crm: '654321SP');
    const medicos = [cardio, pediatra];

    test('tipo de especialidade filtra pelo nome da especialidade', () {
      expect(medicosParaTipo(medicos, 'Consulta Cardiologia'), [cardio]);
      expect(medicosParaTipo(medicos, 'Consulta Pediatria'), [pediatra]);
    });
    test('clínica geral, retorno e exame de rotina aceitam qualquer médico', () {
      expect(medicosParaTipo(medicos, 'Consulta Clínica Geral'), medicos);
      expect(medicosParaTipo(medicos, 'Consulta de Retorno'), medicos);
      expect(medicosParaTipo(medicos, 'Exame de Rotina'), medicos);
    });
    test('sem tipo escolhido mostra todos', () {
      expect(medicosParaTipo(medicos, null), medicos);
    });
  });

  group('Agendamento.fromJson', () {
    final json = {
      'id': 7,
      'paciente': {'id': 3, 'nomeCompleto': 'Maria Silva', 'cpf': '12345678909', 'rg': 'ignorado'},
      'medico': {'id': 1, 'nomeCompleto': 'Ana', 'especialidade': 'Cardiologia', 'crm': '123456SP', 'ativo': true},
      'dataHoraConsulta': '2026-10-05T14:30:00',
      'status': 'CONFIRMADO',
      'tipoConsulta': 'Consulta Cardiologia',
      'observacoes': 'Trazer exames',
      'createdAt': 1759600000000,
    };

    test('lê o formato devolvido pelo backend', () {
      final agendamento = Agendamento.fromJson(json);
      expect(agendamento.id, 7);
      expect(agendamento.paciente.nomeCompleto, 'Maria Silva');
      expect(agendamento.medico.crm, '123456SP');
      expect(agendamento.dataHoraConsulta, DateTime(2026, 10, 5, 14, 30));
      expect(agendamento.status, StatusAgendamento.confirmado);
    });

    test('regras dos botões seguem o status', () {
      expect(StatusAgendamento.agendado.podeConfirmar, isTrue);
      expect(StatusAgendamento.confirmado.podeConfirmar, isFalse);
      expect(StatusAgendamento.confirmado.podeCancelar, isTrue);
      expect(StatusAgendamento.realizado.podeCancelar, isFalse);
    });

    test('status desconhecido é erro, não um palpite', () {
      expect(() => StatusAgendamento.deValor('OUTRO'), throwsFormatException);
    });
  });

  group('ApiException.daResposta', () {
    test('usa o "detail" do ProblemDetail do backend', () {
      final erro = ApiException.daResposta(400, '{"title":"Bad Request","detail":"Médico não disponível neste horário"}');
      expect(erro.mensagem, 'Médico não disponível neste horário');
      expect(erro.statusCode, 400);
    });
    test('guarda os campos extras, como o id do paciente com o mesmo CPF', () {
      final erro = ApiException.daResposta(409, '{"detail":"CPF já cadastrado","pacienteExistenteId":42}');
      expect(erro.dados['pacienteExistenteId'], 42);
    });
    test('sem corpo, cai na mensagem padrão do código', () {
      expect(ApiException.daResposta(401, '').mensagem, contains('Sessão expirada'));
      expect(ApiException.daResposta(502, '<html>').mensagem, contains('servidor'));
    });
  });

  group('endereço do servidor', () {
    test('normaliza tirando as barras finais', () {
      expect(normalizarUrlBase(' https://ubs:8081/diretorio-api/ '), 'https://ubs:8081/diretorio-api');
    });
    test('exige http:// ou https:// explícito', () {
      expect(() => normalizarUrlBase('ubs:8081/api'), throwsFormatException);
      expect(() => normalizarUrlBase(''), throwsFormatException);
    });
    test('monta a rota sem barra dupla e com parâmetros', () {
      final uri = montarUri('https://ubs/diretorio-api', '/api/pacientes/busca', {'termo': 'ana maria'});
      expect(uri.toString(), 'https://ubs/diretorio-api/api/pacientes/busca?termo=ana+maria');
    });
  });
}
