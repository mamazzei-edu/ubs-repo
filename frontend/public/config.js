// Configuracao de runtime do app.
//
// Em PRODUCAO este arquivo e sobrescrito pelo container nginx-ubs na partida,
// a partir de API_BASE_PATH no .env (ver nginx-ubs/30-configura-app.sh).
// O valor abaixo vale apenas para "ng serve", onde o proxy.conf.json encaminha
// este mesmo caminho para o backend em localhost:8080.
//
// apiBaseUrl vazio faz o app cair de volta no <base href>.
window.__UBS_CONFIG__ = { apiBaseUrl: '/diretorio-api' };
