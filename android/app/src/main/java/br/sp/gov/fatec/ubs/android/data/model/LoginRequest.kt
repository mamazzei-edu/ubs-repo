package br.sp.gov.fatec.ubs.android.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")  // Backend espera "password", não "senha"
    val senha: String
)

data class LoginResponse(
    @SerializedName("token")
    val token: String?,
    
    @SerializedName("email")
    val email: String?,
    
    @SerializedName("roles")
    val roles: List<String>?
)
