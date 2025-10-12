package br.sp.gov.fatec.ubs.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity(name = "paciente")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String nomeCompleto;
    public String nomeSocial;
    public String nomeMae;
    public String nomePai;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public String dataNascimento;

    public String cns;
    public String prontuario;
    public String sexo;
    public String nacionalidade;
    public String municipioNascimento;
    public String racaCor;
    public String etnia;
    public String frequentaEscola;
    public String deficiente;
    public String opm;
    public String visual;
    public String auditiva;
    public String motora;
    public String intelectual;
    public String telefoneCelular;
    public String telefoneResidencial;
    public String telefoneComercial;
    public String email;
    public String cpf;

    // Novos campos do PDF
    public String escolaridade;
    public String situacaoFamiliar;
    public String ocupacao;
    public String estabelecimentoVinculo;
    public String estabelecimentoCadastro;
    public String cep;
    public String logradouro;
    public String numero;
    public String bairro;
    public String complemento;
    public String uf;
    public String rg;
    public String orgaoEmissor;
    public String pisPasepNis;
    public String cnh;
    public String ctps;
    public String tituloEleitor;
    public String passaporte;
    public String origemEndereco;
    public String municipioResidencia;
    public String distritoAdministrativo;
    public String tipoLogradouro;
    String referencia;

    @JsonIgnore
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // <-- NOVO
    private List<Agendamento> agendamentos; 

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
 
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
 
    public String getNomeSocial() { return nomeSocial; }
    public void setNomeSocial(String nomeSocial) { this.nomeSocial = nomeSocial; }
 
    public String getNomeMae() { return nomeMae; }
    public void setNomeMae(String nomeMae) { this.nomeMae = nomeMae; }
 
    public String getNomePai() { return nomePai; }
    public void setNomePai(String nomePai) { this.nomePai = nomePai; }
 
    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }
 
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
 
    public String getCns() { return cns;    }
    public void setCns(String cns) { this.cns = cns;}
 
    public String getProntuario() { return prontuario; }
    public void setProntuario(String prontuario) { this.prontuario = prontuario;  }
 
    public String getEscolaridade() { return escolaridade; }
    public void setEscolaridade(String escolaridade) { this.escolaridade = escolaridade; }

    public String getSituacaoFamiliar() { return situacaoFamiliar; }
    public String getRacaCor() {
        return racaCor;
    }
    public void setRacaCor(String racaCor) {
        this.racaCor = racaCor;
    }
    public String getEtnia() {
        return etnia;
    }
    public void setEtnia(String etnia) {
        this.etnia = etnia;
    }
    public String getEstabelecimentoVinculo() {
        return estabelecimentoVinculo;
    }
    public void setEstabelecimentoVinculo(String estabelecimentoVinculo) {
        this.estabelecimentoVinculo = estabelecimentoVinculo;
    }
    public String getEstabelecimentoCadastro() {
        return estabelecimentoCadastro;
    }
    public void setEstabelecimentoCadastro(String estabelecimentoCadastro) {
        this.estabelecimentoCadastro = estabelecimentoCadastro;
    }
    public void setSituacaoFamiliar(String situacaoFamiliar) { this.situacaoFamiliar = situacaoFamiliar; }

    public String getOcupacao() { return ocupacao; }
    public void setOcupacao(String ocupacao) { this.ocupacao = ocupacao; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }



    public String getOrgaoEmissor() { return orgaoEmissor; }
    public void setOrgaoEmissor(String orgaoEmissor) { this.orgaoEmissor = orgaoEmissor; }

    public String getPisPasepNis() { return pisPasepNis; }
    public void setPisPasepNis(String pisPasepNis) { this.pisPasepNis = pisPasepNis; }

    public String getCnh() { return cnh; }
    public void setCnh(String cnh) { this.cnh = cnh; }

    public String getCtps() { return ctps; }
    public void setCtps(String ctps) { this.ctps = ctps; }

    public String getTituloEleitor() { return tituloEleitor; }
    public void setTituloEleitor(String tituloEleitor) { this.tituloEleitor = tituloEleitor; }

    public String getPassaporte() { return passaporte; }
    public void setPassaporte(String passaporte) { this.passaporte = passaporte; }
    
    public String getMunicipioResidencia() {
        return municipioResidencia;
    }
    public void setMunicipioResidencia(String municipioResidencia) {
        this.municipioResidencia = municipioResidencia;
    }
    
    public String getOpm() {
        return opm;
    }
    public void setOpm(String opm) {
        this.opm = opm;
    }
    public String getOrigemEndereco() {
        return origemEndereco;
    }
    public void setOrigemEndereco(String origemEndereco) {
        this.origemEndereco = origemEndereco;
    }
    public String getDistritoAdministrativo() {
        return distritoAdministrativo;
    }
    public void setDistritoAdministrativo(String distritoAdministrativo) {
        this.distritoAdministrativo = distritoAdministrativo;
    }
    public String getTipoLogradouro() {
        return tipoLogradouro;
    }
    public void setTipoLogradouro(String tipoLogradouro) {
        this.tipoLogradouro = tipoLogradouro;
    }
    public String getReferencia() {
        return referencia;
    }
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public List<Agendamento> getAgendamentos() {
        return agendamentos;
    }

    public void setAgendamentos(List<Agendamento> agendamentos) {
        this.agendamentos = agendamentos;
    }
}