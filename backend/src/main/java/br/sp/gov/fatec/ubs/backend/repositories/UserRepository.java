package br.sp.gov.fatec.ubs.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import br.sp.gov.fatec.ubs.backend.model.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    // Busca um usuário pelo e-mail — retornando Optional, assim podemos usar .orElse(null)
    Optional<User> findByEmail(String email);

    // Busca todos os usuários
    List<User> findAll();
}
