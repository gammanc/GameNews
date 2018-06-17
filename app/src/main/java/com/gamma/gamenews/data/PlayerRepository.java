package com.gamma.gamenews.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.gamma.gamenews.AppExecutors;
import com.gamma.gamenews.data.database.News;
import com.gamma.gamenews.data.database.NewsDao;
import com.gamma.gamenews.data.database.Player;
import com.gamma.gamenews.data.database.PlayerDao;
import com.gamma.gamenews.data.network.NetworkDataSource;

import java.util.ArrayList;
import java.util.List;

public class PlayerRepository {
    private static final String TAG = "GN:PlayerRepository";

    private static PlayerRepository instance;
    private static final Object LOCK = new Object();

    private final PlayerDao playerDao;
    private final NetworkDataSource networkDataSource;
    private final AppExecutors executors;

    public PlayerRepository(PlayerDao newsDao,
                            NetworkDataSource networkDataSource,
                            AppExecutors executors) {
        this.playerDao = newsDao;
        this.networkDataSource = networkDataSource;
        this.executors = executors;

        LiveData<ArrayList<Player>> downloadedNews = networkDataSource.getPlayers();
        downloadedNews.observeForever(
                players -> this.executors.diskIO().execute(() -> {
                    playerDao.deleteAll();
                    playerDao.insertPlayers(players);
                })
        );
    }

    public synchronized static PlayerRepository getInstance(PlayerDao playerDao,
                                                          NetworkDataSource networkDataSource,
                                                          AppExecutors executors){
        if(instance == null){
            synchronized (LOCK){
                instance = new PlayerRepository(playerDao,networkDataSource, executors);
            }
        }
        return instance;
    }

    private synchronized void initializeData(){
        networkDataSource.fetchPlayers();
    }
    public LiveData<List<Player>>getPlayers(String game){
        initializeData();
        return playerDao.getPlayersByGame(game);
    }
}
