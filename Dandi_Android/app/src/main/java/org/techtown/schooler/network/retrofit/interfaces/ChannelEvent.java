package org.techtown.schooler.network.retrofit.interfaces;

import org.techtown.schooler.Model.AddChannelEvents;
import org.techtown.schooler.Model.UpdateChannelEvents;
import org.techtown.schooler.network.Data;
import org.techtown.schooler.network.response.Response;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ChannelEvent {

    @GET("/channel-event")
    Call<org.techtown.schooler.network.response.Response<Data>> GetChannelEvent(@Header("x-access-token")String token,
                                                                                @Query("channel_id")String channel_id);

    @POST("/channel-event/add")
    Call<org.techtown.schooler.network.response.Response<Data>> AddChannelEvent(@Header("x-access-token")String token,
                                                                                @Query("channel_id")String channel_id,
                                                                                @Body AddChannelEvents addChannelEvents);

    @DELETE("/channel-event/delete")
    Call<org.techtown.schooler.network.response.Response<Data>> DeleteChannelEvent(@Header("x-access-token")String token,
                                                                                   @Query("event_id")String event_id);

    @PUT("/channel-event/update")
    Call<Response<Data>> UpdateChannelEvent(@Header("x-access-token")String token,
                                            @Query("event_id") String event_id,
                                            @Body UpdateChannelEvents updateChannelEvents);

    @GET("/channel-event/search")
    Call<org.techtown.schooler.network.response.Response<Data>> SearchChannelEvent(@Header("x-access-token") String Token,
                                                                                   @Query("channel_id") String channel_id,
                                                                                   @Query("keyword") String keyword);
}
