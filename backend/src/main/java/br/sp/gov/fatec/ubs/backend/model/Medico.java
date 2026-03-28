package br.sp.gov.fatec.ubs.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "medicos")
@PrimaryKeyJoinColumn(name = "user_id") // Conecta com o ID da tabela User
public class Medico extends User {

    @Column(nullable = false, unique = true)
    private String crm;

    // Cria uma tabela auxiliar automaticamente para guardar as várias especialidades
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "medico_especialidades", joinColumns = @JoinColumn(name = "medico_id"))
    @Column(name = "especialidade")
    private List<String> especialidades;

    public Medico() {
        // Construtor vazio padrão exigido pelo JPA
    }

    // ======================== Getters e Setters ========================

    public String getCrm() {
        return crm;
    }

    public Medico setCrm(String crm) {
        this.crm = crm;
        return this;
    }

    public List<String> getEspecialidades() {
        return especialidades;
    }

    public Medico setEspecialidades(List<String> especialidades) {
        this.especialidades = especialidades;
        return this;
    }
}