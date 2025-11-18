package br.sp.gov.fatec.ubs.android.data.api

import br.sp.gov.fatec.ubs.android.BuildConfig
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    private var token: String? = null
    private val cookieStore = mutableMapOf<String, List<Cookie>>()
    
    fun setToken(newToken: String?) {
        token = newToken
    }
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // CookieJar para gerenciar cookies (autenticação do backend)
    private val cookieJar = object : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            cookieStore[url.host] = cookies
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host] ?: emptyList()
        }
    }
    
    private val authInterceptor = okhttp3.Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        
        // Adicionar token se existir (para compatibilidade)
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        
        // Adicionar header para aceitar cookies
        requestBuilder.addHeader("Accept", "application/json")
        
        chain.proceed(requestBuilder.build())
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar) // Adicionar suporte a cookies
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
