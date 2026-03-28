package br.sp.gov.fatec.ubs.backend.repositories;

import br.sp.gov.fatec.ubs.backend.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Integer> {

    Optional<Medico> findByCrm(String crm);

    Optional<Medico> findByEmail(String email);

    List<Medico> findByAtivoTrue();

    // As especialidades são uma lista, o JPA precisa dar um JOIN na tabela auxiliar.
    @Query("SELECT m FROM Medico m JOIN m.especialidades e WHERE e = :especialidade AND m.ativo = true")
    List<Medico> findByEspecialidadeAndAtivoTrue(@Param("especialidade") String especialidade);

    @Query("SELECT m FROM Medico m WHERE LOWER(m.fullName) LIKE LOWER(CONCAT('%', :nome, '%')) AND m.ativo = true")
    List<Medico> findByFullNameContainingIgnoreCaseAndAtivoTrue(@Param("nome") String nome);

    boolean existsByCrmAndIdNot(String crm, Integer id);

    boolean existsByEmailAndIdNot(String email, Integer id);
}