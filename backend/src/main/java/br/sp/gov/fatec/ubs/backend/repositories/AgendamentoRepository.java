package br.sp.gov.fatec.ubs.backend.repositories;

import br.sp.gov.fatec.ubs.backend.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    
    // Buscar agendamentos por paciente
    List<Agendamento> findByPacienteId(Long pacienteId);
    
    // Buscar agendamentos por médico
    List<Agendamento> findByMedicoId(Long medicoId);
    
    // Buscar agendamentos por status
    List<Agendamento> findByStatus(Agendamento.StatusAgendamento status);
    
    // Buscar agendamentos por data
    @Query("SELECT a FROM agendamento a WHERE DATE(a.dataHoraConsulta) = DATE(:data)")
    List<Agendamento> findByDataConsulta(@Param("data") LocalDateTime data);
    
    // Buscar agendamentos entre datas
    List<Agendamento> findByDataHoraConsultaBetween(LocalDateTime inicio, LocalDateTime fim);
    
    // Verificar se médico tem disponibilidade no horário
    @Query("SELECT COUNT(a) FROM agendamento a WHERE a.medico.id = :medicoId AND a.dataHoraConsulta = :dataHora AND a.status != 'CANCELADO'")
    Long countByMedicoAndDataHora(@Param("medicoId") Long medicoId, @Param("dataHora") LocalDateTime dataHora);
    
    // Buscar próximos agendamentos do paciente
    @Query("SELECT a FROM agendamento a WHERE a.paciente.id = :pacienteId AND a.dataHoraConsulta > :agora ORDER BY a.dataHoraConsulta ASC")
    List<Agendamento> findProximosAgendamentosPaciente(@Param("pacienteId") Long pacienteId, @Param("agora") LocalDateTime agora);
    
    // Buscar próximos agendamentos do médico
    @Query("SELECT a FROM agendamento a WHERE a.medico.id = :medicoId AND a.dataHoraConsulta > :agora ORDER BY a.dataHoraConsulta ASC")
    List<Agendamento> findProximosAgendamentosMedico(@Param("medicoId") Long medicoId, @Param("agora") LocalDateTime agora);
    
    // Buscar agendamentos do médico em um período específico
    @Query("SELECT a FROM agendamento a WHERE a.medico.id = :medicoId AND a.dataHoraConsulta BETWEEN :inicio AND :fim ORDER BY a.dataHoraConsulta ASC")
    List<Agendamento> findAgendamentosMedicoPeriodo(@Param("medicoId") Long medicoId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
    
    // Buscar agendamentos por médico e status
    List<Agendamento> findByMedicoIdAndStatus(Long medicoId, Agendamento.StatusAgendamento status);
    
    // Buscar agendamentos por paciente e status
    List<Agendamento> findByPacienteIdAndStatus(Long pacienteId, Agendamento.StatusAgendamento status);
}