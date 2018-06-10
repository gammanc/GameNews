package com.gamma.gamenews.ui.newslist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.gamma.gamenews.data.DataRepository;
import com.gamma.gamenews.data.database.News;

import java.util.List;

/**
 * Created by emers on 9/6/2018.
 */

public class NewsViewModel extends ViewModel {
    private final LiveData<List<News>> newsArrayList;

    private final DataRepository dataRepository;

    public NewsViewModel(DataRepository repository){
        dataRepository = repository;
        newsArrayList = repository.getNews();
    }

    public LiveData<List<News>> getLatestNews(){
        return newsArrayList;
    }
}
