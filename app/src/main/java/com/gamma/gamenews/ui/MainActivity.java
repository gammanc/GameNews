package com.gamma.gamenews.ui;

import android.os.Bundle;
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
import android.view.View;
import android.widget.TextView;

import com.gamma.gamenews.R;
import com.gamma.gamenews.ui.newslist.NewsFragment;
import com.gamma.gamenews.utils.SharedPreference;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "GN:MainActivity";
    private Fragment contentFragment;
    private FragmentManager fragmentManager;
    TextView lblUser, lblToken;

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
            NewsFragment newsFragment = new NewsFragment();
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        lblUser = navigationView.getHeaderView(0).findViewById(R.id.lblUser);
        lblUser.setText(SharedPreference.read(SharedPreference.KEY_NAME,null));
    }

    public void navigate(){
        if (fragmentManager.getBackStackEntryCount() > 0) {
            super.onBackPressed();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SharedPreference.logOutUser();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch(id){
            case R.id.item_logout:
                SharedPreference.logOutUser();
                finish();
            default:
                ;
        }

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
