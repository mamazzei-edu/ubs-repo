package br.sp.gov.fatec.ubs.android.ui.paciente

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.sp.gov.fatec.ubs.android.data.api.RetrofitClient
import br.sp.gov.fatec.ubs.android.data.model.Paciente
import br.sp.gov.fatec.ubs.android.data.repository.AuthRepository
import br.sp.gov.fatec.ubs.android.data.repository.PacienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PacienteViewModel(
    private val pacienteRepository: PacienteRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _pacientes = MutableStateFlow<List<Paciente>>(emptyList())
    val pacientes: StateFlow<List<Paciente>> = _pacientes
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    init {
        // Carregar token salvo
        viewModelScope.launch {
            authRepository.loadToken()
        }
    }
    
    fun loadPacientes(page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = pacienteRepository.getPacientes(page, size)
            
            result.onSuccess { response ->
                _pacientes.value = response.content
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Erro ao carregar pacientes"
                _isLoading.value = false
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}

class PacienteViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PacienteViewModel::class.java)) {
            val pacienteRepository = PacienteRepository(RetrofitClient.apiService)
            val authRepository = AuthRepository(context, RetrofitClient.apiService)
            @Suppress("UNCHECKED_CAST")
            return PacienteViewModel(pacienteRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
