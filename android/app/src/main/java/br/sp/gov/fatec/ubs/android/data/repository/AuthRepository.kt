package br.sp.gov.fatec.ubs.android.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.sp.gov.fatec.ubs.android.data.api.ApiService
import br.sp.gov.fatec.ubs.android.data.api.RetrofitClient
import br.sp.gov.fatec.ubs.android.data.model.LoginRequest
import br.sp.gov.fatec.ubs.android.data.model.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthRepository(private val context: Context, private val apiService: ApiService) {
    
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val EMAIL_KEY = stringPreferencesKey("user_email")
        private val ROLES_KEY = stringPreferencesKey("user_roles")
    }
    
    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }
    
    val userEmail: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[EMAIL_KEY]
    }
    
    suspend fun login(email: String, senha: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(email, senha))
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    
                    // Salvar token e informações do usuário
                    saveAuthData(
                        token = loginResponse.token,
                        email = loginResponse.email,
                        roles = loginResponse.roles
                    )
                    
                    // Configurar token no RetrofitClient
                    RetrofitClient.setToken(loginResponse.token)
                    
                    Result.success(loginResponse)
                } else {
                    Result.failure(Exception("Email ou senha inválidos"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                apiService.logout()
                clearAuthData()
                RetrofitClient.setToken(null)
                Result.success(Unit)
            } catch (e: Exception) {
                // Mesmo com erro, limpar dados locais
                clearAuthData()
                RetrofitClient.setToken(null)
                Result.success(Unit)
            }
        }
    }
    
    suspend fun isLoggedIn(): Boolean {
        return authToken.first() != null
    }
    
    suspend fun loadToken() {
        val token = authToken.first()
        RetrofitClient.setToken(token)
    }
    
    private suspend fun saveAuthData(token: String?, email: String?, roles: List<String>?) {
        context.dataStore.edit { preferences ->
            token?.let { preferences[TOKEN_KEY] = it }
            email?.let { preferences[EMAIL_KEY] = it }
            roles?.let { preferences[ROLES_KEY] = it.joinToString(",") }
        }
    }
    
    private suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(EMAIL_KEY)
            preferences.remove(ROLES_KEY)
        }
    }
}
