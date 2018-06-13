package com.gamma.gamenews.ui.newslist;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamma.gamenews.ui.newsdetail.DetailViewModelFactory;
import com.gamma.gamenews.ui.newsdetail.NewsDetailActivity;
import com.gamma.gamenews.ui.newsdetail.NewsDetailViewModel;
import com.gamma.gamenews.ui.newslist.NewsAdapter;
import com.gamma.gamenews.data.database.News;
import com.gamma.gamenews.R;
import com.gamma.gamenews.data.network.NetworkUtils;
import com.gamma.gamenews.utils.DependencyContainer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsFragment extends Fragment implements NewsAdapter.onNewsClickHandler, SwipeRefreshLayout.OnRefreshListener{

    RecyclerView newsRecycler;
    NewsAdapter newsAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    List<News> newsArray;
    NewsViewModel newsViewModel;
    NewsDetailViewModel model;

    private static final String TAG = "GN:NewsFragment";

    public NewsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news, container, false);

        newsRecycler = v.findViewById(R.id.main_recycler);
        newsRecycler.setHasFixedSize(true);
        newsRecycler.setLayoutManager(new LinearLayoutManager(container.getContext()));

        swipeRefreshLayout = v.findViewById(R.id.main_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorDivider, R.color.colorAccent);
        swipeRefreshLayout.post(()->{ swipeRefreshLayout.setRefreshing(true); });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onActivityCreated: Setting NewsModelFactory");
        NewsViewModelFactory factory = DependencyContainer.getNewsViewModelFactory(getContext());
        Log.d(TAG, "onActivityCreated: Setting newsViewModel");

        newsViewModel = ViewModelProviders.of(this, factory).get(NewsViewModel.class);
        newsViewModel.getLatestNews().observe(this, news -> {
            Log.d(TAG, "onActivityCreated: Ejecutando observer");
            swipeRefreshLayout.setRefreshing(false);
            loadList(news);
        });
        Log.d(TAG, "onActivityCreated: newsViewModel prepared!");

        /*swipeRefreshLayout.post(()->{
            swipeRefreshLayout.setRefreshing(true);
            refreshData();
        });*/
        //refreshData();

    }

    void loadList(List<News> news){
        // TODO: Verificar si la lista está vacía
        swipeRefreshLayout.setRefreshing(true);
        newsArray = news;
        newsAdapter = new NewsAdapter(getContext(), newsArray,this);
        newsRecycler.setAdapter(newsAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onNewsClick(News mNew) {
        Intent intent = new Intent(getContext(), NewsDetailActivity.class);
        intent.putExtra("id",mNew.getId());
        startActivity(intent);
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        Log.d(TAG, "onRefresh: Loading data...");
        newsViewModel.refreshNews();
        Log.d(TAG, "onRefresh: Finished");
        swipeRefreshLayout.setRefreshing(false);
    }

}
