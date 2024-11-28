package br.sp.gov.fatec.ubs.backend;
 
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
 
@Entity(name="paciente")
public class PacienteEntity {
    @Id
    private int codigo;
    private String NomeCompleto;
    private String NomeMae;
    private String NomePai;
    private String DataNascimento;
    private String Sexo;
    private String Nacioalidade;
    private String MunicipioNascimento;
    private String Ra;
    private String SituacaoFamiliar;
    private String FrequentaEscola;
    private String EstabelecimentoVeiculo;
    private String EstabelecimentoCadastro;
    private String Deficiente;
    private String Visual;
    private String Auditiva;
    private String Motora;
    private String Intelectual;
    private String TelefoneCelular;
    private String TelefoneResidencial;
    private String TelefoneComercial;
    private String Contato;
    private String Email;
   
   
 
    public int getCodigo() {
        return codigo;
    }
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
    public String getNomeCompleto() {
        return NomeCompleto;
    }
    public void setNomeCompleto(String nomeCompleto) {
        NomeCompleto = nomeCompleto;
    }
    public String getNomeMae() {
        return NomeMae;
    }
    public void setNomeMae(String nomeMae) {
        NomeMae = nomeMae;
    }
    public String getNomePai() {
        return NomePai;
    }
    public void setNomePai(String nomePai) {
        NomePai = nomePai;
    }
    public String getDataNascimento() {
        return DataNascimento;
    }
    public void setDataNascimento(String dataNascimento) {
        DataNascimento = dataNascimento;
    }
    public String getSexo() {
        return Sexo;
    }
    public void setSexo(String sexo) {
        Sexo = sexo;
    }
    public String getNacioalidade() {
        return Nacioalidade;
    }
    public void setNacioalidade(String nacioalidade) {
        Nacioalidade = nacioalidade;
    }
    public String getMunicipioNascimento() {
        return MunicipioNascimento;
    }
    public void setMunicipioNascimento(String municipioNascimento) {
        MunicipioNascimento = municipioNascimento;
    }
    public String getRa() {
        return Ra;
    }
    public void setRa(String ra) {
        Ra = ra;
    }
    public String getSituacaoFamiliar() {
        return SituacaoFamiliar;
    }
    public void setSituacaoFamiliar(String situacaoFamiliar) {
        SituacaoFamiliar = situacaoFamiliar;
    }
    public String getFrequentaEscola() {
        return FrequentaEscola;
    }
    public void setFrequentaEscola(String frequentaEscola) {
        FrequentaEscola = frequentaEscola;
    }
    public String getEstabelecimentoVeiculo() {
        return EstabelecimentoVeiculo;
    }
    public void setEstabelecimentoVeiculo(String estabelecimentoVeiculo) {
        EstabelecimentoVeiculo = estabelecimentoVeiculo;
    }
    public String getEstabelecimentoCadastro() {
        return EstabelecimentoCadastro;
    }
    public void setEstabelecimentoCadastro(String estabelecimentoCadastro) {
        EstabelecimentoCadastro = estabelecimentoCadastro;
    }
    public String getDeficiente() {
        return Deficiente;
    }
    public void setDeficiente(String deficiente) {
        Deficiente = deficiente;
    }
    public String getVisual() {
        return Visual;
    }
    public void setVisual(String visual) {
        Visual = visual;
    }
    public String getAuditiva() {
        return Auditiva;
    }
    public void setAuditiva(String auditiva) {
        Auditiva = auditiva;
    }
    public String getMotora() {
        return Motora;
    }
    public void setMotora(String motora) {
        Motora = motora;
    }
    public String getIntelectual() {
        return Intelectual;
    }
    public void setIntelectual(String intelectual) {
        Intelectual = intelectual;
    }
    public String getTelefoneCelular() {
        return TelefoneCelular;
    }
    public void setTelefoneCelular(String telefoneCelular) {
        TelefoneCelular = telefoneCelular;
    }
    public String getTelefoneResidencial() {
        return TelefoneResidencial;
    }
    public void setTelefoneResidencial(String telefoneResidencial) {
        TelefoneResidencial = telefoneResidencial;
    }
    public String getTelefoneComercial() {
        return TelefoneComercial;
    }
    public void setTelefoneComercial(String telefoneComercial) {
        TelefoneComercial = telefoneComercial;
    }
    public String getContato() {
        return Contato;
    }
    public void setContato(String contato) {
        Contato = contato;
    }
   
}