package org.techtown.schooler.network.retrofit.interfaces;

import org.techtown.schooler.Model.User;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.Model.Email;
import org.techtown.schooler.Model.IsOverlapped;
import org.techtown.schooler.network.response.Response;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SignUp {

    // User 사용자 정보를 전달
    @POST("/auth/sign-up")
    Call<Response<Data>> signupPost(@Body User user);

    // Id 중복 체크를 확인
    @POST("/auth/sign-up/is-overlapped")
    Call<Response<Data>> isoverlapped(@Body IsOverlapped eid);


    @POST("/auth/sign-up/email")
    Call<Response<Data>> eamilPost(@Body Email email);

    @GET("school/search-school")
    Call<Response<Data>> SearchSchoolGet(@Query("school_name") String SchoolName);


    @GET("/school/search-class")
    Call<Response<Data>> SearchClassGet(@Query("school_id") String SchoolId,
                                        @Query("office_id") String OfficeId,
                                        @Query("grade") String Grade);
}