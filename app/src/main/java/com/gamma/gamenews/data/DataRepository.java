package com.gamma.gamenews.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.gamma.gamenews.AppExecutors;
import com.gamma.gamenews.data.database.News;
import com.gamma.gamenews.data.database.NewsDao;
import com.gamma.gamenews.data.network.NetworkDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles data operations in the app
 * */
public class DataRepository {
    private static final String TAG = "GN:DataRepository";

    private static DataRepository instance;
    private static final Object LOCK = new Object();

    private final NewsDao newsDao;
    private final NetworkDataSource networkDataSource;
    private final AppExecutors executors;

    private boolean initialized = false;

    private DataRepository(NewsDao newsDao,
                          NetworkDataSource networkDataSource,
                          AppExecutors executors) {
        this.newsDao = newsDao;
        this.networkDataSource = networkDataSource;
        this.executors = executors;

        LiveData<ArrayList<News>> downloadedNews = networkDataSource.getCurrentNews();
        downloadedNews.observeForever(
                news -> this.executors.diskIO().execute(() -> {
                    Log.d(TAG, "DataRepository: truncating News table");
                    newsDao.deleteAll();
                    Log.d(TAG, "DataRepository: Inserting into database");
                    newsDao.insertNews(news);
                })
        );

        LiveData<String[]> favorites = networkDataSource.getCurrentFavs();
        favorites.observeForever(
                favs -> this.executors.diskIO().execute(() ->{
                    Log.d(TAG, "DataRepository: Updating favorite news");
                    for (String fav:favs){
                        newsDao.updateFavorite(fav,true);
                    }
                })
        );
    }

    public synchronized static DataRepository getInstance(NewsDao newsDao,
                                                          NetworkDataSource networkDataSource,
                                                          AppExecutors executors){
        Log.d(TAG, "getInstance: Providing Repository");
        if(instance == null){
            synchronized (LOCK){
                instance = new DataRepository(newsDao,networkDataSource, executors);
                Log.d(TAG, "getInstance: New repository made");
            }
        }
        return instance;
    }

    /**
     * Performs periodic sync tasks and check if a sync is required
     * */
    private synchronized void initializeData(){
        Log.d(TAG, "initializeData? "+(!initialized?"Yes":"No"));
        initialized = true;
        networkDataSource.fetchUserDetails();
    }

    // Database operations

    public LiveData<News> getNewById(String id){
        //initializeData();
        return newsDao.getNewDetail(id);
    }

    public LiveData<List<News>>getNews(){
        initializeData();
        return newsDao.getAll();
    }

    public LiveData<List<News>>getFavNews(){
        //initializeData();
        return newsDao.getAllFavs();
    }

    public LiveData<List<News>> getNewsbyGame(String game){
        //initializeData();
        return newsDao.getNewsByGame(game);
    }

    public void updateFavorite(String newid, boolean fav){
        executors.diskIO().execute(()-> newsDao.updateFavorite(newid,fav));
    }

    //TODO: complete this methods
    private void deleteOldData(){

    }

    private boolean isFetchNeeded(){
        return true;
    }

    /*public void startFetchService(){
        networkDataSource.startFetchNewsService();
    }*/
}
