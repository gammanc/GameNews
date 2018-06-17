package com.gamma.gamenews.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.gamma.gamenews.AppExecutors;
import com.gamma.gamenews.R;
import com.gamma.gamenews.data.database.News;
import com.gamma.gamenews.data.network.deserializer.MessageDeserializer;
import com.gamma.gamenews.data.network.deserializer.User;
import com.gamma.gamenews.data.network.deserializer.UserDeserializer;
import com.gamma.gamenews.utils.DependencyContainer;
import com.gamma.gamenews.utils.SharedPreference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Manages operations to perform with the API
 * Provides the most recent downloaded data
 * */
public class NetworkDataSource {

    private static final String TAG = "GN:NetworkDataSorce";

    private static final Object LOCK = new Object();
    private static NetworkDataSource mInstance;

    private final Context context;
    private final AppExecutors executors;

    private final MutableLiveData<ArrayList<News>> newsArray;
    private final MutableLiveData<String[]> favorites;

    private final MutableLiveData<String[]> games;

    private NetworkDataSource(Context context, AppExecutors executors) {
        this.context = context;
        this.executors = executors;
        newsArray = new MutableLiveData<>();
        favorites = new MutableLiveData<>();
        games = new MutableLiveData<>();
    }

    /**
     * Get the class singleton
     */
    public static NetworkDataSource getInstance(Context context, AppExecutors executors){
        Log.d(TAG, "Providing NetworkDataSource");
        if(mInstance == null){
            synchronized (LOCK){
                mInstance = new NetworkDataSource(context.getApplicationContext(), executors);
            }
        }
        return mInstance;
    }

    public LiveData<ArrayList<News>> getCurrentNews(){
        return newsArray;
    }

    public LiveData<String[]> getCurrentFavs(){
        return favorites;
    }

    public LiveData<String[]> getGames(){ return games;}

    /**
     * Starts an intent service to fetch the news.
     */
    private void startFetchNewsService(String[] favs) {
        Intent intentToFetch = new Intent(context, SyncIntentService.class);
        intentToFetch.putExtra("favorites",favs);
        context.startService(intentToFetch);
        Log.d(TAG, "startFetchNewsService: IntentService executed");
    }

    /**
     * Get the latests news
     */
    public void fetchNews(String[] favs) {
        Log.d(TAG, "fetchNews: Starting a News fetch");
        executors.networkIO().execute(() -> {

            Call<ArrayList<News>> call = NetworkUtils.getClientInstanceAuth().getNewsList();
            call.enqueue(new Callback<ArrayList<News>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<News>> call,
                                       @NonNull Response<ArrayList<News>> response) {
                    if(response.isSuccessful()){
                        newsArray.postValue(response.body());
                        favorites.postValue(favs);
                        Log.d(TAG, "onResponse: News fetching successful!");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<News>> call,
                                      @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: News fetching failed alv!");
                    t.printStackTrace();
                }
            });

        });
    }

    public void fetchUserDetails(){
        Log.d(TAG, "fetchUserDetails: fetching user info");
        if(checkConnection()) return;
        executors.networkIO().execute(()-> {
            Call<User> call = NetworkUtils.getClientInstanceAuth().getUserDetails();

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call,
                                       @NonNull Response<User> response) {
                    if (response.isSuccessful()){
                        Log.d(TAG, "getUserDetail: onResponse: The response was successful");
                        Log.d(TAG, "onResponse: old favs deleted");
                        SharedPreference.write(SharedPreference.USER_ID, response.body().getId());
                        startFetchNewsService(response.body().getFavoriteNews());
                    } else {
                        switch (response.code()){
                            case 401:
                                Toast.makeText(context,
                                        context.getResources().getText(R.string.message_session_expired),
                                        Toast.LENGTH_LONG).show();
                                SharedPreference.logOutUser();
                        }
                        Log.d(TAG, "getUserDetail: onResponse: The response failed. code "+response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call,@NonNull  Throwable t) {
                    showError(t);
                }
            });
        });
    }

    //TODO: FETCH GAMES LIST
    public void fetchGames(){
        Log.d(TAG, "fetchUserDetails: fetching games...");
        if (checkConnection()) return;
        executors.networkIO().execute(()->{
            Call<String[]> call = NetworkUtils.getClientInstanceAuth().getGames();
            call.enqueue(new Callback<String[]>() {
                @Override
                public void onResponse(@NonNull Call<String[]> call,
                                       @NonNull Response<String[]> response) {
                    if (response.isSuccessful()){
                        games.postValue(response.body());
                        Log.d(TAG, "onResponse: Games fetching successful!");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String[]> call,
                                      @NonNull Throwable t) {
                    showError(t);
                }
            });
        });
    }

    private boolean checkConnection(){
        if (!NetworkUtils.checkConectivity(context)){
            Toast.makeText(context,
                    context.getResources().getText(R.string.message_no_internet),
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    public void setFavorite(ImageView v, String newid, View rootView){

        Log.d(TAG, "onNewsChecked: FAV CLICKED");
        ImageView icon = v.findViewById(R.id.btn_favorite);

        Gson gson = new GsonBuilder().registerTypeAdapter(
                String.class,
                new MessageDeserializer()
        ).create();

        executors.networkIO().execute(()->{

            DataService dataService = NetworkUtils.getClientInstanceAuth(gson);

            if(icon.getTag().toString().equalsIgnoreCase("n")){
                Log.d(TAG, "setFavorite: SHAREDPREF - userid:"+
                        SharedPreference.read(SharedPreference.USER_ID,"null"));
                Call<String> response = dataService.addFavorite(
                        SharedPreference.read(SharedPreference.USER_ID,"null"), newid);
                response.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call,
                                           @NonNull Response<String> response) {
                        if (response.isSuccessful()){
                            Log.d(TAG, "onResponse: Response successful");

                            String[] data = response.body().split(":");
                            if (data[0].equalsIgnoreCase("success")){
                                if(data[1].equalsIgnoreCase("true")){
                                    DependencyContainer.getRepository(context).updateFavorite(newid,true);
                                    icon.setTag("y");
                                    icon.setImageResource(R.drawable.ic_favorites);
                                    Log.d(TAG, "onResponse: Favorite saved successfully");
                                    Snackbar.make(rootView,
                                            context.getResources().getString(R.string.message_fav_saved),
                                            Snackbar.LENGTH_SHORT).show();
                                } else if(data[1].equalsIgnoreCase("false")){
                                    Log.d(TAG, "onResponse: Favorite was not saved");
                                    Log.d(TAG, "onResponse:"+response.message());
                                    Log.d(TAG, "onResponse:"+response.code());
                                }
                            }
                        } else {
                            Snackbar.make(rootView,
                                    context.getResources().getString(R.string.message_net_failure),
                                    Snackbar.LENGTH_SHORT).show();
                            Log.d(TAG, "onResponse: Response failed alv - code :"+response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call,@NonNull  Throwable t) {
                        Snackbar.make(rootView,
                                context.getResources().getString(R.string.message_net_failure),
                                Snackbar.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            } else {
                Call<String> response = dataService.deleteFavorite(
                        SharedPreference.read(SharedPreference.USER_ID,"null"), newid);
                response.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call,
                                           @NonNull Response<String> response) {
                        if (response.isSuccessful()){
                            Log.d(TAG, "onResponse: Response successful");
                            String[] data = response.body().split(":");

                            if (data[0].equalsIgnoreCase("message")){

                                DependencyContainer.getRepository(context).updateFavorite(newid,false);
                                icon.setTag("n");
                                icon.setImageResource(R.drawable.ic_favorite_border);
                                Snackbar.make(rootView,
                                        context.getResources().getString(R.string.message_fav_deleted),
                                        Snackbar.LENGTH_SHORT).show();

                                Log.d(TAG, "onResponse: Favorite deleted successfully");

                            } else if(data[0].equals("success")) {
                                if (data[1].equals("false")){
                                    Snackbar.make(rootView,
                                            context.getResources().getString(R.string.message_fav_deleted),
                                            Snackbar.LENGTH_SHORT).show();

                                    Log.d(TAG, "onResponse: Favorite was not deleted");
                                    Log.d(TAG, "onResponse:"+response.message());

                                    Log.d(TAG, "onResponse:"+response.code());
                                }
                            }
                        } else {
                            Log.d(TAG, "onResponse: Response failed alv - code :"+response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call,@NonNull  Throwable t) {
                        Toast.makeText(context,
                                context.getResources().getText(R.string.message_net_failure),
                                Toast.LENGTH_LONG).show();
                        Log.d(TAG, "getUserDet: onFailure: the response failed : +"+t.getMessage());
                        t.printStackTrace();
                    }
                });
            }
        });

    }

    public void showError(Throwable t){
        Toast.makeText(context,
                context.getResources().getText(R.string.message_net_failure),
                Toast.LENGTH_LONG).show();
        Log.d(TAG, "getUserDet: onFailure: the response failed : +"+t.getMessage());
        t.printStackTrace();
    }
}
