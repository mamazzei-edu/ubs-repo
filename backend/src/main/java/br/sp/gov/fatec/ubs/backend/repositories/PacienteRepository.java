package br.sp.gov.fatec.ubs.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.sp.gov.fatec.ubs.backend.entities.PacienteEntity;

@Repository
public interface PacienteRepository extends JpaRepository<PacienteEntity, Long> {

    // Filtrar por nome
    List<PacienteEntity> findByNomeCompletoContainingIgnoreCase(String nomeCompleto);

    // Filtrar por CPF
    List<PacienteEntity> findByCpf(String cpf);
}
