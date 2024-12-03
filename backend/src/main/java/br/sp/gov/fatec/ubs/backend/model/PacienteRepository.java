package br.sp.gov.fatec.ubs.backend.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    // Aqui você pode adicionar métodos personalizados de consulta, se necessário
}
