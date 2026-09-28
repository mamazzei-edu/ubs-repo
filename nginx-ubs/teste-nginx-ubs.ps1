# =============================================================================
# Teste do nginx-ubs com caminhos configurados em runtime pelo .env.
#
#     powershell -ExecutionPolicy Bypass -File nginx-ubs\teste-nginx-ubs.ps1
#
# Grava em nginx-ubs\teste-resultado.txt.
# =============================================================================

$VERSAO = '2026-09-28b'   # aparece no relatorio; confirma qual versao rodou

$ErrorActionPreference = 'Continue'
$ProgressPreference    = 'SilentlyContinue'

$repo = Split-Path -Parent $PSScriptRoot
Set-Location $repo
$log = Join-Path $PSScriptRoot 'teste-resultado.txt'

function Secao($t) { "", ("=" * 70), "== $t", ("=" * 70) }

# Executa um bloco PowerShell capturando stdout E stderr.
# Recebe scriptblock, e nao string: passar o comando como texto para "cmd /c"
# quebra sempre que o proprio comando tem aspas dentro.
function Run([string]$desc, [scriptblock]$bloco) {
    "`n--- $desc ---"
    (& $bloco 2>&1 | Out-String).TrimEnd()
}

# Usa HttpWebRequest em vez de Invoke-WebRequest: no PowerShell 5.1,
# "Invoke-WebRequest -MaximumRedirection 0" trata um 301/302 como ERRO e
# despeja um bloco vermelho no console a cada redirecionamento testado.
# Aqui, com AllowAutoRedirect = $false, um 3xx e uma resposta normal.
#
# Quando a chamada leva corpo (POST), o Set-Cookie e o corpo da resposta
# sempre entram no relatorio.
function Http([string]$url, [string]$metodo = 'GET', [string]$corpo = $null) {

    $req = [System.Net.WebRequest]::Create($url)
    $req.Method            = $metodo
    $req.AllowAutoRedirect = $false
    $req.Timeout           = 25000

    if ($corpo) {
        $req.ContentType = 'application/json'
        $bytes = [System.Text.Encoding]::UTF8.GetBytes($corpo)
        $req.ContentLength = $bytes.Length
        $fluxo = $req.GetRequestStream()
        $fluxo.Write($bytes, 0, $bytes.Length)
        $fluxo.Close()
    }

    $resp = $null
    try {
        $resp = $req.GetResponse()
    } catch [System.Net.WebException] {
        $resp = $_.Exception.Response          # 4xx e 5xx chegam por aqui
        if (-not $resp) {
            return ("{0,-52} FALHOU: {1}" -f $url, $_.Exception.Message)
        }
    }

    $codigo = [int]$resp.StatusCode
    $local  = $resp.Headers['Location']
    $tipo   = $resp.Headers['Content-Type']
    if ($local) { $extra = "-> $local" } else { $extra = $tipo }

    $linhas = @("{0,-52} {1} {2}" -f $url, $codigo, $extra)

    if ($corpo) {
        $cookie = $resp.Headers['Set-Cookie']
        if ($cookie) { $linhas += "    Set-Cookie: $cookie" }
        try {
            $leitor = New-Object System.IO.StreamReader($resp.GetResponseStream())
            $linhas += "    Body: $($leitor.ReadToEnd())"
            $leitor.Close()
        } catch {
            $linhas += "    Body: (nao foi possivel ler: $($_.Exception.Message))"
        }
    }

    $resp.Close()
    return $linhas
}

# Os caminhos vem do .env, que e a fonte de verdade.
$cfg = @{}
Get-Content .env | Where-Object { $_ -match '^\s*[A-Z_]+\s*=' } | ForEach-Object {
    $k, $v = $_ -split '=', 2
    $cfg[$k.Trim()] = $v.Trim()
}
$FRONT = $cfg['FRONT_BASE_PATH']
$API   = $cfg['API_BASE_PATH']
$base  = 'http://localhost:8081'

if (-not $FRONT -or -not $API) {
    Write-Host "ERRO: defina FRONT_BASE_PATH e API_BASE_PATH no .env antes de rodar." -ForegroundColor Red
    exit 1
}

$saida = @()
$saida += Secao "Ambiente"
$saida += "Versao do script : $VERSAO"
$saida += "Data             : $(Get-Date -Format s)"
$saida += "FRONT_BASE_PATH  : $FRONT"
$saida += "API_BASE_PATH    : $API"
$saida += Run "docker version" { docker version --format "Server {{.Server.Version}}" }

$saida += Secao "1. Build do Angular"
Push-Location frontend
if (-not (Test-Path node_modules)) { npm ci --no-audit --no-fund | Out-Null }
$saida += Run "npm run build" { npm run build }
Pop-Location
$saida += "`n--- base href gerado pelo build (a imagem o troca por marcador) ---"
$saida += (Select-String -Path frontend\dist\*\browser\index.html -Pattern '<base href="[^"]*">' |
            ForEach-Object { $_.Matches.Value })

$saida += Secao "2. Build da imagem e subida so do nginx (backend fora)"
$saida += Run "build" { docker compose build nginx-ubs }
$saida += Run "up nginx-ubs" { docker compose up -d --no-deps --force-recreate --remove-orphans nginx-ubs }
Start-Sleep -Seconds 5
$saida += Run "ps" { docker compose ps nginx-ubs }

$saida += Secao "3. Configuracao gerada dentro do container"
$saida += Run "nginx -t" { docker compose exec -T nginx-ubs nginx -t }
$saida += Run "locations do default.conf" {
    docker compose exec -T nginx-ubs grep -nE 'location|proxy_pass|return' /etc/nginx/conf.d/default.conf
}
# grep -o com padrao simples; se nao casar, mostra o inicio do arquivo para
# dar para enxergar o que o container esta realmente servindo.
$saida += Run "base href do index.html servido" {
    docker compose exec -T nginx-ubs sh -c 'grep -o "<base[^>]*>" /usr/share/nginx/html/index.html || head -c 400 /usr/share/nginx/html/index.html'
}
$saida += Run "index.html.template (deve ter o marcador)" {
    docker compose exec -T nginx-ubs sh -c 'grep -o "<base[^>]*>" /usr/share/nginx/html/index.html.template'
}
$saida += Run "config.js gerado" { docker compose exec -T nginx-ubs cat /usr/share/nginx/html/config.js }

$saida += Secao "4. Requisicoes com o backend fora do ar"
$saida += Http "$base/"
$saida += Http "$base$FRONT"
$saida += Http "$base$FRONT/"
$saida += Http "$base$FRONT/lista"
$saida += Http "$base$FRONT/config.js"
$saida += Http "$base$FRONT/img1.png"
$saida += Http "$base$API/api/pacientes"
$saida += "(esperado: 302, 301, 200, 200, 200, 200, 502 se o backend estiver fora)"

$saida += Secao "5. Stack completa"
$saida += Run "up" { docker compose up -d --remove-orphans }
Start-Sleep -Seconds 30
$saida += Run "ps" { docker compose ps }
$saida += Http "$base$API/api/pacientes"
$saida += Http "$base$API/auth/login"
$saida += "(esperado: 401 e 405)"
$saida += "`n--- login do super usuario ---"
$saida += Http "$base$API/auth/login" 'POST' '{"email":"super.admin@email.com","password":"123456"}'
$saida += "`n--- login com senha errada (esperado 401) ---"
$saida += Http "$base$API/auth/login" 'POST' '{"email":"super.admin@email.com","password":"errada"}'

$saida += Secao "6. Troca de caminho SEM rebuild"
$saida += "Recria o MESMO container com FRONT_BASE_PATH=/teste-runtime"
$env:FRONT_BASE_PATH = '/teste-runtime'
$saida += Run "up com override" { docker compose up -d --force-recreate nginx-ubs }
Remove-Item Env:FRONT_BASE_PATH
Start-Sleep -Seconds 6
$saida += Http "$base/teste-runtime/"
$saida += Http "$base$FRONT/"
$saida += "(esperado: 200 no caminho novo e 404 no antigo)"
$saida += Run "volta ao valor do .env" { docker compose up -d --force-recreate nginx-ubs }
Start-Sleep -Seconds 6
$saida += Http "$base$FRONT/"
$saida += "(esperado: 200 de novo)"

$saida += Secao "7. Logs"
$saida += Run "nginx-ubs" { docker compose logs --tail=30 nginx-ubs }
$saida += Run "backend" { docker compose logs --tail=60 backend }

$saida | Out-File -FilePath $log -Encoding utf8
Write-Host "Resultado gravado em $log  (script $VERSAO)"
