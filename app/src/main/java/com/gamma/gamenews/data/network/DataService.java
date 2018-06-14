package com.gamma.gamenews.data.network;

import com.gamma.gamenews.data.database.News;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

    @POST("/users/{userid}/fav")
    @FormUrlEncoded
    Call<String> addFavorite(@Path("userid") String userId, @Field("new")String newid);

    @HTTP(method = "DELETE", path = "/users/{userid}/fav", hasBody = true)
    @FormUrlEncoded
    Call<String> deleteFavorite(@Path("userid") String userId, @Field("new") String newid);
}
