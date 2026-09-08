# =============================================================================
# nginx-ubs — servidor Nginx para o app Angular (UBS)
# =============================================================================
# Diferente de frontend/Dockerfile (que compila o Angular dentro da imagem),
# esta imagem apenas EMPACOTA o resultado de um build feito previamente na
# maquina/pipeline:
#
#     cd frontend
#     npm ci
#     npm run build          # gera frontend/dist/frontend/browser
#
# Por isso o contexto de build declarado no compose.yaml e a RAIZ do
# repositorio (context: .), permitindo copiar frontend/dist/ daqui.
# =============================================================================

ARG NGINX_VERSION=alpine3.22

FROM nginxinc/nginx-unprivileged:${NGINX_VERSION} AS runner

# Imagem nginx-unprivileged ja roda como usuario nao-root (uid 101 / nginx).
USER nginx

# Configuracao customizada: escuta na porta 8081 e trata o roteamento do Angular.
COPY --chown=nginx:nginx nginx-ubs/nginx.conf /etc/nginx/nginx.conf

# Conteudo estatico gerado por "npm run build" no diretorio frontend.
# O Angular 20 (@angular/build:application) grava a saida em
# dist/<nome-do-projeto>/browser -> dist/frontend/browser.
# O curinga evita quebrar caso o nome do projeto mude.
COPY --chown=nginx:nginx frontend/dist/*/browser/ /usr/share/nginx/html/

# Porta HTTP exposta pelo container.
EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget -q --spider http://127.0.0.1:8081/ || exit 1

ENTRYPOINT ["nginx", "-c", "/etc/nginx/nginx.conf"]
CMD ["-g", "daemon off;"]
