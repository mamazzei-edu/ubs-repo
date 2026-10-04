package br.sp.gov.fatec.ubs.backend.dtos;

import br.sp.gov.fatec.ubs.backend.model.Role;
import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * Contrato da tela de cadastro de usuários.
 *
 * Existe porque a tela trata dois registros ao mesmo tempo: o usuário de login
 * (entidade User) e, quando a função é MEDICO, o médico agendável (entidade
 * Medico, que é a referenciada por Agendamento).
 *
 * Os campos crm, especialidade, telefone e ativo só se aplicam à função MEDICO
 * e vêm nulos nas demais.
 *
 * Nomes iguais aos de Paciente e Medico (nomeCompleto). Os @JsonAlias aceitam
 * os nomes antigos (fullName, username) na ENTRADA, para não quebrar cliente
 * que ainda os envie; a saída usa só os novos.
 */
public class UsuarioDto {

    private Integer id;
    @JsonAlias("fullName")
    private String nomeCompleto;
    private String matricula;
    private String email;
    @JsonAlias("username")
    private String nomeUsuario;

    /**
     * Só é lido na entrada. Na saída vai sempre null — a senha, ainda que
     * criptografada, não deve trafegar para a tela.
     */
    private String password;

    private Role role;

    // ---- específicos da função MEDICO ----
    private String crm;
    private String especialidade;
    private String telefone;
    private Boolean ativo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
