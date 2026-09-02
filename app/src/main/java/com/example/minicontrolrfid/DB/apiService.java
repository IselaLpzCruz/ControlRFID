package com.example.minicontrolrfid.DB;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface apiService {
    @POST("Auth")
    Call<RespuestaToken> loginAuth(@Body Credenciales credenciales);

    @POST("api/Procedimiento")
    Call<Respuesta> llamarProcedimiento(@Header("Authorization") String token, @Header("Content-Type") String contenttype, @Body Peticion peticion);
}
