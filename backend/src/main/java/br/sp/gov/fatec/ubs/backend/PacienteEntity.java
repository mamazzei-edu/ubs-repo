package br.sp.gov.fatec.ubs.backend;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

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

    // Novo campo CPF (se precisar capturar do PDF)
    private String cpf;

    // Armazena apenas o nome/caminho do arquivo PDF
    private String ficha; 

    // Caso queira armazenar o PDF no banco diretamente:
    @Lob
    private byte[] arquivoFicha;

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

    public String getFicha() { return ficha; }
    public void setFicha(String ficha) { this.ficha = ficha; }

    public byte[] getArquivoFicha() { return arquivoFicha; }
    public void setArquivoFicha(byte[] arquivoFicha) { this.arquivoFicha = arquivoFicha; }
    public void setSexo(String extrairRegex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setSexo'");
    }
}