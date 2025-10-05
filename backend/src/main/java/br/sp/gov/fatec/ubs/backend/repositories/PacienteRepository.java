package br.sp.gov.fatec.ubs.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.sp.gov.fatec.ubs.backend.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    // Aqui você pode adicionar métodos personalizados de consulta, se necessário
}
