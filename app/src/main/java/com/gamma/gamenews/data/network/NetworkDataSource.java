package com.gamma.gamenews.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import com.gamma.gamenews.data.network.deserializer.UserDeserializer;
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

    //Number of days we want API to return
    public static final int NUM_DAYS = 14;
    private static final String TAG = "GN:NetworkDataSorce";

    //Setting intervals to do sync
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    private static final String GAMENEWS_SYNC_TAG = "GameNews-sync";

    private static final Object LOCK = new Object();
    private static NetworkDataSource mInstance;

    private final Context context;
    private final AppExecutors executors;

    private final MutableLiveData<ArrayList<News>> newsArray;

    private NetworkDataSource(Context context, AppExecutors executors) {
        this.context = context;
        this.executors = executors;
        newsArray = new MutableLiveData<>();
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


    /**
     * Do a recurring job service which fetches latest info
     */
    public void schedulePeriodicSync(){
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        //Creating the Job periodically sync the app data
        Job syncAppJob = dispatcher.newJobBuilder()
                .setService(AppJobService.class)
                .setTag(GAMENEWS_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(syncAppJob);
        Log.d(TAG, "schedulePeriodicSync: Job scheduled and ready to sync");
    }

    public LiveData<ArrayList<News>> getCurrentNews(){
        return newsArray;
    }

    /**
     * Starts an intent service to fetch the news.
     */
    public void startFetchNewsService() {
        Intent intentToFetch = new Intent(context, SyncIntentService.class);
        context.startService(intentToFetch);
        Log.d(TAG, "startFetchNewsService: IntentService executed");
    }

    /**
     * Get the latests news
     */
    public void fetchNews() {
        Log.d(TAG, "fetchNews: Starting a News fetch");
        executors.networkIO().execute(() -> {

            Call<ArrayList<News>> call = NetworkUtils.getClientInstanceAuth().getNewsList();
            call.enqueue(new Callback<ArrayList<News>>() {
                @Override
                public void onResponse(Call<ArrayList<News>> call, Response<ArrayList<News>> response) {
                    if(response.isSuccessful()){
                        newsArray.postValue(response.body());
                        Log.d(TAG, "onResponse: News fetching successful!");
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<News>> call, Throwable t) {
                    Log.d(TAG, "onFailure: News fetching failed alv!");
                    t.printStackTrace();
                }
            });

        });
    }

    public void getUserDetails(){
        Log.d(TAG, "getUserDetails: Getting user info");
        if (!NetworkUtils.checkConectivity(context)){
            Toast.makeText(context,
                    context.getResources().getText(R.string.message_no_internet),
                    Toast.LENGTH_LONG).show();
        }
        executors.networkIO().execute(()-> {
            Gson gson = new GsonBuilder().registerTypeAdapter(
                    ArrayList.class,
                    new UserDeserializer()
            ).create();
            Call<ArrayList<String>> call = NetworkUtils.getClientInstanceAuth(gson).getUserDetails();
            call.enqueue(new Callback<ArrayList<String>>() {
                @Override
                public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                    if (response.isSuccessful()){
                        Log.d(TAG, "getUserDetail: onResponse: The response was successful");
                        SharedPreference.removeAllFavs();
                        Log.d(TAG, "onResponse: old favs deleted");
                        for(String n : response.body()){
                            SharedPreference.addFavorite(n);
                        }
                        startFetchNewsService();
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
                public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                    Toast.makeText(context,
                            context.getResources().getText(R.string.message_net_failure),
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "getUserDet: onFailure: the response failed : +"+t.getMessage());
                    t.printStackTrace();
                }
            });
        });
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
                Call<String> response = dataService.addFavorite(
                        SharedPreference.read(SharedPreference.USER_ID,"null"), newid);
                response.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()){
                            Log.d(TAG, "onResponse: Response successful");

                            String[] data = response.body().split(":");
                            if (data[0].equalsIgnoreCase("success")){
                                if(data[1].equalsIgnoreCase("true")){
                                    SharedPreference.addFavorite(newid);
                                    icon.setTag("y");
                                    icon.setImageResource(R.drawable.ic_favorites);
                                    Log.d(TAG, "onResponse: Favorite saved successfully");
                                    Snackbar.make(rootView,
                                            context.getResources().getString(R.string.message_fav_saved),
                                            Snackbar.LENGTH_SHORT).show();
                                } else if(data[1].equalsIgnoreCase("false")){
                                    Log.d(TAG, "onResponse: Favorite was not saved");
                                    Log.d(TAG, "onResponse:"+response.message());
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
                    public void onFailure(Call<String> call, Throwable t) {
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
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()){
                            Log.d(TAG, "onResponse: Response successful");
                            String[] data = response.body().split(":");

                            if (data[0].equalsIgnoreCase("message")){
                                SharedPreference.removeFavorite(newid);
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
                                }
                            }
                        } else {
                            Log.d(TAG, "onResponse: Response failed alv - code :"+response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });

    }
}
