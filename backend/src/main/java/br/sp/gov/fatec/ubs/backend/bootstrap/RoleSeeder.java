package br.sp.gov.fatec.ubs.backend.bootstrap;

import br.sp.gov.fatec.ubs.backend.model.Role;
import br.sp.gov.fatec.ubs.backend.model.RoleEnum;
import br.sp.gov.fatec.ubs.backend.repositories.RoleRepository;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;


    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
    }

    private void loadRoles() {
        RoleEnum[] roleNames = new RoleEnum[] { RoleEnum.USER, RoleEnum.ADMIN, RoleEnum.SUPER_ADMIN };
        Map<RoleEnum, String> roleDescriptionMap = Map.of(
            RoleEnum.USER, "Usuário default",
            RoleEnum.ADMIN, "Usuário administrativo",
            RoleEnum.SUPER_ADMIN, "Super usuário administrativo com privilégios extras"
        );

        Arrays.stream(roleNames).forEach((roleName) -> {
            Optional<Role> optionalRole = roleRepository.findByName(roleName);

            optionalRole.ifPresentOrElse(System.out::println, () -> {
                Role roleToCreate = new Role();

                roleToCreate.setName(roleName);
                roleToCreate.setDescription(roleDescriptionMap.get(roleName));
                roleRepository.save(roleToCreate);
            });
        });
    }
}
