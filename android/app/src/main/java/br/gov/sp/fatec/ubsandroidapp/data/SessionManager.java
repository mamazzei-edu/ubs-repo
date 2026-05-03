package br.gov.sp.fatec.ubsandroidapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Set;

/**
 * SessionManager - Gerenciador de sessão atômico
 * Garante que cookie e permissões sejam salvos simultaneamente
 */
public class SessionManager {
    
    private static final String PREF_NAME = "UBS_Session";
    private static final String KEY_COOKIE = "access_cookie";
    private static final String KEY_PERMISSIONS = "user_permissions";
    
    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Atualiza a sessão de forma atômica.
     * Salva o cookie e as permissões simultaneamente.
     * 
     * @param newCookie O novo cookie de acesso
     * @param updatedPermissions As permissões atualizadas do usuário
     */
    public void refreshSession(String newCookie, Set<String> updatedPermissions) {
        SharedPreferences.Editor editor = prefs.edit();
        
        // 1. Renova o Cookie (O acesso ao prédio)
        editor.putString(KEY_COOKIE, newCookie);
        
        // 2. Sincroniza a Chave de Permissão (O chip das portas internas)
        // Isso garante que o sistema não "esqueça" o que o usuário pode fazer
        if (updatedPermissions != null) {
            editor.putStringSet(KEY_PERMISSIONS, updatedPermissions);
        }
        
        editor.apply();
    }

    /**
     * Recupera o cookie de acesso atual.
     * 
     * @return O cookie armazenado ou null se não existir
     */
    public String getCookie() {
        return prefs.getString(KEY_COOKIE, null);
    }

    /**
     * Recupera as permissões do usuário.
     * 
     * @return Set de permissões ou null se não existirem
     */
    public Set<String> getUserPermissions() {
        return prefs.getStringSet(KEY_PERMISSIONS, null);
    }

    /**
     * Limpa todos os dados de sessão.
     */
    public void clearSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_COOKIE);
        editor.remove(KEY_PERMISSIONS);
        editor.apply();
    }
}