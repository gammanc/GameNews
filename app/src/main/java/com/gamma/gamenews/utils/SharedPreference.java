package com.gamma.gamenews.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.gamma.gamenews.ui.LoginActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*
* Call once in an activity
* SharedPreference.init(getApplicationContext());
*
* Writing
* SharedPreference.write(SharedPreference.ATTRIB, "XXXX");
*
* Reading
* String name = SharedPreference.read(SharedPref.ATTRIB, null);
* */

public class SharedPreference {
    private static final String APP_SETTINGS = "default_settings";
    private static SharedPreferences mSharedPref;
    private static SharedPreferences.Editor editor;
    private static Context mContext;

    private static final String IS_LOGGED_IN = "islogged";
    public static final String KEY_NAME = "username";
    public static final String TOKEN = "token";
    public static final String USER_ID = "userid";
    public static final String GAMES = "games";
    public static final String FAVS = "favorites";

    private static final String TAG = "GN:SharedPreference";

    private SharedPreference() {
    }

    public static void init(Context context)
    {
        mContext = context;
        if(mSharedPref == null){
            mSharedPref = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
            editor = mSharedPref.edit();
        }
    }

    public static void write(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    private static void write(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void write(String key, Integer value) {
        editor.putInt(key, value).commit();
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    private static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void delete(String key){
        editor.remove(key);
    }

    public static void setGames(String[] games){
        String allgames = TextUtils.join(",",games);
        Log.d(TAG, "setGames: "+allgames);
        write(GAMES, allgames);
    }

    public static String[] getGames(){
        return read(GAMES,"").split(",");
    }

    public boolean hasGames(){
        return !read(GAMES, "").isEmpty();
    }

    /* Session related functions */
    public static void logInUser(String name, String token){
        write(IS_LOGGED_IN, true);
        write(KEY_NAME, name);
        write(TOKEN, token);
        Log.d("GN: SharedPref:", "token: "+token);
    }

    public static boolean checkLogin(){
        if(!isLoggedIn()){
            Intent i = new Intent(mContext, LoginActivity.class);

            //Close all the activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            mContext.startActivity(i);
            return true;
        }
        return false;
    }

    public static void logOutUser(){
        editor.clear();
        editor.commit();
        checkLogin();
    }

    private static boolean isLoggedIn(){
        return read(IS_LOGGED_IN,false);
    }
}

