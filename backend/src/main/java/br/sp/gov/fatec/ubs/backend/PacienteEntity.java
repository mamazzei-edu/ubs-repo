package br.sp.gov.fatec.ubs.backend;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "paciente")
public class PacienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private Long codigo;

    private String nomeCompleto;
    private String nomeSocial;
    private String nomeMae;
    private String nomePai;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")  
    private LocalDate dataNascimento;

    private String sexo;
    private String nacionalidade;
    private String municipioNascimento;
    private String frequentaEscola;
    private String deficiente;
    private String visual;
    private String auditiva;
    private String motora;
    private String intelectual;
    private String telefoneCelular;
    private String telefoneResidencial;
    private String telefoneComercial;
    private String email;
    private String cpf;

    // Getters e Setters
    public Long getCodigo() { return codigo; }
    public void setCodigo(Long codigo) { this.codigo = codigo; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getNomeSocial() { return nomeSocial; }
    public void setNomeSocial(String nomeSocial) { this.nomeSocial = nomeSocial; }

    public String getNomeMae() { return nomeMae; }
    public void setNomeMae(String nomeMae) { this.nomeMae = nomeMae; }

    public String getNomePai() { return nomePai; }
    public void setNomePai(String nomePai) { this.nomePai = nomePai; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getNacionalidade() { return nacionalidade; }
    public void setNacionalidade(String nacionalidade) { this.nacionalidade = nacionalidade; }

    public String getMunicipioNascimento() { return municipioNascimento; }
    public void setMunicipioNascimento(String municipioNascimento) { this.municipioNascimento = municipioNascimento; }

    public String getFrequentaEscola() { return frequentaEscola; }
    public void setFrequentaEscola(String frequentaEscola) { this.frequentaEscola = frequentaEscola; }

    public String getDeficiente() { return deficiente; }
    public void setDeficiente(String deficiente) { this.deficiente = deficiente; }

    public String getVisual() { return visual; }
    public void setVisual(String visual) { this.visual = visual; }

    public String getAuditiva() { return auditiva; }
    public void setAuditiva(String auditiva) { this.auditiva = auditiva; }

    public String getMotora() { return motora; }
    public void setMotora(String motora) { this.motora = motora; }

    public String getIntelectual() { return intelectual; }
    public void setIntelectual(String intelectual) { this.intelectual = intelectual; }

    public String getTelefoneCelular() { return telefoneCelular; }
    public void setTelefoneCelular(String telefoneCelular) { this.telefoneCelular = telefoneCelular; }

    public String getTelefoneResidencial() { return telefoneResidencial; }
    public void setTelefoneResidencial(String telefoneResidencial) { this.telefoneResidencial = telefoneResidencial; }

    public String getTelefoneComercial() { return telefoneComercial; }
    public void setTelefoneComercial(String telefoneComercial) { this.telefoneComercial = telefoneComercial; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}
