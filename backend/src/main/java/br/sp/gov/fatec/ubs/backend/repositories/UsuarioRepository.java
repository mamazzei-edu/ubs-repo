package br.sp.gov.fatec.ubs.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.sp.gov.fatec.ubs.backend.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}
