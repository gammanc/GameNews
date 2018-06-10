package com.gamma.gamenews.ui.newslist;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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


public class NewsFragment extends Fragment implements NewsAdapter.onNewsClickHandler{

    RecyclerView newsRecycler;
    NewsAdapter newsAdapter;
    List<News> newsArray;
    NewsDetailViewModel model;

    private static final String TAG = "GameNews - NewsFragment";

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

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onActivityCreated: Getting the ModelFactory");
        NewsViewModelFactory factory = DependencyContainer.getNewsViewModelFactory(getContext());
        NewsViewModel model = ViewModelProviders.of(this, factory).get(NewsViewModel.class);
        model.getLatestNews().observe(this, news -> {
            loadList(news);
        });
    }

    void loadList(List<News> news){
        // TODO: Verificar si la lista está vacía
        newsArray = news;
        newsAdapter = new NewsAdapter(getContext(), newsArray,this);
        newsRecycler.setAdapter(newsAdapter);
    }

    @Override
    public void onNewsClick(News mNew) {
        Intent intent = new Intent(getContext(), NewsDetailActivity.class);
        intent.putExtra("id",mNew.getId());
        startActivity(intent);
        /*
        NewsDetailFragment fragment = new NewsDetailFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down,
                        R.anim.slide_in_down, R.anim.slide_out_down)
                .add(R.id.main_container,fragment)
                .hide(fm.findFragmentByTag("news"))
                .addToBackStack("detail")
                .commit();*/
    }
}
