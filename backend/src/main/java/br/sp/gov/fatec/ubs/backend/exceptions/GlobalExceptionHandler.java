package br.sp.gov.fatec.ubs.backend.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.jsonwebtoken.JwtException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Credenciais inválidas e usuário inexistente devolvem a MESMA resposta,
    // para não permitir descobrir quais e-mails existem na base.
    @ExceptionHandler({ BadCredentialsException.class, UsernameNotFoundException.class })
    public ProblemDetail handleCredenciais(AuthenticationException ex) {
        log.warn("Falha de autenticação: {}", ex.getMessage());
        return problema(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos");
    }

    @ExceptionHandler(AccountStatusException.class)
    public ProblemDetail handleConta(AccountStatusException ex) {
        log.warn("Conta indisponível: {}", ex.getMessage());
        return problema(HttpStatus.FORBIDDEN, "Conta bloqueada ou desabilitada");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAcesso(AccessDeniedException ex) {
        return problema(HttpStatus.FORBIDDEN, "Sem permissão para acessar este recurso");
    }

    // ExpiredJwtException e SignatureException descendem de JwtException.
    @ExceptionHandler(JwtException.class)
    public ProblemDetail handleJwt(JwtException ex) {
        log.warn("JWT inválido: {}", ex.getMessage());
        return problema(HttpStatus.UNAUTHORIZED, "Sessão expirada ou token inválido");
    }

    // CPF ja pertencente a outro paciente: 409.
    @ExceptionHandler(CpfDuplicadoException.class)
    public ProblemDetail handleCpfDuplicado(CpfDuplicadoException ex) {
        return problema(HttpStatus.CONFLICT, ex.getMessage());
    }

    // E-mail ja existente no cadastro: 409, nao 500.
    @ExceptionHandler(EmailJaCadastradoException.class)
    public ProblemDetail handleEmailDuplicado(EmailJaCadastradoException ex) {
        return problema(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleArgumento(IllegalArgumentException ex) {
        return problema(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Último recurso: registra o stack trace no log e devolve 500 genérico.
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleDesconhecida(Exception ex) {
        log.error("Erro não tratado", ex);
        return problema(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
    }

    private ProblemDetail problema(HttpStatus status, String detalhe) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detalhe);
        pd.setTitle(status.getReasonPhrase());
        return pd;
    }
}