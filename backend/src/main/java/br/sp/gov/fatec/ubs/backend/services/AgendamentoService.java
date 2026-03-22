package br.sp.gov.fatec.ubs.backend.services;

import br.sp.gov.fatec.ubs.backend.model.Agendamento;
import br.sp.gov.fatec.ubs.backend.model.Medico;
import br.sp.gov.fatec.ubs.backend.model.Paciente;
import br.sp.gov.fatec.ubs.backend.repositories.AgendamentoRepository;
import br.sp.gov.fatec.ubs.backend.repositories.MedicoRepository;
import br.sp.gov.fatec.ubs.backend.repositories.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private MedicoRepository medicoRepository; // Agora injetamos o repositório do médico

    @Autowired
    private PacienteRepository pacienteRepository;

    public Agendamento criarAgendamento(Long pacienteId, Long medicoId, LocalDateTime dataHora, String tipoConsulta, String observacoes) {
        Optional<Paciente> paciente = pacienteRepository.findById(pacienteId);
        if (paciente.isEmpty()) {
            throw new IllegalArgumentException("Paciente não encontrado");
        }

        // Agora buscamos direto do MedicoRepository para garantir o tipo
        Optional<Medico> medico = medicoRepository.findById(medicoId.intValue());
        if (medico.isEmpty()) {
            throw new IllegalArgumentException("Médico não encontrado");
        }

        if (!medico.get().isAtivo()) {
            throw new IllegalArgumentException("Médico não está ativo para agendamentos");
        }

        Long conflitos = agendamentoRepository.countByMedicoAndDataHora(medicoId, dataHora);
        if (conflitos > 0) {
            throw new IllegalArgumentException("Médico não disponível neste horário");
        }

        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível agendar para horários no passado");
        }

        Agendamento agendamento = new Agendamento(paciente.get(), medico.get(), dataHora, tipoConsulta);
        agendamento.setObservacoes(observacoes);

        return agendamentoRepository.save(agendamento);
    }

    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }

    public Optional<Agendamento> buscarPorId(Long id) {
        return agendamentoRepository.findById(id);
    }

    public List<Agendamento> listarPorPaciente(Long pacienteId) {
        return agendamentoRepository.findByPacienteId(pacienteId);
    }

    public List<Agendamento> listarPorMedico(Long medicoId) {
        return agendamentoRepository.findByMedicoId(medicoId);
    }

    public List<Agendamento> listarPorData(LocalDateTime data) {
        return agendamentoRepository.findByDataConsulta(data);
    }

    public List<Agendamento> listarEntreDatas(LocalDateTime inicio, LocalDateTime fim) {
        return agendamentoRepository.findByDataHoraConsultaBetween(inicio, fim);
    }

    public Agendamento atualizarStatus(Long id, Agendamento.StatusAgendamento novoStatus) {
        Optional<Agendamento> agendamento = agendamentoRepository.findById(id);
        if (agendamento.isEmpty()) {
            throw new IllegalArgumentException("Agendamento não encontrado");
        }
        agendamento.get().setStatus(novoStatus);
        return agendamentoRepository.save(agendamento.get());
    }

    public Agendamento cancelarAgendamento(Long id) {
        return atualizarStatus(id, Agendamento.StatusAgendamento.CANCELADO);
    }

    public Agendamento confirmarAgendamento(Long id) {
        return atualizarStatus(id, Agendamento.StatusAgendamento.CONFIRMADO);
    }

    public Agendamento marcarComoRealizado(Long id) {
        return atualizarStatus(id, Agendamento.StatusAgendamento.REALIZADO);
    }

    public Agendamento marcarFalta(Long id) {
        return atualizarStatus(id, Agendamento.StatusAgendamento.FALTOU);
    }

    public Agendamento atualizarObservacoes(Long id, String observacoes) {
        Optional<Agendamento> agendamento = agendamentoRepository.findById(id);
        if (agendamento.isEmpty()) {
            throw new IllegalArgumentException("Agendamento não encontrado");
        }
        agendamento.get().setObservacoes(observacoes);
        return agendamentoRepository.save(agendamento.get());
    }

    public List<Agendamento> buscarProximosAgendamentos(Long pacienteId) {
        return agendamentoRepository.findProximosAgendamentosPaciente(pacienteId, LocalDateTime.now());
    }

    public List<Agendamento> buscarProximosAgendamentosMedico(Long medicoId) {
        return agendamentoRepository.findProximosAgendamentosMedico(medicoId, LocalDateTime.now());
    }

    public boolean verificarDisponibilidade(Long medicoId, LocalDateTime dataHora) {
        Long conflitos = agendamentoRepository.countByMedicoAndDataHora(medicoId, dataHora);
        return conflitos == 0;
    }

    public Agendamento reagendar(Long id, LocalDateTime novaDataHora) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(id);
        if (agendamentoOpt.isEmpty()) {
            throw new IllegalArgumentException("Agendamento não encontrado");
        }

        Agendamento agendamento = agendamentoOpt.get();

        if (!verificarDisponibilidade(Long.valueOf(agendamento.getMedico().getId()), novaDataHora)) {
            throw new IllegalArgumentException("Médico não disponível no novo horário");
        }

        if (novaDataHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível reagendar para horários no passado");
        }

        agendamento.setDataHoraConsulta(novaDataHora);
        agendamento.setStatus(Agendamento.StatusAgendamento.AGENDADO);

        return agendamentoRepository.save(agendamento);
    }

    public void deletarAgendamento(Long id) {
        if (!agendamentoRepository.existsById(id)) {
            throw new IllegalArgumentException("Agendamento não encontrado");
        }
        agendamentoRepository.deleteById(id);
    }
}