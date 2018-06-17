package com.gamma.gamenews.ui.player;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamma.gamenews.R;
import com.gamma.gamenews.data.database.News;
import com.gamma.gamenews.data.database.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerListFragment extends Fragment {

    RecyclerView recyclerView;
    PlayerAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Player> players = new ArrayList<>();

    private static final String TAG = "GN:NewsFragment";

    public PlayerListFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView = v.findViewById(R.id.main_recycler);
        recyclerView.setHasFixedSize(true);
        adapter = new PlayerAdapter(getContext(), players);
        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    void loadList(List<Player> players){
        // TODO: Verificar si la lista está vacía
        this.players.clear();
        this.players.addAll(players);

        adapter.notifyDataSetChanged();
        //swipeRefreshLayout.post(()-> swipeRefreshLayout.setRefreshing(false));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null){
            Log.d(TAG, "onResume: notifying...");
            adapter.notifyDataSetChanged();
        }
    }
}
