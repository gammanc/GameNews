package com.gamma.gamenews.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
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
    LiveData<List<News>> getAll();

    @Query("SELECT * FROM news WHERE id = :newid")
    LiveData<News> getNewDetail(String newid);

    @Query("UPDATE news SET favorite = :favorite WHERE id LIKE :id")
    void updateFavorite(String id, boolean favorite);

    @Query("DELETE FROM news")
    void deleteAll();
}
