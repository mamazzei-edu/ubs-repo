// src/app/core/api.ts
//
// Monta as URLs da API a partir de configuracao de RUNTIME, nao de build.
// O valor vem de config.js, carregado no <head> antes dos bundles: em
// producao o container nginx-ubs o gera a partir do .env; em desenvolvimento
// ele vem de public/config.js.

declare global {
    interface Window {
        __UBS_CONFIG__?: { apiBaseUrl?: string };
    }
}

/**
 * Resolve uma rota da API.
 *
 * @example apiUrl('auth/login') -> https://exemplo.com.br/diretorio-api/auth/login
 */
export function apiUrl(path: string): string {
    const configurado = (window.__UBS_CONFIG__?.apiBaseUrl ?? '').trim();
    const rota = path.replace(/^\/+/, '');

    // Sem configuracao, cai no <base href> — util em testes e no modo antigo,
    // em que a API ficava sob o mesmo prefixo do app.
    if (!configurado) {
        return new URL(rota, document.baseURI).toString();
    }

    // A barra final e obrigatoria: sem ela o URL descarta o ultimo segmento.
    const base = configurado.endsWith('/') ? configurado : configurado + '/';

    // Resolver contra document.baseURI aceita tanto caminho relativo
    // ("/diretorio-api") quanto URL absoluta.
    return new URL(rota, new URL(base, document.baseURI)).toString();
}

/**
 * Texto de erro de uma resposta HTTP do backend. O GlobalExceptionHandler
 * responde em ProblemDetail, com a mensagem em "detail".
 */
export function mensagemDoErro(erro: any, padrao: string): string {
    return erro?.error?.detail || padrao;
}
