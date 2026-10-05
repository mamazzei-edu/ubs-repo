package br.sp.gov.fatec.ubs.backend.exceptions;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

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

    // CRM ja pertencente a outro medico: 409.
    @ExceptionHandler(CrmDuplicadoException.class)
    public ProblemDetail handleCrmDuplicado(CrmDuplicadoException ex) {
        return problema(HttpStatus.CONFLICT, ex.getMessage());
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

    /**
     * Violação de Bean Validation disparada FORA do controlador — tipicamente
     * pelo Hibernate, no momento do INSERT/UPDATE (o "during persist time" do
     * log). Sem este tratamento a exceção caía no handler genérico de Exception
     * e virava um 500 "Erro interno do servidor", escondendo da tela o motivo
     * real da recusa.
     *
     * A resposta passa a ser 400 com as mensagens das anotações no campo
     * "detail" (que é o que o frontend exibe) e, em "erros", o mapa
     * campo -> mensagem, para quem quiser destacar o campo no formulário.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleViolacoes(ConstraintViolationException ex) {
        Map<String, String> erros = new LinkedHashMap<>();
        for (ConstraintViolation<?> violacao : ex.getConstraintViolations()) {
            erros.merge(violacao.getPropertyPath().toString(),
                    violacao.getMessage(), (a, b) -> a + " " + b);
        }

        String detalhe = erros.isEmpty() ? "Dados inválidos."
                : String.join(" ", erros.values());
        log.warn("Dados inválidos: {}", detalhe);

        ProblemDetail pd = problema(HttpStatus.BAD_REQUEST, detalhe);
        pd.setProperty("erros", erros);
        return pd;
    }

    /**
     * Violação de @Valid no corpo da requisição.
     *
     * O comportamento padrão do ResponseEntityExceptionHandler devolve 400 com
     * detail "Invalid request content.", que não diz nada a quem está
     * preenchendo o formulário. Aqui o detail passa a trazer as mensagens dos
     * campos recusados.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        Map<String, String> erros = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                erro -> erros.merge(erro.getField(), mensagemDe(erro), (a, b) -> a + " " + b));
        ex.getBindingResult().getGlobalErrors().forEach(
                erro -> erros.merge(erro.getObjectName(), mensagemDe(erro), (a, b) -> a + " " + b));

        String detalhe = erros.isEmpty() ? "Dados inválidos."
                : String.join(" ", erros.values());
        log.warn("Requisição inválida: {}", detalhe);

        ProblemDetail pd = problema(HttpStatus.BAD_REQUEST, detalhe);
        pd.setProperty("erros", erros);
        return ResponseEntity.badRequest().body(pd);
    }

    private String mensagemDe(ObjectError erro) {
        String mensagem = erro.getDefaultMessage();
        return (mensagem == null || mensagem.isBlank()) ? "valor inválido" : mensagem;
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