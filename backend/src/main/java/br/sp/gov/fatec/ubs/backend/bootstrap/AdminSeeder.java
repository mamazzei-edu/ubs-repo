package br.sp.gov.fatec.ubs.backend.bootstrap;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.sp.gov.fatec.ubs.backend.dtos.RegisterUserDto;
import br.sp.gov.fatec.ubs.backend.entities.Role;
import br.sp.gov.fatec.ubs.backend.entities.RoleEnum;
import br.sp.gov.fatec.ubs.backend.entities.User;
import br.sp.gov.fatec.ubs.backend.repositories.RoleRepository;
import br.sp.gov.fatec.ubs.backend.repositories.UserRepository;

import java.util.Optional;

@Component
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public AdminSeeder(RoleRepository roleRepository, UserRepository  userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createSuperAdministrator();
    }

    private void createSuperAdministrator() {
        RegisterUserDto userDto = new RegisterUserDto();
        userDto.setFullName("Super Admin");
        userDto.setEmail("super.admin@email.com");
        userDto.setPassword("123456");

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.SUPER_ADMIN);
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());

        if (optionalRole.isEmpty() || optionalUser.isPresent() && optionalUser.get().getRole().getName().equals(RoleEnum.SUPER_ADMIN)) {
            return;
        }

        var user = new User()
            .setFullName(userDto.getFullName())
            .setEmail(userDto.getEmail())
            .setPassword(passwordEncoder.encode(userDto.getPassword()))
            .setRole(optionalRole.get());

        userRepository.save(user);
    }
}
