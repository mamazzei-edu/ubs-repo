package br.sp.gov.fatec.ubs.backend.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.sp.gov.fatec.ubs.backend.model.Role;
import br.sp.gov.fatec.ubs.backend.model.RoleEnum;
import br.sp.gov.fatec.ubs.backend.model.User;
import br.sp.gov.fatec.ubs.backend.repositories.RoleRepository;
import br.sp.gov.fatec.ubs.backend.repositories.UserRepository;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, 
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Buscar a role SUPER_ADMIN
        Optional<Role> superAdminRole = roleRepository.findByName(RoleEnum.SUPER_ADMIN);
        
        if (superAdminRole.isEmpty()) {
            System.out.println("⚠️ Role SUPER_ADMIN não encontrada no banco de dados.");
            return;
        }
        
        // Verificar se o usuário super admin já existe
        Optional<User> existingUser = userRepository.findByEmail("super.admin@email.com");
        
        if (existingUser.isEmpty()) {
            // Criar o usuário super admin
            User superAdmin = new User()
                    .setFullName("Super Admin")
                    .setEmail("super.admin@email.com")
                    .setPassword(passwordEncoder.encode("123456"))
                    .setMatricula("ADMIN001")
                    .setUsername("super.admin")
                    .setRole(superAdminRole.get());
            
            userRepository.save(superAdmin);
            System.out.println("✅ Usuário Super Admin criado com sucesso!");
            System.out.println("   Email: super.admin@email.com");
            System.out.println("   Senha: 123456");
        } else {
            // Atualizar a senha do usuário existente
            User superAdmin = existingUser.get();
            superAdmin.setPassword(passwordEncoder.encode("123456"));
            superAdmin.setRole(superAdminRole.get());
            userRepository.save(superAdmin);
            System.out.println("✅ Senha do Super Admin atualizada com sucesso!");
            System.out.println("   Email: super.admin@email.com");
            System.out.println("   Senha: 123456");
        }
    }
}
