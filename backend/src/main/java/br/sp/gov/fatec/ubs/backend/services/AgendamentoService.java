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
    private MedicoRepository medicoRepository;
    
    @Autowired
    private PacienteRepository pacienteRepository;
    
    // Criar novo agendamento
    public Agendamento criarAgendamento(Long pacienteId, Long medicoId, LocalDateTime dataHora, String tipoConsulta, String observacoes) {
        // Verificar se paciente existe
        Optional<Paciente> paciente = pacienteRepository.findById(pacienteId);
        if (paciente.isEmpty()) {
            throw new IllegalArgumentException("Paciente não encontrado");
        }
        
        // Verificar se médico existe e está ativo
        Optional<Medico> medico = medicoRepository.findById(medicoId);
        if (medico.isEmpty()) {
            throw new IllegalArgumentException("Médico não encontrado");
        }
        
        if (!medico.get().isAtivo()) {
            throw new IllegalArgumentException("Médico não está ativo para agendamentos");
        }
        
        // Verificar se médico tem disponibilidade
        Long conflitos = agendamentoRepository.countByMedicoAndDataHora(medicoId, dataHora);
        if (conflitos > 0) {
            throw new IllegalArgumentException("Médico não disponível neste horário");
        }
        
        // Verificar se o horário não é no passado
        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível agendar para horários no passado");
        }
        
        // Criar agendamento
        Agendamento agendamento = new Agendamento(paciente.get(), medico.get(), dataHora, tipoConsulta);
        agendamento.setObservacoes(observacoes);
        
        return agendamentoRepository.save(agendamento);
    }
    
    // Listar todos os agendamentos
    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }
    
    // Buscar agendamento por ID
    public Optional<Agendamento> buscarPorId(Long id) {
        return agendamentoRepository.findById(id);
    }
    
    // Listar agendamentos por paciente
    public List<Agendamento> listarPorPaciente(Long pacienteId) {
        return agendamentoRepository.findByPacienteId(pacienteId);
    }
    
    // Listar agendamentos por médico
    public List<Agendamento> listarPorMedico(Long medicoId) {
        return agendamentoRepository.findByMedicoId(medicoId);
    }
    
    // Listar agendamentos por data
    public List<Agendamento> listarPorData(LocalDateTime data) {
        return agendamentoRepository.findByDataConsulta(data);
    }
    
    // Listar agendamentos entre datas
    public List<Agendamento> listarEntreDatas(LocalDateTime inicio, LocalDateTime fim) {
        return agendamentoRepository.findByDataHoraConsultaBetween(inicio, fim);
    }
    
    // Atualizar status do agendamento
    public Agendamento atualizarStatus(Long id, Agendamento.StatusAgendamento novoStatus) {
        Optional<Agendamento> agendamento = agendamentoRepository.findById(id);
        if (agendamento.isEmpty()) {
            throw new IllegalArgumentException("Agendamento não encontrado");
        }
        
        agendamento.get().setStatus(novoStatus);
        return agendamentoRepository.save(agendamento.get());
    }
    
    // Cancelar agendamento
    public Agendamento cancelarAgendamento(Long id) {
        return atualizarStatus(id, Agendamento.StatusAgendamento.CANCELADO);
    }
    
    // Confirmar agendamento
    public Agendamento confirmarAgendamento(Long id) {
        return atualizarStatus(id, Agendamento.StatusAgendamento.CONFIRMADO);
    }
    
    // Marcar como realizado
    public Agendamento marcarComoRealizado(Long id) {
        return atualizarStatus(id, Agendamento.StatusAgendamento.REALIZADO);
    }
    
    // Marcar falta
    public Agendamento marcarFalta(Long id) {
        return atualizarStatus(id, Agendamento.StatusAgendamento.FALTOU);
    }
    
    // Atualizar observações
    public Agendamento atualizarObservacoes(Long id, String observacoes) {
        Optional<Agendamento> agendamento = agendamentoRepository.findById(id);
        if (agendamento.isEmpty()) {
            throw new IllegalArgumentException("Agendamento não encontrado");
        }
        
        agendamento.get().setObservacoes(observacoes);
        return agendamentoRepository.save(agendamento.get());
    }
    
    // Buscar próximos agendamentos do paciente
    public List<Agendamento> buscarProximosAgendamentos(Long pacienteId) {
        return agendamentoRepository.findProximosAgendamentosPaciente(pacienteId, LocalDateTime.now());
    }
    
    // Buscar próximos agendamentos do médico
    public List<Agendamento> buscarProximosAgendamentosMedico(Long medicoId) {
        return agendamentoRepository.findProximosAgendamentosMedico(medicoId, LocalDateTime.now());
    }
    
    // Verificar disponibilidade do médico
    public boolean verificarDisponibilidade(Long medicoId, LocalDateTime dataHora) {
        Long conflitos = agendamentoRepository.countByMedicoAndDataHora(medicoId, dataHora);
        return conflitos == 0;
    }
    
    // Reagendar consulta
    public Agendamento reagendar(Long id, LocalDateTime novaDataHora) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(id);
        if (agendamentoOpt.isEmpty()) {
            throw new IllegalArgumentException("Agendamento não encontrado");
        }
        
        Agendamento agendamento = agendamentoOpt.get();
        
        // Verificar se o novo horário está disponível
        if (!verificarDisponibilidade(agendamento.getMedico().getId(), novaDataHora)) {
            throw new IllegalArgumentException("Médico não disponível no novo horário");
        }
        
        // Verificar se o novo horário não é no passado
        if (novaDataHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível reagendar para horários no passado");
        }
        
        agendamento.setDataHoraConsulta(novaDataHora);
        agendamento.setStatus(Agendamento.StatusAgendamento.AGENDADO);
        
        return agendamentoRepository.save(agendamento);
    }
    
    // Deletar agendamento
    public void deletarAgendamento(Long id) {
        if (!agendamentoRepository.existsById(id)) {
            throw new IllegalArgumentException("Agendamento não encontrado");
        }
        agendamentoRepository.deleteById(id);
    }
}