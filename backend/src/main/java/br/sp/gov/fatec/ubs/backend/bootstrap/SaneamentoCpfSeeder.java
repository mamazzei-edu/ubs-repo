package br.sp.gov.fatec.ubs.backend.bootstrap;

import br.sp.gov.fatec.ubs.backend.exceptions.ValidaCPF;
import br.sp.gov.fatec.ubs.backend.model.Paciente;
import br.sp.gov.fatec.ubs.backend.repositories.PacienteRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Arruma, na partida, os CPFs gravados antes da validação existir.
 *
 * Bases antigas guardavam o CPF como foi digitado ("123.456.789-09", "",
 * "12345678909"). Isso fazia a busca por CPF normalizado não achar o paciente
 * já cadastrado e permitia a duplicata; e as strings vazias e repetidas
 * impediam o Hibernate de criar o índice único da coluna.
 *
 * O que faz:
 *   1. normaliza o CPF (somente dígitos; vazio vira NULL) quando isso não
 *      colide com outro paciente;
 *   2. lista no log os pacientes com CPF repetido.
 *
 * O que NÃO faz: fundir ou apagar pacientes duplicados. Os dois registros
 * podem ter agendamentos e dados diferentes — escolher o que fica é decisão de
 * quem conhece o paciente. Resolvidas as duplicatas, o índice único é criado
 * pelo Hibernate (ddl-auto=update) na partida seguinte.
 *
 * Idempotente: numa base já saneada, as duas consultas voltam vazias.
 */
@Component
@Order(3)
public class SaneamentoCpfSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(SaneamentoCpfSeeder.class);

    private final PacienteRepository pacienteRepository;

    public SaneamentoCpfSeeder(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            this.normalizarCpfs();
            this.avisarDuplicados();
        } catch (RuntimeException e) {
            // O saneamento é um conserto de dados antigos: se falhar, a
            // aplicação precisa subir do mesmo jeito.
            log.error("Saneamento de CPF não concluído", e);
        }
    }

    private void normalizarCpfs() {
        List<Paciente> pendentes = pacienteRepository.findComCpfNaoNormalizado();
        int corrigidos = 0;

        for (Paciente paciente : pendentes) {
            String normalizado = ValidaCPF.normalizar(paciente.getCpf());

            if (normalizado != null) {
                var outro = pacienteRepository.findFirstByCpfAndIdNotOrderByIdAsc(normalizado, paciente.getId());
                if (outro.isPresent()) {
                    log.warn("CPF duplicado: paciente id={} tem o mesmo CPF do paciente id={}. "
                            + "Mantido como está para revisão manual.", paciente.getId(), outro.get().getId());
                    continue;
                }
            }

            paciente.setCpf(normalizado);
            pacienteRepository.save(paciente);
            corrigidos++;
        }

        if (corrigidos > 0) {
            log.info("Saneamento de CPF: {} paciente(s) com CPF normalizado.", corrigidos);
        }
    }

    private void avisarDuplicados() {
        List<String> duplicados = pacienteRepository.findCpfsDuplicados();
        for (String cpf : duplicados) {
            String ids = pacienteRepository.findByCpfOrderByIdAsc(cpf).stream()
                    .map(p -> String.valueOf(p.getId()))
                    .collect(Collectors.joining(", "));
            log.warn("CPF duplicado na base: pacientes id {}. Revisar e unificar o cadastro.", ids);
        }
        if (!duplicados.isEmpty()) {
            log.warn("{} CPF(s) repetido(s): o índice único da coluna cpf só é criado depois de resolvê-los.",
                    duplicados.size());
        }
    }
}
