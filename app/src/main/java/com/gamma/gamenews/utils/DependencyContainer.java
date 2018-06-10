package com.gamma.gamenews.utils;

import android.content.Context;

import com.gamma.gamenews.AppExecutors;
import com.gamma.gamenews.data.DataRepository;
import com.gamma.gamenews.data.database.GNDatabase;
import com.gamma.gamenews.data.network.NetworkDataSource;
import com.gamma.gamenews.ui.newsdetail.DetailViewModelFactory;
import com.gamma.gamenews.ui.newslist.NewsViewModelFactory;

/**
 * Creates classes instances which other classes need
 * This to avoid hard dependencies creating within the actual class
 * */
public class DependencyContainer {

    public static DataRepository getRepository(Context context){
        GNDatabase database = GNDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();

        NetworkDataSource networkDataSource =
                NetworkDataSource.getInstance(context.getApplicationContext(),
                        executors);
        return DataRepository.getInstance(database.newsDao(),networkDataSource,executors);
    }

    public static NetworkDataSource getNetworkDataSource(Context context){
        AppExecutors executors = AppExecutors.getInstance();
        return NetworkDataSource.getInstance(context.getApplicationContext(), executors);
    }

    public static DetailViewModelFactory getDetailViewModelFactory(Context context, String id){
        DataRepository dataRepository = getRepository(context.getApplicationContext());
        return new DetailViewModelFactory(dataRepository, id);
    }

    public static NewsViewModelFactory getNewsViewModelFactory(Context context){
        DataRepository dataRepository = getRepository(context.getApplicationContext());
        return new NewsViewModelFactory(dataRepository);
    }
}
