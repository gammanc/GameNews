package com.gamma.gamenews.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.gamma.gamenews.LoginActivity;

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

    public static final String IS_LOGGED_IN = "islogged";
    public static final String KEY_NAME = "username";
    public static final String TOKEN = "token";

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

    public static void write(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void write(String key, Integer value) {
        editor.putInt(key, value).commit();
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void delete(String key){;
        editor.remove(key);
    }

    /* Session related functions */
    public static void logInUser(String name, String token){
        write(IS_LOGGED_IN, true);
        write(KEY_NAME, name);
        write(TOKEN, token);
    }

    public static boolean checkLogin(){
        if(!isLoggedIn()){
            Intent i = new Intent(mContext, LoginActivity.class);

            //Close all the activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    public static boolean isLoggedIn(){
        return read(IS_LOGGED_IN,false);
    }
}

