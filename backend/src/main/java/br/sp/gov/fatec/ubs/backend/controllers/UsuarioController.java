package br.sp.gov.fatec.ubs.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.sp.gov.fatec.ubs.backend.dtos.UsuarioDto;
import br.sp.gov.fatec.ubs.backend.services.UserService;

import jakarta.validation.Valid;

import java.util.List;

/**
 * Tela de cadastro de usuários.
 *
 * Trabalha com UsuarioDto, e não com a entidade User, porque a tela trata dois
 * registros: o usuário de login e, quando a função é MEDICO, o médico agendável
 * (entidade Medico, referenciada por Agendamento). O serviço mantém os dois em
 * dia a partir de um único envio.
 */
@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class UsuarioController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UsuarioDto> listarUsuarios() {
        return userService.listarDtos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> buscarPorId(@PathVariable Long id) {
        UsuarioDto dto = userService.buscarDto(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<UsuarioDto> criarUsuario(@Valid @RequestBody UsuarioDto usuario) {
        return new ResponseEntity<>(userService.salvarUsuario(usuario, null), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> atualizarUsuario(@PathVariable Long id,
            @Valid @RequestBody UsuarioDto usuario) {
        UsuarioDto salvo = userService.salvarUsuario(usuario, id);
        if (salvo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(salvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        if (userService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
