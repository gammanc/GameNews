package com.gamma.gamenews.data.network;

import com.gamma.gamenews.data.database.News;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by emers on 4/6/2018.
 */

public interface DataService {

    @FormUrlEncoded
    @POST("/login")
    Call<String> login(@Field("user") String user, @Field("password")String password);

    @GET("/news")
    Call<ArrayList<News>> getNewsList();
}
