# =============================================================================
# nginx-ubs — servidor Nginx do app Angular (UBS) e proxy da API
# =============================================================================
# A imagem apenas EMPACOTA o resultado de um build feito previamente:
#
#     cd frontend
#     npm ci
#     npm run build          # gera frontend/dist/frontend/browser
#
# Por isso o contexto de build declarado no compose.yaml e a RAIZ do
# repositorio (context: .), permitindo copiar frontend/dist/ daqui.
#
# Os caminhos publicos (FRONT_BASE_PATH e API_BASE_PATH) NAO entram na imagem:
# sao lidos na partida do container, a partir do .env. Uma mesma imagem serve
# qualquer ambiente — trocar de caminho e "docker compose up -d", sem rebuild.
# =============================================================================

ARG NGINX_VERSION=alpine3.22

FROM nginxinc/nginx-unprivileged:${NGINX_VERSION} AS runner

# Os passos abaixo escrevem em /etc/nginx e /usr/share/nginx/html.
USER root

# Template do virtual host. O entrypoint da imagem roda envsubst sobre
# /etc/nginx/templates/*.template e grava o resultado em /etc/nginx/conf.d/,
# que o nginx.conf padrao da imagem ja inclui. Nao sobrescrevemos o nginx.conf.
COPY nginx-ubs/default.conf.template /etc/nginx/templates/default.conf.template

# Script de partida: regera index.html e config.js a partir das variaveis.
COPY --chmod=0755 nginx-ubs/30-configura-app.sh /docker-entrypoint.d/30-configura-app.sh

# Conteudo estatico do build. O Angular 20 (@angular/build:application) grava
# a saida em dist/<nome-do-projeto>/browser -> dist/frontend/browser.
COPY frontend/dist/*/browser/ /usr/share/nginx/html/

# Guarda o index.html como template, com o <base href> trocado por um marcador.
# E isto que desacopla o valor usado no "ng build" do caminho de implantacao.
# O grep falha o build se o index.html nao tiver a tag <base href>.
RUN sed -E 's|<base href="[^"]*">|<base href="__BASE_HREF__">|' \
        /usr/share/nginx/html/index.html > /usr/share/nginx/html/index.html.template \
    && grep -q "__BASE_HREF__" /usr/share/nginx/html/index.html.template \
    && chown -R nginx:nginx /usr/share/nginx/html

USER nginx

EXPOSE 8081

# Sem ENTRYPOINT proprio: o entrypoint da imagem e que executa os scripts de
# /docker-entrypoint.d/ (envsubst dos templates e o 30-configura-app.sh).
CMD ["nginx", "-g", "daemon off;"]
