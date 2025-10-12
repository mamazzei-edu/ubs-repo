package br.sp.gov.fatec.ubs.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity(name = "agendamento")
public class Agendamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;
    
    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime dataHoraConsulta;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status;
    
    @Column(length = 500)
    private String observacoes;
    
    @Column(nullable = false)
    private String tipoConsulta;
    
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
    
    // Enum para status do agendamento
    public enum StatusAgendamento {
        AGENDADO,
        CONFIRMADO,
        CANCELADO,
        REALIZADO,
        FALTOU
    }
    
    // Construtores
    public Agendamento() {}
    
    public Agendamento(Paciente paciente, Medico medico, LocalDateTime dataHoraConsulta, String tipoConsulta) {
        this.paciente = paciente;
        this.medico = medico;
        this.dataHoraConsulta = dataHoraConsulta;
        this.tipoConsulta = tipoConsulta;
        this.status = StatusAgendamento.AGENDADO;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Paciente getPaciente() {
        return paciente;
    }
    
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
    
    public Medico getMedico() {
        return medico;
    }
    
    public void setMedico(Medico medico) {
        this.medico = medico;
    }
    
    public LocalDateTime getDataHoraConsulta() {
        return dataHoraConsulta;
    }
    
    public void setDataHoraConsulta(LocalDateTime dataHoraConsulta) {
        this.dataHoraConsulta = dataHoraConsulta;
    }
    
    public StatusAgendamento getStatus() {
        return status;
    }
    
    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public String getTipoConsulta() {
        return tipoConsulta;
    }
    
    public void setTipoConsulta(String tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Agendamento{" +
                "id=" + id +
                ", paciente=" + (paciente != null ? paciente.getNomeCompleto() : "N/A") +
                ", medico=" + (medico != null ? medico.getNomeCompleto() : "N/A") +
                ", dataHoraConsulta=" + dataHoraConsulta +
                ", status=" + status +
                ", tipoConsulta='" + tipoConsulta + '\'' +
                '}';
    }
}