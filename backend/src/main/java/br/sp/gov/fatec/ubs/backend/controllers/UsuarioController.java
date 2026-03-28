package br.sp.gov.fatec.ubs.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.sp.gov.fatec.ubs.backend.model.Medico;
import br.sp.gov.fatec.ubs.backend.model.Role;
import br.sp.gov.fatec.ubs.backend.model.User;
import br.sp.gov.fatec.ubs.backend.services.UserService;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/username/{username}")
    public User buscarPorUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @PostMapping
    public ResponseEntity<?> criarUsuario(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Descobre a Role
            Object roleObj = payload.get("role");
            boolean isMedico = false;
            
            if (roleObj != null) {
                String roleStr = roleObj.toString();
                if ("MEDICO".equals(roleStr) || "1".equals(roleStr)) {
                    isMedico = true;
                } else if (roleObj instanceof Map) {
                    isMedico = "MEDICO".equals(((Map<?, ?>) roleObj).get("name"));
                }
            }

            // 2. Prepara a Entidade
            User usuario = isMedico ? new Medico() : new User();
            
            // 3. Preenche os dados básicos manualmente
            usuario.setFullName((String) payload.get("fullName"));
            usuario.setEmail((String) payload.get("email"));
            usuario.setMatricula((String) payload.get("matricula"));
            usuario.setUsername((String) payload.get("username"));
            usuario.setPassword((String) payload.get("password"));
            usuario.setAtivo(payload.containsKey("ativo") ? (Boolean) payload.get("ativo") : true);

            // 4. Preenche a Role
            br.sp.gov.fatec.ubs.backend.model.Role role = new br.sp.gov.fatec.ubs.backend.model.Role();
            if (isMedico) {
                role.setId(1); 
            } else {
                int roleId = roleObj != null && !roleObj.toString().contains("MEDICO") 
                              ? Integer.parseInt(roleObj.toString()) : 4; 
                role.setId(roleId);
            }
            usuario.setRole(role);

            // 5. Preenche dados específicos de Médico
            if (isMedico) {
                Medico medico = (Medico) usuario;
                medico.setCrm((String) payload.get("crm"));
                
                if (payload.containsKey("especialidades") && payload.get("especialidades") instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> especialidades = (List<String>) payload.get("especialidades");
                    medico.setEspecialidades(especialidades);
                } else if (payload.containsKey("especialidade")) {
                    medico.setEspecialidades(List.of((String) payload.get("especialidade")));
                }
            }

            User usuarioSalvo = userService.save(usuario);
            return ResponseEntity.ok(usuarioSalvo);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public void deletarUsuario(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
}