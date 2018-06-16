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
        //newsArrayList = dataRepository.getNews();
    }

    public LiveData<List<News>> getLatestNews(){
        newsArrayList = dataRepository.getNews();
        return newsArrayList;
    }

    public LiveData<List<News>> getFavNews(){
        newsArrayList = dataRepository.getFavNews();
        return newsArrayList;
    }

    public void refreshNews(){
        newsArrayList = dataRepository.getNews();
    }

    public void refreshFavsNews() { newsArrayList = dataRepository.getFavNews(); }
}
