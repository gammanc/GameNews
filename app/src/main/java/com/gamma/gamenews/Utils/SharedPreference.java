package com.gamma.gamenews.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/*
* Call once in an activity
* SharedPreference.init(getApplicationContext());
*
* Writing
* SharedPref.write(SharedPreference.ATTRIB, "XXXX");
*
* Reading
* String name = SharedPreference.read(SharedPref.ATTRIB, null);
* */

public class SharedPreference {
    private static final String APP_SETTINGS = "default_settings";

    private static SharedPreferences mSharedPref;
    public static final String NAME = "NAME";
    public static final String AGE = "AGE";
    public static final String IS_SELECT = "IS_SELECT";


    private SharedPreference() {
    }

    public static void init(Context context)
    {
        if(mSharedPref == null)
            mSharedPref = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).commit();
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
}
