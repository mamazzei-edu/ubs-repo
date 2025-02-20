package br.sp.gov.fatec.ubs.backend;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name="paciente")
public class PacienteEntity {
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int codigo;

    private String nomeCompleto;
    private String nomeSocial;
    private String nomeMae;
    private String nomePai;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento; 

    private String sexo;
    private String nacionalidade; 
    private String municipioNascimento;
    private String situacaoFamiliar;
    private String frequentaEscola;
    private String estabelecimentoVeiculo;
    private String estabelecimentoCadastro;
    private String deficiente;
    private String visual;
    private String auditiva;
    private String motora;
    private String intelectual;
    private String telefoneCelular;
    private String telefoneResidencial;
    private String telefoneComercial;
    private String contato;
    private String email;
    private String cpf; // CPF do paciente

    // Getters e Setters
    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getNomeSocial() { return nomeSocial; }
    public void setNomeSocial(String nomeSocial) { this.nomeSocial = nomeSocial; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
}
