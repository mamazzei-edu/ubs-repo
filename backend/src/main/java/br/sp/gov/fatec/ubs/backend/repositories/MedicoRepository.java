package br.sp.gov.fatec.ubs.backend.repositories;

import br.sp.gov.fatec.ubs.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<User, Long> {

    // Buscar médico por CRM
    Optional<User> findByCrm(String crm);

    // Buscar médico por email
    Optional<User> findByEmail(String email);

    // Buscar médicos por especialidade
    List<User> findByEspecialidade(String especialidade);

    // Buscar médicos ativos
    List<User> findByAtivoTrue();

    // Buscar médicos por especialidade e ativo
    List<User> findByEspecialidadeAndAtivoTrue(String especialidade);

    // Buscar médicos por nome (contendo)
    // Buscar médicos por nome (contendo)
    @Query("SELECT m FROM User m WHERE LOWER(m.fullName) LIKE LOWER(CONCAT('%', :nome, '%')) AND m.ativo = true")
    List<User> findByFullNameContainingIgnoreCaseAndAtivoTrue(@Param("nome") String nome);

    // Verificar se CRM já existe (para validação durante cadastro/update)
    boolean existsByCrmAndIdNot(String crm, Long id);

    // Verificar se email já existe (para validação durante cadastro/update)
    boolean existsByEmailAndIdNot(String email, Long id);
}