package br.sp.gov.fatec.ubs.backend.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.sp.gov.fatec.ubs.backend.dtos.LoginUserDto;
import br.sp.gov.fatec.ubs.backend.dtos.RegisterUserDto;
import br.sp.gov.fatec.ubs.backend.exceptions.EmailJaCadastradoException;
import br.sp.gov.fatec.ubs.backend.model.Role;
import br.sp.gov.fatec.ubs.backend.model.RoleEnum;
import br.sp.gov.fatec.ubs.backend.model.User;
import br.sp.gov.fatec.ubs.backend.repositories.RoleRepository;
import br.sp.gov.fatec.ubs.backend.repositories.UserRepository;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    // Cadastro de novo usuário, sempre com o papel USER.
    public User signup(RegisterUserDto input) {
        // Rede de segurança: o @Valid do controller já barra estes casos, mas
        // o serviço também é chamado de outros pontos do código.
        exigir(input.getEmail(), "O e-mail é obrigatório.");
        exigir(input.getPassword(), "A senha é obrigatória para cadastro de usuário.");
        exigir(input.getFullName(), "O nome completo é obrigatório.");
        exigir(input.getMatricula(), "A matrícula é obrigatória.");
        exigir(input.getUsername(), "O nome de usuário é obrigatório.");

        // A autenticação é pelo e-mail: normalizar evita cadastros que diferem
        // apenas por espaços ou caixa das letras.
        String email = input.getEmail().trim().toLowerCase();

        // A coluna é unique; sem esta checagem a violação de integridade só
        // apareceria como erro genérico 500.
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailJaCadastradoException(email);
        }

        // Papel ausente é banco mal semeado, ou seja, defeito do servidor e não
        // do cliente: IllegalStateException cai no handler genérico (500).
        Role role = roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> new IllegalStateException("Role USER não encontrada no banco de dados."));

        // matricula e username são NOT NULL no modelo User: precisam vir do DTO.
        var user = new User()
                .setFullName(input.getFullName().trim())
                .setEmail(email)
                .setPassword(passwordEncoder.encode(input.getPassword()))
                .setMatricula(input.getMatricula().trim())
                .setUsername(input.getUsername().trim())
                .setRole(role);

        if (input.getCrm() != null && !input.getCrm().isBlank()) {
            user.setCrm(input.getCrm().trim());
        }

        return userRepository.save(user);
    }

    // Autenticação de usuário. As exceções sobem para o GlobalExceptionHandler,
    // que as traduz em 401/403 — capturá-las aqui apagaria a causa real.
    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()));

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    // Campo obrigatório ausente é erro do cliente: IllegalArgumentException
    // é traduzida em 400 pelo GlobalExceptionHandler.
    private void exigir(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
    }
}
