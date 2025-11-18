package br.sp.gov.fatec.ubs.android.data.model

import com.google.gson.annotations.SerializedName

data class Paciente(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("nomeCompleto")
    val nomeCompleto: String,
    
    @SerializedName("nomeSocial")
    val nomeSocial: String? = null,
    
    @SerializedName("nomeMae")
    val nomeMae: String? = null,
    
    @SerializedName("nomePai")
    val nomePai: String? = null,
    
    @SerializedName("dataNascimento")
    val dataNascimento: String,
    
    @SerializedName("sexo")
    val sexo: String? = null,
    
    @SerializedName("raca")
    val raca: String? = null,
    
    @SerializedName("cpf")
    val cpf: String? = null,
    
    @SerializedName("cns")
    val cns: String? = null,
    
    @SerializedName("prontuario")
    val prontuario: String,
    
    @SerializedName("telefone")
    val telefone: String? = null,
    
    @SerializedName("email")
    val email: String? = null,
    
    @SerializedName("endereco")
    val endereco: String? = null,
    
    @SerializedName("numero")
    val numero: String? = null,
    
    @SerializedName("complemento")
    val complemento: String? = null,
    
    @SerializedName("bairro")
    val bairro: String? = null,
    
    @SerializedName("cidade")
    val cidade: String? = null,
    
    @SerializedName("estado")
    val estado: String? = null,
    
    @SerializedName("cep")
    val cep: String? = null,
    
    @SerializedName("observacoes")
    val observacoes: String? = null
)
