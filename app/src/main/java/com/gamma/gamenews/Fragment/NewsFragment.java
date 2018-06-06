package com.gamma.gamenews.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamma.gamenews.Adapter.NewsAdapter;
import com.gamma.gamenews.Beans.News;
import com.gamma.gamenews.R;
import com.gamma.gamenews.Utils.Client;
import com.gamma.gamenews.Utils.DataService;
import com.gamma.gamenews.Utils.SharedPreference;

import java.sql.SQLOutput;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsFragment extends Fragment {

    RecyclerView newsRecycler;
    NewsAdapter newsAdapter;
    ArrayList<News> newsArray;

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
        getNewsList();
        return v;
    }

    private void getNewsList(){
        DataService service = Client.getClientInstanceAuth().create(DataService.class);
        Call<ArrayList<News>> call = service.getNewsList();
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
        newsAdapter = new NewsAdapter(getContext(), newsArray);
        newsRecycler.setAdapter(newsAdapter);
    }
}