package com.gamma.gamenews.data;

/**
 * Created by emers on 8/6/2018.
 */

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.gamma.gamenews.AppExecutors;
import com.gamma.gamenews.data.database.News;
import com.gamma.gamenews.data.database.NewsDao;
import com.gamma.gamenews.data.network.NetworkDataSource;

import java.util.ArrayList;

/**
 * Acts like a mediator between the Retrofit Data and Room Data
 * */
public class DataRepository {
    private static final String TAG = DataRepository.class.getSimpleName();

    private static DataRepository instance;
    private static final Object LOCK = new Object();

    private final NewsDao newsDao;
    private final NetworkDataSource networkDataSource;
    private final AppExecutors executors;

    private boolean initialized = false;

    public DataRepository(NewsDao newsDao,
                          NetworkDataSource networkDataSource,
                          AppExecutors executors) {
        this.newsDao = newsDao;
        this.networkDataSource = networkDataSource;
        this.executors = executors;
        LiveData<ArrayList<News>> downloadedNews = networkDataSource.getCurrentNews();
        downloadedNews.observeForever(
                news -> executors.diskIO().execute(() -> newsDao.insertNews(news))
        );
    }

    public synchronized static DataRepository getInstance(NewsDao newsDao,
                                                          NetworkDataSource networkDataSource,
                                                          AppExecutors executors){
        Log.d(TAG, "getInstance: Getting the repository ...");
        if(instance == null){
            synchronized (LOCK){
                instance = new DataRepository(newsDao, networkDataSource, executors);
                Log.d(TAG, "getInstance: New repository made!");
            }
        }
        return instance;
    }

    /**
     * Performs periodic sync tasks and check if a sync is required
     * */
    public synchronized void initializeData(){
        if (initialized) return;
        initialized = true;
        startFetchService();
    }

    // Database operations

    //TODO: complete this methods
    private void deleteOldData(){

    }

    private boolean isFetchNeeded(){
        return true;
    }

    private void startFetchService(){
        networkDataSource.startFetchNewsService();
    }
}
