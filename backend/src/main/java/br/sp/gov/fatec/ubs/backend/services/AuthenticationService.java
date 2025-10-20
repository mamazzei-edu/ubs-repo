package br.sp.gov.fatec.ubs.backend.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.sp.gov.fatec.ubs.backend.dtos.LoginUserDto;
import br.sp.gov.fatec.ubs.backend.dtos.RegisterUserDto;
import br.sp.gov.fatec.ubs.backend.model.Role;
import br.sp.gov.fatec.ubs.backend.model.RoleEnum;
import br.sp.gov.fatec.ubs.backend.model.User;
import br.sp.gov.fatec.ubs.backend.repositories.RoleRepository;
import br.sp.gov.fatec.ubs.backend.repositories.UserRepository;

import java.util.Optional;

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

    // Cadastro de novo usuário
    public User signup(RegisterUserDto input) {
        // Validação de senha obrigatória
        if (input.getPassword() == null || input.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória para cadastro de usuário.");
        }

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        if (optionalRole.isEmpty()) {
            throw new RuntimeException("Role USER não encontrada no banco de dados.");
        }

        var user = new User()
                .setFullName(input.getFullName())
                .setEmail(input.getEmail())
                .setPassword(passwordEncoder.encode(input.getPassword()))
                .setRole(optionalRole.get());

        return userRepository.save(user);
    }

    // Autenticação de usuário
    public User authenticate(LoginUserDto input) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()));

            // Busca usuário por email e trata Optional corretamente
            Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
            User usuario = optionalUser.orElseThrow(() ->
                    new UsernameNotFoundException("Usuário não encontrado"));

            return usuario;

        } catch (Exception e) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }
    }
}
