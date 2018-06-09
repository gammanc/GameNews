package com.gamma.gamenews.data.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emers on 6/6/2018.
 */

@Dao
public interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNews(ArrayList<News> news);

    @Query("SELECT * FROM news ORDER BY created_date DESC")
    List<News> getAll();

    @Insert
    void bulkInsert(News... weather);
}
