package br.sp.gov.fatec.ubs.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.sp.gov.fatec.ubs.backend.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    /**
     * O CPF é único na base. Sempre consultar com o valor normalizado
     * (somente dígitos) — ver ValidaCPF.normalizar.
     *
     * "findFirst" e não "findBy" de propósito: bases antigas podem ter CPF
     * repetido de antes do índice único (ver SaneamentoCpfSeeder). Com
     * "findBy", uma duplicata dessas derrubava a consulta com 500.
     */
    Optional<Paciente> findFirstByCpfOrderByIdAsc(String cpf);

    /** Outro paciente, que não o de id informado, já usa este CPF? */
    Optional<Paciente> findFirstByCpfAndIdNotOrderByIdAsc(String cpf, Long id);

    /**
     * Pacientes cujo CPF ainda não está normalizado (com máscara ou em branco).
     * Consulta nativa porque o HQL não tem REGEXP.
     */
    @Query(value = "SELECT * FROM paciente WHERE cpf IS NOT NULL AND (TRIM(cpf) = '' OR cpf REGEXP '[^0-9]')",
            nativeQuery = true)
    List<Paciente> findComCpfNaoNormalizado();

    /** CPFs que aparecem em mais de um paciente. */
    @Query("SELECT p.cpf FROM paciente p WHERE p.cpf IS NOT NULL GROUP BY p.cpf HAVING COUNT(p) > 1")
    List<String> findCpfsDuplicados();

    List<Paciente> findByCpfOrderByIdAsc(String cpf);

    /** Busca para autocompletar: parte do nome ou começo do CPF. */
    @Query("SELECT p FROM paciente p WHERE LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :nome, '%')) "
            + "OR (:cpf <> '' AND p.cpf LIKE CONCAT(:cpf, '%')) ORDER BY p.nomeCompleto ASC")
    List<Paciente> buscarPorNomeOuCpf(@Param("nome") String nome, @Param("cpf") String cpf, Pageable pagina);
}
