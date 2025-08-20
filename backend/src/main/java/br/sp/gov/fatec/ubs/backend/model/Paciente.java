package br.sp.gov.fatec.ubs.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String nomeCompleto;
    public String nomeSocial;
    public String nomeMae;
    public String nomePai;
    public String dataNascimento;
    public String sexo;
    public String nacionalidade;
    public String municipioNascimento;
    public String racaCor;
    public String frequentaEscola;
    public String escolaridade;
    public String situacaoFamiliar;
    public String vinculoEstabelecimento;
    public String deficiencia;
    public String contatoCelular;
    public String contatoResidencial;
    public String contatoComercial;
    public String contatoEmail;
    public String cpf;

    // Getter e Setter para id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter e Setter para nomeCompleto
    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    // Getter e Setter para nomeSocial
    public String getNomeSocial() {
        return nomeSocial;
    }

    public void setNomeSocial(String nomeSocial) {
        this.nomeSocial = nomeSocial;
    }

    // Getter e Setter para nomeMae
    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    // Getter e Setter para nomePai
    public String getNomePai() {
        return nomePai;
    }

    public void setNomePai(String nomePai) {
        this.nomePai = nomePai;
    }

    // Getter e Setter para dataNascimento
    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    // Getter e Setter para sexo
    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    // Getter e Setter para nacionalidade
    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    // Getter e Setter para municipioNascimento
    public String getMunicipioNascimento() {
        return municipioNascimento;
    }

    public void setMunicipioNascimento(String municipioNascimento) {
        this.municipioNascimento = municipioNascimento;
    }

    // Getter e Setter para racaCor
    public String getRacaCor() {
        return racaCor;
    }

    public void setRacaCor(String racaCor) {
        this.racaCor = racaCor;
    }

    // Getter e Setter para frequentaEscola
    public String getFrequentaEscola() {
        return frequentaEscola;
    }

    public void setFrequentaEscola(String frequentaEscola) {
        this.frequentaEscola = frequentaEscola;
    }

    // Getter e Setter para escolaridade
    public String getEscolaridade() {
        return escolaridade;
    }

    public void setEscolaridade(String escolaridade) {
        this.escolaridade = escolaridade;
    }

    // Getter e Setter para situacaoFamiliar
    public String getSituacaoFamiliar() {
        return situacaoFamiliar;
    }

    public void setSituacaoFamiliar(String situacaoFamiliar) {
        this.situacaoFamiliar = situacaoFamiliar;
    }

    // Getter e Setter para vinculoEstabelecimento
    public String getVinculoEstabelecimento() {
        return vinculoEstabelecimento;
    }

    public void setVinculoEstabelecimento(String vinculoEstabelecimento) {
        this.vinculoEstabelecimento = vinculoEstabelecimento;
    }

    // Getter e Setter para deficiencia
    public String getDeficiencia() {
        return deficiencia;
    }

    public void setDeficiencia(String deficiencia) {
        this.deficiencia = deficiencia;
    }

    // Getter e Setter para contatoCelular
    public String getContatoCelular() {
        return contatoCelular;
    }

    public void setContatoCelular(String contatoCelular) {
        this.contatoCelular = contatoCelular;
    }

    // Getter e Setter para contatoResidencial
    public String getContatoResidencial() {
        return contatoResidencial;
    }

    public void setContatoResidencial(String contatoResidencial) {
        this.contatoResidencial = contatoResidencial;
    }

    // Getter e Setter para contatoComercial
    public String getContatoComercial() {
        return contatoComercial;
    }

    public void setContatoComercial(String contatoComercial) {
        this.contatoComercial = contatoComercial;
    }

    // Getter e Setter para contatoEmail
    public String getContatoEmail() {
        return contatoEmail;
    }

    public void setContatoEmail(String contatoEmail) {
        this.contatoEmail = contatoEmail;
    }

    // Getter e Setter para cpf
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
