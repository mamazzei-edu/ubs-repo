package br.sp.gov.fatec.ubs.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sp.gov.fatec.ubs.backend.dtos.LoginUserDto;
import br.sp.gov.fatec.ubs.backend.dtos.RegisterUserDto;
import br.sp.gov.fatec.ubs.backend.entities.User;
import br.sp.gov.fatec.ubs.backend.services.AuthenticationService;
import br.sp.gov.fatec.ubs.backend.services.JwtService;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
//        String valor = loginUserDto.getEmail();
//        System.out.print("Tentando autenticar com: " + valor);
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setCreatedAt(System.currentTimeMillis());
        loginResponse.setUserId(authenticatedUser.getId());
        loginResponse.setToken(jwtToken);
        String[] roles = new String[authenticatedUser.getAuthorities().size()];
        int contador=0;
        for (String roleString : authenticatedUser.getAuthorities().stream().map(auth -> auth.getAuthority()).toList()) {
            roles[contador] = roleString;
            contador++; 
        }
        loginResponse.setRoles(roles);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}
