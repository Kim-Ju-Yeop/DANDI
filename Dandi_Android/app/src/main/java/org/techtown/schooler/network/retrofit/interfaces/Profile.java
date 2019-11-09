package org.techtown.schooler.network.retrofit.interfaces;

import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.response.Response;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Profile {

    @GET("/profile")
    Call<Response<Data>> GetProfile(@Header("x-access-token") String token,
                                    @Query("user_id") String user_id);
    @Multipart
    @POST("/image/upload/profile")
    Call<Response<Data>> uploadProfile(@Header("x-access-token") String token,
                                       @Part MultipartBody.Part profile_pic,
                                       @Part("profile_pic") RequestBody name);
}
