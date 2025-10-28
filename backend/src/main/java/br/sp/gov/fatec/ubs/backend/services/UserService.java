package br.sp.gov.fatec.ubs.backend.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.sp.gov.fatec.ubs.backend.dtos.RegisterUserDto;
import br.sp.gov.fatec.ubs.backend.model.Role;
import br.sp.gov.fatec.ubs.backend.model.RoleEnum;
import br.sp.gov.fatec.ubs.backend.model.User;
import br.sp.gov.fatec.ubs.backend.repositories.RoleRepository;
import br.sp.gov.fatec.ubs.backend.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    // BOTÃO EXCLUIR: Adiciona transação para permitir a operação
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    // Cria administrador
    public User createAdministrator(RegisterUserDto input) {
        // Validação de senha obrigatória
        if (input.getPassword() == null || input.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória para criar um administrador.");
        }

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.ADMIN);

        if (optionalRole.isEmpty()) {
            throw new RuntimeException("Role ADMIN não encontrada no banco de dados.");
        }

        var user = new User()
                .setFullName(input.getFullName())
                .setEmail(input.getEmail())
                .setPassword(passwordEncoder.encode(input.getPassword()))
                .setMatricula(input.getMatricula())
                .setUsername(input.getUsername())
                .setRole(optionalRole.get());

        if (input.getCrm() != null) {
            user.setCrm(input.getCrm());
        }

        return userRepository.save(user);
    }

    // Busca usuário por ID
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // BOTÃO EDITAR / SALVAR USUÁRIO
    public User save(User input) {
        // 1. Verifica se a senha foi informada
        if (input.getPassword() == null || input.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("A senha não pode ser nula ou vazia.");
        }

        // 2. Garante que a Role é válida
        Optional<Role> optionalRole = roleRepository.findById(input.getRole().getId());
        if (optionalRole.isEmpty()) {
            throw new RuntimeException("Role não encontrada com ID: " + input.getRole().getId());
        }
        input.setRole(optionalRole.get());

        // 3. Criptografa a senha apenas se for nova
        if (!input.getPassword().startsWith("$2a")) {
            input.setPassword(passwordEncoder.encode(input.getPassword()));
        }

        // 4. Salva ou atualiza o usuário
        return userRepository.save(input);
    }

    // Retorna todos os usuários
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
