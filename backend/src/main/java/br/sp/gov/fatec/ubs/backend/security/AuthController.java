package br.sp.gov.fatec.ubs.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Autentica usando usuario e senha

            if (loginRequest.getEmail() == "teste@teste.com.br" && loginRequest.getSenha() == "123456") {
                // Authentication authentication = authenticationManager.authenticate(
                //     new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
                // );
                return ResponseEntity.ok(new AuthResponse("123"));
    
            } else {
                return ResponseEntity.ok(new AuthResponse("456"));

            }

            // Gera token JWT usando o usuário como subject
//            String token = jwtUtil.generateToken(loginRequest.getEmail(), "your-issuer");


        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }
        
}
