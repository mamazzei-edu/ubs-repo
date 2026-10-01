package br.sp.gov.fatec.ubs.backend.exceptions;

/**
 * Tentativa de gravar um CRM que já pertence a outro médico.
 *
 * Traduzida em 409 Conflict pelo GlobalExceptionHandler — sem ela, a violação
 * do índice único da coluna crm chegaria ao cliente como 500.
 */
public class CrmDuplicadoException extends RuntimeException {

    public CrmDuplicadoException(String crm) {
        super("O CRM " + crm + " já está cadastrado para outro médico");
    }
}
