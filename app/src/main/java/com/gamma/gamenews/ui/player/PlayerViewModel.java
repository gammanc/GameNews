package com.gamma.gamenews.ui.player;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.gamma.gamenews.data.PlayerRepository;
import com.gamma.gamenews.data.database.News;
import com.gamma.gamenews.data.database.Player;

import java.util.List;

public class PlayerViewModel extends ViewModel{
    private LiveData<List<Player>> playerArrayList;

    private final PlayerRepository dataRepository;

    public PlayerViewModel(PlayerRepository repository){
        dataRepository = repository;
    }

    public LiveData<List<Player>> getPlayersByGame(String game){
        playerArrayList = dataRepository.getPlayers(game);
        return playerArrayList;
    }

    public void refreshNews(String game){
        playerArrayList = dataRepository.getPlayers(game);
    }
}
