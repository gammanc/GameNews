package com.gamma.gamenews.Data.Network;

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
import com.gamma.gamenews.Data.Database.News;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by emers on 7/6/2018.
 */

/**
 * Provide the operations with the API server
 * */
public class NetworkDataSource {

    //Number of days we want API to return
    public static final int NUM_DAYS = 14;
    private static final String TAG = NetworkDataSource.class.getSimpleName();

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
        Log.d(TAG, "Getting NetworkDataSource");
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
     * Starts an intent service to fetch the weather.
     */
    public void startFetchWeatherService() {
        Intent intentToFetch = new Intent(context, SyncIntentService.class);
        context.startService(intentToFetch);
        Log.d(TAG, "startFetchWeatherService: IntentService created");
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
                        //TODO: Averiguar como asignar esto alv
                        //newsArray = LiveData<response.body()>;
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<News>> call, Throwable t) {
                    t.printStackTrace();
                }
            });

        });
    }
}