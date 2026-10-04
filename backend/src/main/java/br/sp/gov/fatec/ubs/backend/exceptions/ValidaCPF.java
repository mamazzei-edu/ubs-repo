package br.sp.gov.fatec.ubs.backend.exceptions;

/**
 * Classe utilitária para validação de CPF, no mesmo molde de ValidaCRM.
 *
 * O CPF é a chave de negócio do paciente. Por isso circula sempre normalizado
 * (somente os 11 dígitos): sem isso "123.456.789-09" e "12345678909" seriam
 * tratados como pacientes diferentes, que é exatamente a duplicata a evitar.
 */
public class ValidaCPF {

    private ValidaCPF() {}

    /**
     * Reduz o CPF a somente dígitos, e devolve null quando não sobra nada.
     *
     * O null importa para o índice único: o MySQL aceita vários NULL num
     * índice único, mas não várias strings vazias.
     */
    public static String normalizar(String cpf) {
        if (cpf == null) {
            return null;
        }
        String digitos = cpf.replaceAll("\\D", "");
        return digitos.isEmpty() ? null : digitos;
    }

    /**
     * Valida o CPF pelos dígitos verificadores (aceita com ou sem máscara).
     * @param cpf A String do CPF a ser validada.
     * @return true se o CPF for válido, false caso contrário.
     */
    public static boolean isValid(String cpf) {
        String digitos = normalizar(cpf);
        if (digitos == null || digitos.length() != 11) {
            return false;
        }

        // 000.000.000-00, 111.111.111-11 etc. passam na conta dos dígitos
        // verificadores, mas não são CPFs emitidos.
        if (digitos.chars().distinct().count() == 1) {
            return false;
        }

        return digitoVerificador(digitos, 9) == digitos.charAt(9) - '0'
                && digitoVerificador(digitos, 10) == digitos.charAt(10) - '0';
    }

    /**
     * Formata para exibição: 12345678909 -> 123.456.789-09.
     * @return CPF formatado ou null se inválido
     */
    public static String format(String cpf) {
        if (!isValid(cpf)) {
            return null;
        }
        String d = normalizar(cpf);
        return d.substring(0, 3) + "." + d.substring(3, 6) + "." + d.substring(6, 9) + "-" + d.substring(9);
    }

    /** Calcula o dígito verificador a partir dos {@code quantidade} primeiros dígitos. */
    private static int digitoVerificador(String digitos, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (digitos.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
