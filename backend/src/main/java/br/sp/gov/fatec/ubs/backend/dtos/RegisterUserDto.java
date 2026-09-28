package br.sp.gov.fatec.ubs.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados de entrada do cadastro de usuário.
 *
 * A autenticação do sistema é feita pelo e-mail; o campo "username" é apenas
 * um nome de exibição (ver User.getUsername(), que devolve o e-mail por
 * exigência do contrato UserDetails).
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
    private String fullName;

    @NotBlank(message = "A matrícula é obrigatória")
    private String matricula;

    @NotBlank(message = "O nome de usuário é obrigatório")
    private String username;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }
}
