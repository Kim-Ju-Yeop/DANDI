package org.techtown.schooler.network.retrofit.interfaces;

import org.techtown.schooler.Model.LoginPostRequest;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.response.Response;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Login {
    @POST("auth/login")
    Call<Response<Data>> loginPost(@Body LoginPostRequest loginPostRequest);
}