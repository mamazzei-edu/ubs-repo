package br.sp.gov.fatec.ubs.backend.controllers;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sp.gov.fatec.ubs.backend.model.Role;
import br.sp.gov.fatec.ubs.backend.services.RoleService;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class RoleController {
    
    @Autowired
    private RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<Role>> listarRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String currentPrincipalName = authentication.getName();
            System.out.println("Usuário autenticado: " + currentPrincipalName);
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                System.out.println("Papel: " + authority.getAuthority());
            }
            boolean hasSuperAdminRole = authorities.stream()
                                            .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

            boolean hasAdminRole = authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                                
            if (hasSuperAdminRole) {
                List<Role> roles = roleService.listarRoles();
                return new ResponseEntity<>(roles, HttpStatus.OK);
            } else if (hasAdminRole) {
                List<Role> roles = roleService.listarRoles();
                Role roleAdmin = roles.stream()
                                            .filter(role -> role.getName().toString().equals("ROLE_SUPER_ADMIN"))
                                            .findFirst()
                                            .orElse(null);
                roles.remove(roleAdmin);
                return new ResponseEntity<>(roles, HttpStatus.OK);
            } else {
                // Usuário não tem papéis de administrador
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            System.out.println("Nenhum usuário autenticado.");
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }    

}
