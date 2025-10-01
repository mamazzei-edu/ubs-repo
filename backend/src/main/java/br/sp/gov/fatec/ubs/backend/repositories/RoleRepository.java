package br.sp.gov.fatec.ubs.backend.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.sp.gov.fatec.ubs.backend.model.Role;
import br.sp.gov.fatec.ubs.backend.model.RoleEnum;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByName(RoleEnum name);
}
