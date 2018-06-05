package com.gamma.gamenews.Utils;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by emers on 4/6/2018.
 */

public class Client {
    private static Retrofit retrofit;
    private static String BASE_URL = "http://gamenewsuca.herokuapp.com";

    public static Retrofit getClientInstance(Gson gson){
        if(retrofit == null){
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
