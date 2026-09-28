package br.sp.gov.fatec.ubs.backend.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.sp.gov.fatec.ubs.backend.dtos.LoginUserDto;
import br.sp.gov.fatec.ubs.backend.dtos.RegisterUserDto;
import br.sp.gov.fatec.ubs.backend.model.User;
import br.sp.gov.fatec.ubs.backend.services.AuthenticationService;
import br.sp.gov.fatec.ubs.backend.services.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    // @Valid dispara as restrições do RegisterUserDto. Uma violação lança
    // MethodArgumentNotValidException, tratada pelo ResponseEntityExceptionHandler
    // que o GlobalExceptionHandler herda: 400 com a lista de campos inválidos.
    @PostMapping("/signup")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    // Sem try/catch: credenciais inválidas sobem como BadCredentialsException e
    // o GlobalExceptionHandler devolve 401. Capturar aqui devolvia 200 com corpo
    // vazio, e o front não tinha como distinguir sucesso de falha.
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto,
            HttpServletResponse response) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setCreatedAt(System.currentTimeMillis());
        loginResponse.setUserId(authenticatedUser.getId());
        // loginResponse.setToken(jwtToken);
        String[] roles = authenticatedUser.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .toArray(String[]::new);
        loginResponse.setRoles(roles);
        loginResponse.setExpiresIn(System.currentTimeMillis() + jwtService.getExpirationTime());

        // Aqui é setado o cookie para a autenticação por cookies.
        // maxAge é em SEGUNDOS e security.jwt.expiration-time em milissegundos.
        ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .maxAge(jwtService.getExpirationTime() / 1000)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logout(HttpServletResponse response) {
        // Aqui é setado o cookie para a autenticação por cookies
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .path("/")
                .secure(true)
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }

}
