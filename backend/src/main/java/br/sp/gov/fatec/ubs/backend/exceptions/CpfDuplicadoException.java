package br.sp.gov.fatec.ubs.backend.exceptions;

/**
 * Tentativa de gravar um CPF que já pertence a OUTRO paciente.
 *
 * Acontece em dois casos: um cadastro NOVO com CPF já existente, e a edição do
 * CPF de um registro para um valor que já pertence a outro. Traduzida em 409
 * Conflict pelo GlobalExceptionHandler — sem ela, a violação do índice único
 * chegaria ao cliente como 500.
 *
 * Carrega o id do paciente já cadastrado, para a tela poder oferecer "abrir o
 * cadastro existente" em vez de deixar o usuário digitar tudo de novo.
 */
public class CpfDuplicadoException extends RuntimeException {

    private final Long pacienteExistenteId;

    public CpfDuplicadoException(String cpf, Long pacienteExistenteId) {
        super("O CPF " + formatar(cpf) + " já está cadastrado para outro paciente");
        this.pacienteExistenteId = pacienteExistenteId;
    }

    public Long getPacienteExistenteId() {
        return pacienteExistenteId;
    }

    private static String formatar(String cpf) {
        String formatado = ValidaCPF.format(cpf);
        return formatado != null ? formatado : cpf;
    }
}
