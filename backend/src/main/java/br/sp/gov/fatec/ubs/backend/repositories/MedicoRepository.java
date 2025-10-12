package br.sp.gov.fatec.ubs.backend.repositories;

import br.sp.gov.fatec.ubs.backend.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    
    // Buscar médico por CRM
    Optional<Medico> findByCrm(String crm);
    
    // Buscar médico por email
    Optional<Medico> findByEmail(String email);
    
    // Buscar médicos por especialidade
    List<Medico> findByEspecialidade(String especialidade);
    
    // Buscar médicos ativos
    List<Medico> findByAtivoTrue();
    
    // Buscar médicos por especialidade e ativo
    List<Medico> findByEspecialidadeAndAtivoTrue(String especialidade);
    
    // Buscar médicos por nome (contendo)
    @Query("SELECT m FROM medico m WHERE LOWER(m.nomeCompleto) LIKE LOWER(CONCAT('%', :nome, '%')) AND m.ativo = true")
    List<Medico> findByNomeCompletoContainingIgnoreCaseAndAtivoTrue(@Param("nome") String nome);
    
    // Verificar se CRM já existe (para validação durante cadastro/update)
    boolean existsByCrmAndIdNot(String crm, Long id);
    
    // Verificar se email já existe (para validação durante cadastro/update)
    boolean existsByEmailAndIdNot(String email, Long id);
}