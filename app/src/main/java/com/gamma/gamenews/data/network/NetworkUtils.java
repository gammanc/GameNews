package com.gamma.gamenews.data.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.gamma.gamenews.utils.SharedPreference;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Performs the actual connection with the API servers
 * */
public class NetworkUtils {
    private static Retrofit retrofit;
    private static String BASE_URL = "http://gamenewsuca.herokuapp.com";
    private static DataService dataService;

    public static DataService getClientInstance(Gson gson){
        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        dataService = retrofit.create(DataService.class);
        return dataService;
    }
    public static DataService getClientInstance(){
        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        dataService = retrofit.create(DataService.class);
        return dataService;
    }
    public static DataService getClientInstanceAuth(){
        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getHeader())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        dataService = retrofit.create(DataService.class);
        return dataService;
    }
    public static DataService getClientInstanceAuth(Gson gson){
        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getHeader())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        dataService = retrofit.create(DataService.class);
        return dataService;
    }

    private static OkHttpClient getHeader() {
        final String token = SharedPreference.read(SharedPreference.TOKEN,null);
        return new OkHttpClient.Builder().addInterceptor(chain -> {
            Request newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(newRequest);
        }).build();
    }

    public static boolean checkConectivity(Context context){
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
