package br.sp.gov.fatec.ubs.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.List;

@Entity(name = "medico")
public class Medico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Column(nullable = false)
    private String nomeCompleto;
    
    @NotBlank(message = "Especialidade é obrigatória")
    @Size(min = 2, max = 50, message = "Especialidade deve ter entre 2 e 50 caracteres")
    @Column(nullable = false)
    private String especialidade;
    
    @NotBlank(message = "CRM é obrigatório")
    @Size(min = 6, max = 9, message = "CRM deve ter formato válido (ex: 12345SP)")
    @Column(nullable = false, unique = true)
    private String crm;
    
    @Email(message = "Email deve ter formato válido")
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column
    private String telefone;
    
    @Column
    private boolean ativo = true;
    
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
    
    @JsonIgnore
    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Agendamento> agendamentos;
    
    // Construtores
    public Medico() {}
    
    public Medico(String nomeCompleto, String especialidade, String crm, String email) {
        this.nomeCompleto = nomeCompleto;
        this.especialidade = especialidade;
        this.crm = crm;
        this.email = email;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNomeCompleto() {
        return nomeCompleto;
    }
    
    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }
    
    public String getEspecialidade() {
        return especialidade;
    }
    
    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
    
    public String getCrm() {
        return crm;
    }
    
    public void setCrm(String crm) {
        this.crm = crm;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public boolean isAtivo() {
        return ativo;
    }
    
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
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
    
    public List<Agendamento> getAgendamentos() {
        return agendamentos;
    }
    
    public void setAgendamentos(List<Agendamento> agendamentos) {
        this.agendamentos = agendamentos;
    }
    
    @Override
    public String toString() {
        return "Medico{" +
                "id=" + id +
                ", nomeCompleto='" + nomeCompleto + '\'' +
                ", especialidade='" + especialidade + '\'' +
                ", crm='" + crm + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}