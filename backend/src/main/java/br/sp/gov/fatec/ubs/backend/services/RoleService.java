package br.sp.gov.fatec.ubs.backend.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import br.sp.gov.fatec.ubs.backend.model.Role;
import br.sp.gov.fatec.ubs.backend.model.RoleEnum;
import br.sp.gov.fatec.ubs.backend.repositories.RoleRepository;


public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Optional<Role> findByName(RoleEnum name) {
        return roleRepository.findByName(name);
    }
    

}
