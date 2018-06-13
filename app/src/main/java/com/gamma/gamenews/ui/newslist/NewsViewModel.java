package com.gamma.gamenews.ui.newslist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.gamma.gamenews.data.DataRepository;
import com.gamma.gamenews.data.database.News;

import java.util.List;

public class NewsViewModel extends ViewModel {
    private LiveData<List<News>> newsArrayList;

    private final DataRepository dataRepository;

    public NewsViewModel(DataRepository repository){
        dataRepository = repository;
        newsArrayList = dataRepository.getNews();
    }

    public LiveData<List<News>> getLatestNews(){
        return newsArrayList;
    }

    public void refreshNews(){
        newsArrayList = dataRepository.getNews();
    }
}
