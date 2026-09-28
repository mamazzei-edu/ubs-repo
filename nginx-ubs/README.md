# nginx-ubs

Servidor Nginx do app Angular **já compilado** e proxy da API, na porta **8081**.

Os caminhos públicos são lidos na **partida do container**, a partir do `.env` —
trocá-los não exige recompilar o Angular nem reconstruir a imagem.

| Variável (`.env`) | Exemplo | Onde vale |
|---|---|---|
| `FRONT_BASE_PATH` | `/outro-diretorio` | `<base href>` do Angular, `location` do nginx, `servePath` do `ng serve` |
| `API_BASE_PATH` | `/diretorio-api` | `server.servlet.context-path` do Spring, `proxy_pass` do nginx, `config.js` do app |

Resultado: `https://exemplo.com.br/outro-diretorio/` e
`https://exemplo.com.br/diretorio-api/` — mesmo domínio, o que mantém o app sem
CORS e o cookie JWT funcionando.

## Como usar

```bash
cd frontend && npm ci && npm run build && cd ..
docker compose up -d --build
```

Acesse <http://localhost:8081/outro-diretorio/> (a raiz redireciona para lá).

Depois de mudar o código Angular: `npm run build` e `docker compose build nginx-ubs`.
Depois de mudar só um caminho no `.env`: `docker compose up -d` — sem build.

## Como as duas peças de build viram runtime

O `ng build` grava dois valores no artefato que normalmente exigiriam recompilar
para trocar de ambiente. A imagem desfaz os dois:

**`<base href>`** — no build da imagem, o `index.html` é salvo como
`index.html.template` com a tag trocada por `__BASE_HREF__`. Na partida,
`30-configura-app.sh` regera o `index.html` com o valor de `FRONT_BASE_PATH`.
Custo zero por requisição, ao contrário do `sub_filter` do nginx.

**URL da API** — o mesmo script escreve `config.js`:

```js
window.__UBS_CONFIG__ = { apiBaseUrl: "/diretorio-api" };
```

carregado no `<head>` antes dos bundles, então `apiUrl()` continua sendo uma
função síncrona comum e nenhum dos serviços do Angular precisou mudar.

A configuração do nginx é um template (`default.conf.template`) processado por
`envsubst` pelo entrypoint da própria imagem. Por isso o `Dockerfile` **não**
define `ENTRYPOINT`: quem executa `/docker-entrypoint.d/` é o entrypoint da
imagem nginx.

## Atrás de outro proxy reverso

Funciona, com uma condição: o proxy da frente deve repassar o caminho
**inteiro**. Em nginx, é a diferença de uma barra:

```nginx
location /outro-diretorio/  { proxy_pass http://ubs-host:8081; }   # correto
location /outro-diretorio/  { proxy_pass http://ubs-host:8081/; }  # remove o prefixo
```

Com o caminho inteiro, o `.env` deste projeto é a única fonte de verdade.

Três pontos de atenção nesse arranjo:

- **`X-Forwarded-Proto`.** Se o TLS termina no proxy da frente, o header precisa
  chegar ao backend. O template propaga o valor recebido em vez de sobrescrever
  com `$scheme`, e o `application.properties` tem
  `server.forward-headers-strategy=framework`. Sem isso o Spring enxerga `http`
  e gera redirects com esquema errado.
- **`client_max_body_size`.** Aqui são 20 MB; se o proxy da frente tiver o padrão
  de 1 MB, o upload de ficha é barrado antes de chegar.
- **Diagnóstico em três camadas.** Um 404 pode nascer no proxy externo, aqui ou
  no Spring. Vale ter log nos três.

Se o proxy da frente **remover** o prefixo, o `FRONT_BASE_PATH` do nginx passa a
ser `/` — mas o `<base href>` continua precisando do caminho público, porque ele
reflete o que o **navegador** enxerga. Seriam duas variáveis distintas; é a razão
de preferir o repasse integral.

## Arquivos

- `Dockerfile` — copia o `dist`, guarda o `index.html.template` e instala o
  template do nginx e o script de partida.
- `default.conf.template` — virtual host: redireciona `/` e o prefixo sem barra,
  faz proxy de `${API_BASE_PATH}/` para `backend:8080`, `try_files` para o
  roteamento do Angular, gzip e cache dos assets com hash.
- `30-configura-app.sh` — regera `index.html` e `config.js` na partida.
- `teste-nginx-ubs.ps1` — build, sobe a stack e verifica as URLs.

## Observações

- Imagem base `nginxinc/nginx-unprivileged`: roda como não-root; a porta 8081
  (> 1024) não exige privilégios.
- O endereço do backend vai em variável com `resolver`: com nome literal o nginx
  se recusaria a subir enquanto o backend estivesse fora do ar, derrubando o
  frontend junto.
- O antigo serviço `frontend` (porta 4200) foi removido do `compose.yaml`: este
  container o substitui.
- `nginx.conf` na raiz desta pasta ficou obsoleto e pode ser apagado — o
  `default.conf.template` o substituiu.
