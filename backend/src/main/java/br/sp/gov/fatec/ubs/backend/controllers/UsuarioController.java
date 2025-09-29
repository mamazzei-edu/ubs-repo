package br.sp.gov.fatec.ubs.backend.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.sp.gov.fatec.ubs.backend.entities.UsuarioEntity;
import br.sp.gov.fatec.ubs.backend.repositories.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<UsuarioEntity> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<UsuarioEntity> buscarPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
//    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public UsuarioEntity criarUsuario(@RequestBody UsuarioEntity usuario) {
        return usuarioRepository.save(usuario);
    }

    @PutMapping("/{id}")
    public UsuarioEntity atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioEntity usuarioAtualizado) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNomeCompleto(usuarioAtualizado.getNomeCompleto());
            usuario.setMatricula(usuarioAtualizado.getMatricula());
            usuario.setEmail(usuarioAtualizado.getEmail());
            usuario.setUsername(usuarioAtualizado.getUsername());
            usuario.setSenha(usuarioAtualizado.getSenha());
            return usuarioRepository.save(usuario);
        }).orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));
    }

    @DeleteMapping("/{id}")
    public void deletarUsuario(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
    }
}
