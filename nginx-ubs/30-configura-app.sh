#!/bin/sh
# =============================================================================
# Roda na partida do container, pelo entrypoint da propria imagem nginx
# (/docker-entrypoint.sh executa tudo que estiver em /docker-entrypoint.d/).
#
# Resolve as duas coisas que o "ng build" grava no artefato e que, sem isto,
# exigiriam recompilar o Angular para trocar de ambiente:
#
#   1. o <base href> do index.html  -> ${FRONT_BASE_PATH}/
#   2. a URL publica da API         -> config.js, lido antes dos bundles
# =============================================================================
set -e

HTML=/usr/share/nginx/html

FRONT_BASE_PATH="${FRONT_BASE_PATH:-}"
API_BASE_PATH="${API_BASE_PATH:-}"

# O <base href> precisa terminar em barra: sem ela o navegador descarta o
# ultimo segmento e resolve tudo a partir da raiz de novo.
case "$FRONT_BASE_PATH" in
    */) BASE_HREF="$FRONT_BASE_PATH" ;;
    *)  BASE_HREF="$FRONT_BASE_PATH/" ;;
esac

if [ ! -f "$HTML/index.html.template" ]; then
    echo "nginx-ubs: ERRO - index.html.template nao encontrado na imagem" >&2
    exit 1
fi

sed "s|__BASE_HREF__|${BASE_HREF}|g" "$HTML/index.html.template" > "$HTML/index.html"

cat > "$HTML/config.js" <<EOF
// Gerado na partida do container a partir do .env. Nao editar a mao.
window.__UBS_CONFIG__ = { apiBaseUrl: "${API_BASE_PATH}" };
EOF

echo "nginx-ubs: app em ${BASE_HREF} | API em ${API_BASE_PATH:-(mesma origem)}"
