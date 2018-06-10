package com.gamma.gamenews.ui.newsdetail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.gamma.gamenews.data.DataRepository;

public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final DataRepository dataRepository;
    private final String id;

    public DetailViewModelFactory(DataRepository repository, String id){
        dataRepository = repository;
        this.id = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NewsDetailViewModel(dataRepository, id);
    }
}
