package com.gamma.gamenews.ui.gameinfo;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> frList = new ArrayList<>();
    private final  List<String> frTitles = new ArrayList<>();
    private FragmentManager fragmentManager;
    private final Map<Integer, String> frTags;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentManager = fm;
        frTags = new HashMap<Integer, String>();
    }

    @Override
    public Fragment getItem(int position) {
        return frList.get(position);
    }

    @Override
    public int getCount() {
        return frTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return frTitles.get(position);
    }

    public void addFragment(Fragment f, String title){
        frList.add(f);
        frTitles.add(title);
    }

    @Override
    @NonNull
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if(obj instanceof Fragment){
            Fragment f = (Fragment) obj;
            frTags.put(position, f.getTag());
        }
        return obj;
    }


    public Fragment getFragment(int position){
        String tag = frTags.get(position);
        if(tag == null) return  null;
        return fragmentManager.findFragmentByTag(tag);
    }
}
