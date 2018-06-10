package com.gamma.gamenews.ui.newsdetail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.gamma.gamenews.data.DataRepository;
import com.gamma.gamenews.data.database.News;


public class NewsDetailViewModel extends ViewModel {
    private final LiveData<News> mNew;

    private final DataRepository dataRepository;

    public NewsDetailViewModel(DataRepository repository, String id){
        dataRepository = repository;
        mNew = dataRepository.getNewById(id);
    }

    public LiveData<News> getNew() {
        return mNew;
    }

}
