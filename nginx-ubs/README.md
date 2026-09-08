# nginx-ubs

Container Nginx que serve o app Angular (pasta `frontend`) **já compilado**, na porta **8081**.

## Diferença para `frontend/Dockerfile`

| | `frontend/Dockerfile` | `nginx-ubs/Dockerfile` |
|---|---|---|
| Build do Angular | dentro da imagem (multi-stage com Node) | fora, na máquina/pipeline |
| Contexto de build | `./frontend` | `.` (raiz do repositório) |
| Porta | 8080 (publicada em 4200) | 8081 (publicada em 8081) |

## Como usar

1. Gerar o build do Angular (a saída fica em `frontend/dist/frontend/browser`):

```bash
cd frontend
npm ci
npm run build
cd ..
```

2. Subir o container:

```bash
docker compose up -d --build nginx-ubs
```

3. Acessar: <http://localhost:8081>

> Sempre que o código do Angular mudar, refaça o `npm run build` e rode
> `docker compose build nginx-ubs` novamente — a imagem não recompila o app.

## Arquivos

- `Dockerfile` — copia `frontend/dist/*/browser/` para `/usr/share/nginx/html`.
- `nginx.conf` — `listen 8081`, `try_files` para o roteamento do Angular, gzip,
  cache de assets e um bloco `location /api/` (comentado) para proxy do backend.

## Observações

- A imagem base é `nginxinc/nginx-unprivileged`, que roda como usuário não-root;
  a porta 8081 (> 1024) não exige privilégios.
- O `.dockerignore` na raiz do repositório mantém o contexto de build enxuto,
  mas **não** exclui `frontend/dist`, que é justamente o que precisa ser copiado.
