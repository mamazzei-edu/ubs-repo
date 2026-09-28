package br.sp.gov.fatec.ubs.backend.exceptions;

/**
 * Tentativa de cadastrar um e-mail que já existe na base.
 *
 * Traduzida em 409 Conflict pelo GlobalExceptionHandler. Sem ela, a violação
 * da restrição unique da coluna "email" chegaria ao cliente como 500.
 */
public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException(String email) {
        super("Já existe um usuário cadastrado com o e-mail " + email);
    }
}
