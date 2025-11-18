package br.sp.gov.fatec.ubs.android.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.sp.gov.fatec.ubs.android.data.api.RetrofitClient
import br.sp.gov.fatec.ubs.android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState
    
    fun login(email: String, senha: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            val result = authRepository.login(email, senha)
            
            result.onSuccess {
                _loginState.value = LoginState.Success
            }.onFailure { exception ->
                _loginState.value = LoginState.Error(
                    exception.message ?: "Erro ao fazer login"
                )
            }
        }
    }
}

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val authRepository = AuthRepository(context, RetrofitClient.apiService)
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
