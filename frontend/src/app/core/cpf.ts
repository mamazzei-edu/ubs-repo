// src/app/core/cpf.ts
//
// Regras de CPF do front. Espelham ValidaCPF do backend: o backend é quem
// decide, mas validar aqui avisa o usuário antes do envio.

/** Somente os dígitos. */
export function normalizarCpf(cpf: string | null | undefined): string {
    return (cpf ?? '').replace(/\D/g, '');
}

/** Valida pelos dígitos verificadores; aceita com ou sem máscara. */
export function cpfValido(cpf: string | null | undefined): boolean {
    const d = normalizarCpf(cpf);
    if (d.length !== 11 || /^(\d)\1{10}$/.test(d)) {
        return false;
    }
    return digitoVerificador(d, 9) === Number(d[9]) && digitoVerificador(d, 10) === Number(d[10]);
}

/**
 * Máscara progressiva para usar enquanto o usuário digita:
 * "1234" -> "123.4", "12345678909" -> "123.456.789-09".
 */
export function mascararCpf(cpf: string | null | undefined): string {
    const d = normalizarCpf(cpf).slice(0, 11);
    if (d.length <= 3) return d;
    if (d.length <= 6) return `${d.slice(0, 3)}.${d.slice(3)}`;
    if (d.length <= 9) return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6)}`;
    return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6, 9)}-${d.slice(9)}`;
}

function digitoVerificador(digitos: string, quantidade: number): number {
    let soma = 0;
    for (let i = 0; i < quantidade; i++) {
        soma += Number(digitos[i]) * (quantidade + 1 - i);
    }
    const resto = soma % 11;
    return resto < 2 ? 0 : 11 - resto;
}
