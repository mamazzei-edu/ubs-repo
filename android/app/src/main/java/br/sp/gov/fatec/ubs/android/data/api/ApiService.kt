package br.sp.gov.fatec.ubs.android.data.api

import br.sp.gov.fatec.ubs.android.data.model.LoginRequest
import br.sp.gov.fatec.ubs.android.data.model.LoginResponse
import br.sp.gov.fatec.ubs.android.data.model.Paciente
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // ========== AUTENTICAÇÃO ==========
    // Backend expõe /auth/login e /auth/logout
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    
    @POST("auth/logout")
    suspend fun logout(): Response<Void>
    
    // ========== PACIENTES ==========
    @GET("api/pacientes")
    suspend fun getPacientes(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<PacientePageResponse>
    
    @GET("api/pacientes/{id}")
    suspend fun getPacienteById(@Path("id") id: Long): Response<Paciente>
    
    @GET("api/pacientes/prontuario/{prontuario}")
    suspend fun getPacienteByProntuario(@Path("prontuario") prontuario: String): Response<Paciente>
    
    @GET("api/pacientes/nome/{nome}")
    suspend fun getPacienteByNome(@Path("nome") nome: String): Response<Paciente>
    
    @POST("api/pacientes")
    suspend fun createPaciente(@Body paciente: Paciente): Response<Paciente>
    
    @PUT("api/pacientes/{id}")
    suspend fun updatePaciente(
        @Path("id") id: Long,
        @Body paciente: Paciente
    ): Response<Paciente>
    
    @DELETE("api/pacientes/{id}")
    suspend fun deletePaciente(@Path("id") id: Long): Response<Void>
}

data class PacientePageResponse(
    val content: List<Paciente>,
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val number: Int
)
