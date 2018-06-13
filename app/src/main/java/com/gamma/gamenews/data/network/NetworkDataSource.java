package com.gamma.gamenews.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.gamma.gamenews.AppExecutors;
import com.gamma.gamenews.data.database.News;
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
                        Log.d(TAG, "onResponse: The response was successful");
                        for(String n : response.body()){
                            SharedPreference.addFavorite(n);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<String>> call, Throwable t) {

                }
            });
        });
    }
}
