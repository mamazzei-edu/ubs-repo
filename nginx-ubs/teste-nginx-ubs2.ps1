# =============================================================================
# Teste complementar: captura a saida dos comandos docker (que se perdeu no
# primeiro script) e exercita o login de verdade atraves do proxy.
#
#     powershell -ExecutionPolicy Bypass -File nginx-ubs\teste-nginx-ubs2.ps1
#
# Grava em nginx-ubs\teste-resultado2.txt.
# =============================================================================

$ErrorActionPreference = 'Continue'
$ProgressPreference    = 'SilentlyContinue'

$repo = Split-Path -Parent $PSScriptRoot
Set-Location $repo
$log = Join-Path $PSScriptRoot 'teste-resultado2.txt'

function Secao($t) { "", ("=" * 70), "== $t", ("=" * 70) }

# Executa comando nativo capturando stdout E stderr (o que faltou antes).
function Run($desc, $cmd) {
    "`n--- $desc ---"
    (cmd /c "$cmd 2>&1" | Out-String).TrimEnd()
}

$saida = @()

$saida += Secao "Estado dos containers"
$saida += Run "docker compose ps" "docker compose ps"

$saida += Secao "Configuracao do nginx dentro do container"
$saida += Run "nginx -t" "docker compose exec -T nginx-ubs nginx -t -c /etc/nginx/nginx.conf"
$saida += Run "conteudo servido" "docker compose exec -T nginx-ubs sh -c ""ls /usr/share/nginx/html; echo ---; ls /usr/share/nginx/html/diretorio"""
$saida += Run "placeholder remanescente (esperado: 0)" "docker compose exec -T nginx-ubs sh -c ""grep -c __APP_BASE_PATH__ /etc/nginx/nginx.conf; true"""

$saida += Secao "Login real atraves do proxy (POST)"
$body = '{"email":"super.admin@email.com","password":"123456"}'
try {
    $r = Invoke-WebRequest -Uri 'http://localhost:8081/diretorio/auth/login' -Method POST `
            -Body $body -ContentType 'application/json' -UseBasicParsing -TimeoutSec 25
    $saida += "Status     : $([int]$r.StatusCode)"
    $saida += "Set-Cookie : $($r.Headers['Set-Cookie'])"
    $saida += "Body       : $($r.Content)"
} catch {
    $resp = $_.Exception.Response
    if ($resp) {
        $saida += "Status     : $([int]$resp.StatusCode)"
        try {
            $sr = New-Object System.IO.StreamReader($resp.GetResponseStream())
            $saida += "Body       : $($sr.ReadToEnd())"
        } catch {}
    } else {
        $saida += "FALHOU: $($_.Exception.Message)"
    }
}

$saida += Secao "Banco: papeis e usuarios semeados"
$saida += Run "roles" "docker compose exec -T mysql_server sh -c ""mysql -uroot -p\$MYSQL_ROOT_PASSWORD -N -e 'select id,name from ubs.roles;'"""
$saida += Run "users" "docker compose exec -T mysql_server sh -c ""mysql -uroot -p\$MYSQL_ROOT_PASSWORD -N -e 'select u.id,u.email,r.name from ubs.users u join ubs.roles r on r.id=u.role_id;'"""

$saida += Secao "Servico 'frontend' antigo (porta 4200) apos o baseHref"
foreach ($u in @('http://localhost:4200/', 'http://localhost:4200/diretorio/')) {
    try {
        $r = Invoke-WebRequest -Uri $u -UseBasicParsing -TimeoutSec 10
        $m = [regex]::Match($r.Content, '<base href="[^"]*">')
        $saida += ("{0,-38} {1}  {2}" -f $u, [int]$r.StatusCode, $m.Value)
    } catch {
        $resp = $_.Exception.Response
        if ($resp) { $saida += ("{0,-38} {1}" -f $u, [int]$resp.StatusCode) }
        else { $saida += ("{0,-38} FALHOU: {1}" -f $u, $_.Exception.Message) }
    }
}

$saida += Secao "Logs do backend"
$saida += Run "backend" "docker compose logs --tail=80 backend"
$saida += Secao "Logs do nginx-ubs"
$saida += Run "nginx-ubs" "docker compose logs --tail=30 nginx-ubs"

$saida | Out-File -FilePath $log -Encoding utf8
Write-Host "Resultado gravado em $log"
