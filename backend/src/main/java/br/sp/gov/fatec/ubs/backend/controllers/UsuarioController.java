package br.sp.gov.fatec.ubs.backend.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.sp.gov.fatec.ubs.backend.model.User;
import br.sp.gov.fatec.ubs.backend.services.RoleService;
import br.sp.gov.fatec.ubs.backend.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class UsuarioController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> listarUsuarios() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User buscarPorId(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
//    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public User criarUsuario(@RequestBody User usuario) {
            return userService.save(usuario);
    }

    @PutMapping("/{id}")
    public User atualizarUsuario(@PathVariable Long id, @RequestBody User usuarioAtualizado) {
        User usuario = userService.findById(id);
        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }
        usuario.setFullName(usuarioAtualizado.getFullName());
        usuario.setMatricula(usuarioAtualizado.getMatricula());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setUsername(usuarioAtualizado.getUsername());
        usuario.setPassword(usuarioAtualizado.getPassword());
        return userService.save(usuario);
    }

    @DeleteMapping("/{id}")
    public void deletarUsuario(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
}
