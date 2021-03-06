package com.gamma.gamenews.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gamma.gamenews.R;
import com.gamma.gamenews.data.network.DataService;
import com.gamma.gamenews.data.network.NetworkDataSource;
import com.gamma.gamenews.data.network.NetworkUtils;
import com.gamma.gamenews.ui.gameinfo.GamesFragment;
import com.gamma.gamenews.ui.newslist.NewsFragment;
import com.gamma.gamenews.utils.DependencyContainer;
import com.gamma.gamenews.utils.SharedPreference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "GN:MainActivity";
    private Fragment contentFragment;
    private FragmentManager fragmentManager;
    TextView lblUser, lblToken;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreference.init(getApplicationContext());
        Log.d(TAG, "onCreate: Checking login");
        if(SharedPreference.checkLogin()){
            finishAffinity();
            //finish();
            Log.d(TAG, "onCreate: No login");
        }
        findViews();

        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null){
            if (savedInstanceState.containsKey("content")){
                String content = savedInstanceState.getString("content");
                if (content.equals("news") && fragmentManager.findFragmentByTag("news")!=null){
                    contentFragment = fragmentManager.findFragmentByTag("news");
                }
            }
        } else {
            Bundle args = new Bundle();
            args.putInt("type",1);
            NewsFragment newsFragment = new NewsFragment();
            newsFragment.setArguments(args);
            setTitle(R.string.app_name);
            switchContent(newsFragment,"news");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (contentFragment instanceof NewsFragment)
            outState.putString("content", "news");
        super.onSaveInstanceState(outState);
    }



    void findViews(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        );

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_news);
        lblUser = navigationView.getHeaderView(0).findViewById(R.id.lblUser);
        lblUser.setText(SharedPreference.read(SharedPreference.KEY_NAME,null));

        loadGames();
        loadGameOptions(SharedPreference.getGames());
    }

    void loadGames(){
        DataService dataService = NetworkUtils.getClientInstanceAuth();
        Call<String[]> games = dataService.getGames();
        games.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(@NonNull Call<String[]> call,
                                   @NonNull Response<String[]> response) {
                if (response.isSuccessful()){
                    SharedPreference.setGames(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String[]> call,
                                  @NonNull Throwable t) {
                Log.d(TAG, "getUserDet: onFailure: the response failed : +"+t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void navigate(){
        if (fragmentManager.getBackStackEntryCount() > 0) {
            super.onBackPressed();
            if(getSupportActionBar()!=null &&
                    !getSupportActionBar().getTitle().equals(getResources().getString(R.string.app_name))){
                setTitle(R.string.app_name);
                navigationView.setCheckedItem(R.id.nav_news);
            }
        } else if (contentFragment instanceof NewsFragment
                || fragmentManager.getBackStackEntryCount() == 0) {
            finish();
        }
    }

    public void switchContent(Fragment fragment, String tag) {
        //while (fragmentManager.popBackStackImmediate());

        if (fragment != null){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            //transaction.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_up,
              //      R.anim.slide_in_down, R.anim.slide_out_up);
            transaction.replace(R.id.main_container, fragment, tag);

            if(fragment instanceof  NewsFragment &&
                    fragment.getArguments().getInt("type")==2){
                transaction.addToBackStack(tag);
            }

            if(!(fragment instanceof NewsFragment)){
                transaction.addToBackStack(tag);
            }
            transaction.commit();
            contentFragment = fragment;
        }
    }

    public void setTitle(int resource){
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(resource));
    }

    /* Navigation Drawer Methods */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            navigate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            SharedPreference.logOutUser();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Bundle args = new Bundle();
        switch(id){
            case R.id.nav_news:
                args.putInt("type",1);
                NewsFragment newsFragment = new NewsFragment();
                newsFragment.setArguments(args);
                setTitle(R.string.app_name);
                switchContent(newsFragment,"news");
                break;
            case R.id.nav_favs:
                args.putInt("type",2);
                NewsFragment favNewsFragment = new NewsFragment();
                favNewsFragment.setArguments(args);
                setTitle(R.string.favorites);
                switchContent(favNewsFragment,"favnews");
                break;
            case R.id.item_logout:
                SharedPreference.logOutUser();
                finish();
                break;
            default:
                args.putString("game",item.getTitle().toString());
                GamesFragment gamesFragment = new GamesFragment();
                gamesFragment.setArguments(args);
                setTitle(R.string.app_name);
                switchContent(gamesFragment,"gamenews");
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadGameOptions(String[] categories) {
        MenuItem item = navigationView.getMenu().findItem(R.id.submenu_games);
        SubMenu games = item.getSubMenu();
        games.clear();
        //int count = 1000;
        for (String category : categories) {
            games.add(category).setIcon(R.drawable.ic_game).setCheckable(true);
            //games.add(1, count, count, category).setIcon(R.drawable.ic_game).setCheckable(true);
            //count++;
        }
        games.setGroupCheckable(1, true, true);
    }
}
