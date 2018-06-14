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

import com.gamma.gamenews.AppExecutors;
import com.gamma.gamenews.data.network.NetworkDataSource;
import com.gamma.gamenews.ui.newsdetail.NewsDetailActivity;
import com.gamma.gamenews.ui.newsdetail.NewsDetailViewModel;
import com.gamma.gamenews.data.database.News;
import com.gamma.gamenews.R;
import com.gamma.gamenews.utils.DependencyContainer;

import java.util.List;


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
        Log.d(TAG, "onCreate: Setting NewsModelFactory");
        NewsViewModelFactory factory = DependencyContainer.getNewsViewModelFactory(getContext());

        Log.d(TAG, "onCreate: Setting newsViewModel");
        newsViewModel = ViewModelProviders.of(this, factory).get(NewsViewModel.class);
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
        Log.d(TAG, "onCreateView: Activating swipe animation");
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        newsViewModel.getLatestNews().observe(this, news -> {
            Log.d(TAG, "onActivityCreated: Ejecutando observer");
            Log.d(TAG, "observer: Hiding swipe animation");
            swipeRefreshLayout.setRefreshing(false);
            loadList(news);
        });
        Log.d(TAG, "onActivityCreated: newsViewModel prepared!");
    }

    void loadList(List<News> news){
        // TODO: Verificar si la lista está vacía
        newsArray = news;
        newsAdapter = new NewsAdapter(getContext(), newsArray,this);
        newsRecycler.setAdapter(newsAdapter);
        Log.d(TAG, "loadList: Hiding swipe animation");
        swipeRefreshLayout.post(()->{ swipeRefreshLayout.setRefreshing(false); });
    }

    @Override
    public void onNewsClick(News mNew) {
        Intent intent = new Intent(getContext(), NewsDetailActivity.class);
        intent.putExtra("id",mNew.getId());
        startActivity(intent);
    }

    @Override
    public void onNewsChecked(View v, String newid) {
        NetworkDataSource.getInstance(this.getContext(), AppExecutors.getInstance()).setFavorite(v,newid);
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

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Hi");
        if(newsAdapter != null){
            Log.d(TAG, "onResume: notifying...");
            newsAdapter.notifyDataSetChanged();
        }
    }
}
