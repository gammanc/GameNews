package com.gamma.gamenews.data.network;

import com.gamma.gamenews.data.database.News;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Defines the operations that NetworkUtils can perform
 */

public interface DataService {

    @FormUrlEncoded
    @POST("/login")
    Call<String> login(@Field("user") String user, @Field("password")String password);

    @GET("/news")
    Call<ArrayList<News>> getNewsList();

    @GET("/users/detail")
    Call<ArrayList<String>> getUserDetails();


}
