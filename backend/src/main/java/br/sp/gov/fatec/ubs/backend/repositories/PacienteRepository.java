package br.sp.gov.fatec.ubs.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.sp.gov.fatec.ubs.backend.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    // Buscar paciente por prontuário
    Optional<Paciente> findByProntuario(String prontuario);

    // Buscar paciente por nome completo
    Optional<Paciente> findByNomeCompleto(String nomeCompleto);

    // Buscar pacientes por parte do nome (case insensitive)
    List<Paciente> findByNomeCompletoStartingWithIgnoreCase(String nome);

    // Buscar pacientes por parte do CPF
    List<Paciente> findByCpfStartingWith(String cpf);
}
