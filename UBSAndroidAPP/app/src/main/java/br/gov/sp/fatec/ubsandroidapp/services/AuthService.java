package br.gov.sp.fatec.ubsandroidapp.services;

import br.gov.sp.fatec.ubsandroidapp.dtos.LoginRequest;
import br.gov.sp.fatec.ubsandroidapp.dtos.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}