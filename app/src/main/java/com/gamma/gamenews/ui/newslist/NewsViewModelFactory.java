package com.gamma.gamenews.ui.newslist;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.gamma.gamenews.data.DataRepository;

public class NewsViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final DataRepository dataRepository;

    public NewsViewModelFactory(DataRepository repository){
        dataRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NewsViewModel(dataRepository);
    }
}
