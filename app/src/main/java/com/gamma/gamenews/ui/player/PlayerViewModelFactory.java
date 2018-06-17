package com.gamma.gamenews.ui.player;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.gamma.gamenews.data.PlayerRepository;

public class PlayerViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final PlayerRepository repository;

    public PlayerViewModelFactory(PlayerRepository repository){
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PlayerViewModel(repository);
    }
}
