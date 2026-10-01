package br.sp.gov.fatec.ubs.backend.exceptions;

/**
 * Tentativa de gravar um CPF que já pertence a OUTRO paciente.
 *
 * Acontece quando se edita o CPF de um registro para um valor que já existe na
 * base. Traduzida em 409 Conflict pelo GlobalExceptionHandler — sem ela, a
 * violação do índice único chegaria ao cliente como 500.
 */
public class CpfDuplicadoException extends RuntimeException {

    public CpfDuplicadoException(String cpf) {
        super("O CPF " + cpf + " já está cadastrado para outro paciente");
    }
}
