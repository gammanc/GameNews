package com.gamma.gamenews.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

/**
 * Created by emers on 6/6/2018.
 */

@Database(entities = {News.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class GNDatabase extends RoomDatabase{
    private static final String DATABASE_NAME = "game_news_db";

    private static final Object LOCK = new Object();
    private static volatile GNDatabase instance;

    public static GNDatabase getInstance(Context context){
        if (instance == null){
            synchronized (LOCK){
                if (instance == null){
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            GNDatabase.class, GNDatabase.DATABASE_NAME).build();
                }
            }
        }
        return instance;
    }

    public abstract NewsDao newsDao();
}