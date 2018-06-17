package com.gamma.gamenews.ui.gameinfo;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamma.gamenews.R;
import com.gamma.gamenews.ui.newslist.NewsFragment;

public class GamesFragment extends Fragment {


    public GamesFragment() {}

    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    private NewsFragment newsFragment;
    private NewsFragment newsFragment1;
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_games, container, false);
        prepareTabs();
        return view;
    }

    public void prepareTabs(){
        tabLayout = view.findViewById(R.id.main_tablayout);
        viewPager = view.findViewById(R.id.main_viewpager);
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        //getSupportActionBar().setElevation(0);

        Bundle args = getArguments();
        String game = args.getString("game");

        newsFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", 3);
        bundle.putString("game", game);
        newsFragment.setArguments(bundle);

        adapter.addFragment(newsFragment,"News");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
