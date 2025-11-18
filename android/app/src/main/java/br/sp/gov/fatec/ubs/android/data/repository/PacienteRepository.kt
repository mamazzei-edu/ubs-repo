package br.sp.gov.fatec.ubs.android.data.repository

import br.sp.gov.fatec.ubs.android.data.api.ApiService
import br.sp.gov.fatec.ubs.android.data.api.PacientePageResponse
import br.sp.gov.fatec.ubs.android.data.model.Paciente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PacienteRepository(private val apiService: ApiService) {
    
    suspend fun getPacientes(page: Int = 0, size: Int = 10): Result<PacientePageResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPacientes(page, size)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Erro ao buscar pacientes: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getPacienteById(id: Long): Result<Paciente> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPacienteById(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Paciente não encontrado"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun createPaciente(paciente: Paciente): Result<Paciente> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createPaciente(paciente)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Erro ao criar paciente: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun updatePaciente(id: Long, paciente: Paciente): Result<Paciente> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updatePaciente(id, paciente)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Erro ao atualizar paciente: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun deletePaciente(id: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deletePaciente(id)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Erro ao deletar paciente: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
