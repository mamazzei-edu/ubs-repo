package br.sp.gov.fatec.ubs.backend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Table(name = "users")
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    // Mesmo nome de Paciente e Medico. A coluna continua "full_name" para nao
    // exigir migracao da base.
    @Column(name = "full_name", nullable = false)
    private String nomeCompleto;

    @Column(unique = true, length = 200, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String matricula;

    // Nome de exibicao. A coluna continua "username"; o campo nao, porque
    // getUsername() do UserDetails devolve o e-mail (ver abaixo).
    @Column(name = "username", nullable = false)
    private String nomeUsuario;

    @Column(nullable = true) // para cadastro dos usuários médicos
    private String crm;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    // Relação corrigida — sem CascadeType.REMOVE
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private Role role;

    // ======================== Métodos de segurança ========================

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + role.getName().toString());
        return List.of(authority);
    }

    // Sem @JsonIgnore a senha (ainda que criptografada) ia no JSON de
    // /api/usuarios, /users/me e /auth/signup.
    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Contrato do UserDetails: a autenticacao e feita pelo e-mail.
     * NAO e a coluna "username" — para ela, ver getNomeUsuario().
     * Com @JsonIgnore para o JSON nao expor duas propriedades "username"
     * com valores diferentes, que era o que fazia a tela mostrar o e-mail
     * no campo "Nome do usuario".
     */
    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }

    /** O valor da coluna "username" — o getUsername() acima devolve o e-mail. */
    public String getNomeUsuario() {
        return nomeUsuario;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // ======================== Getters e Setters ========================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public User setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCrm() {
        return crm;
    }

    public User setCrm(String crm) {
        this.crm = crm;
        return this;
    }

    public Role getRole() {
        return role;
    }

    public User setRole(Role role) {
        this.role = role;
        return this;
    }

    public String getMatricula() {
        return matricula;
    }

    public User setMatricula(String matricula) {
        this.matricula = matricula;
        return this;
    }

    public User setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
        return this;
    }
}
