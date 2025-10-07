package br.sp.gov.fatec.ubs.backend.repositories;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.sp.gov.fatec.ubs.backend.model.User;


@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByEmail(String email);
    User findById(Long id);
    void deleteById(Long id);
    java.util.List<User> findAll();
    User save(User user);
}