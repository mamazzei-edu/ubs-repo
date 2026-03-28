package br.gov.sp.fatec.ubsandroidapp.api;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Response;
import br.gov.sp.fatec.ubsandroidapp.data.SessionManager;

/**
 * AuthInterceptor - Interceptor para gerenciar autenticação e sessão
 * Captura o cookie da resposta e sincroniza permissões
 */
public class AuthInterceptor implements Interceptor {
    
    private SessionManager sessionManager;

    public AuthInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        okhttp3.Request originalRequest = chain.request();
        
        okhttp3.Request.Builder requestBuilder = originalRequest.newBuilder()
                .header("Accept", "application/json");
        
        // Adicionar o cookie atual se existir
        String currentCookie = sessionManager.getCookie();
        if (currentCookie != null && !currentCookie.isEmpty()) {
            requestBuilder.header("Cookie", currentCookie);
        }
        
        okhttp3.Request request = requestBuilder.build();
        Response response = chain.proceed(request);
        
        // Verifica se a resposta foi bem-sucedida e se há um novo cookie
        if (response.isSuccessful()) {
            String newCookie = response.header("Set-Cookie");
            if (newCookie != null && !newCookie.isEmpty()) {
                // Extrai as permissões da resposta
                Set<String> newPermissions = parsePermissionsFromResponse(response);
                
                // Atualiza a sessão de forma atômica
                sessionManager.refreshSession(newCookie, newPermissions);
            }
        }
        
        return response;
    }

    /**
     * Extrai as permissões da resposta do servidor.
     * Este é um método auxiliar que pode ser expandido para parsing real.
     * 
     * @param response A resposta do servidor
     * @return Set de permissões extraídas
     */
    private Set<String> parsePermissionsFromResponse(Response response) {
        // Por enquanto, retorna um Set vazio ou pode ser expandido
        // para extrair permissões de um header ou corpo da resposta
        String permissionsHeader = response.header("X-User-Permissions");
        
        Set<String> permissions = new HashSet<>();
        
        if (permissionsHeader != null && !permissionsHeader.isEmpty()) {
            // Exemplo de parsing: "ROLE_ADMIN,ROLE_USER"
            String[] perms = permissionsHeader.split(",");
            for (String perm : perms) {
                String trimmed = perm.trim();
                if (!trimmed.isEmpty()) {
                    permissions.add(trimmed);
                }
            }
        }
        
        return permissions;
    }
}