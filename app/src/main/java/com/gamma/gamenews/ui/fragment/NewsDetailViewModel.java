package com.gamma.gamenews.ui.fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.gamma.gamenews.data.database.News;

/**
 * Created by emers on 6/6/2018.
 */

public class NewsDetailViewModel extends ViewModel {
    private MutableLiveData<News> mNew = new MutableLiveData<>();

    public NewsDetailViewModel(){
        mNew = new MutableLiveData<>();
    }

    public LiveData<News> getNew() {
        return mNew;
    }

    public void setNew(News news) {
        mNew.setValue(news);
    }
}
