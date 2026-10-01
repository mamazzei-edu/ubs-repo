package br.sp.gov.fatec.ubs.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.sp.gov.fatec.ubs.backend.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    /**
     * O CPF é único na base. Sempre consultar com o valor normalizado
     * (somente dígitos) — ver PacienteService.normalizarCpf.
     */
    Optional<Paciente> findByCpf(String cpf);
}
