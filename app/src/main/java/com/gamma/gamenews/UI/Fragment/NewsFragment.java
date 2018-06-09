package com.gamma.gamenews.UI.Fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamma.gamenews.UI.Adapter.NewsAdapter;
import com.gamma.gamenews.Data.Database.News;
import com.gamma.gamenews.R;
import com.gamma.gamenews.Data.Network.NetworkUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsFragment extends Fragment implements NewsAdapter.onNewsClickHandler{

    RecyclerView newsRecycler;
    NewsAdapter newsAdapter;
    ArrayList<News> newsArray;
    NewsDetailViewModel model;

    final String TAG = "NewsFragment";

    public NewsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(NewsDetailViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news, container, false);

        newsRecycler = v.findViewById(R.id.main_recycler);
        newsRecycler.setHasFixedSize(true);
        newsRecycler.setLayoutManager(new LinearLayoutManager(container.getContext()));

        getNewsList();
        return v;
    }

    private void getNewsList(){
        Call<ArrayList<News>> call = NetworkUtils.getClientInstanceAuth().getNewsList();
        call.enqueue(new Callback<ArrayList<News>>() {
            @Override
            public void onResponse(Call<ArrayList<News>> call, Response<ArrayList<News>> response) {
                if(response.isSuccessful()){
                    loadList(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<News>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    void loadList(ArrayList<News> news){
        // TODO: Verificar si la lista está vacía
        newsArray = news;
        newsAdapter = new NewsAdapter(getContext(), newsArray,this);
        newsRecycler.setAdapter(newsAdapter);
    }

    @Override
    public void onNewsClick(News mNew) {
        model.setNew(mNew);
        NewsDetailFragment fragment = new NewsDetailFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down,
                        R.anim.slide_in_down, R.anim.slide_out_down)
                .add(R.id.main_container,fragment)
                .hide(fm.findFragmentByTag("news"))
                .addToBackStack("detail")
                .commit();
    }
}
