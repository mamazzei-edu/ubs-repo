package br.sp.gov.fatec.ubs.backend.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados de entrada do cadastro de usuário.
 *
 * A autenticação do sistema é feita pelo e-mail; o campo "nomeUsuario" é
 * apenas um nome de exibição (ver User.getUsername(), que devolve o e-mail por
 * exigência do contrato UserDetails).
 *
 * Os @JsonAlias aceitam os nomes antigos (fullName, username) na entrada.
 *
 * As restrições abaixo só são aplicadas onde o parâmetro estiver anotado com
 * @Valid — ver AuthenticationController.register. Objetos montados em código,
 * como no AdminSeeder, não passam por validação.
 */
public class RegisterUserDto {

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter ao menos 6 caracteres")
    private String password;

    @NotBlank(message = "O nome completo é obrigatório")
    @JsonAlias("fullName")
    private String nomeCompleto;

    @NotBlank(message = "A matrícula é obrigatória")
    private String matricula;

    @NotBlank(message = "O nome de usuário é obrigatório")
    @JsonAlias("username")
    private String nomeUsuario;

    private String crm; // Campo opcional para médicos

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }
}
