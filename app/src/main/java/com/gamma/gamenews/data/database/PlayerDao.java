package com.gamma.gamenews.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface PlayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlayers(ArrayList<Player> players);

    @Query("SELECT * FROM player WHERE game = :game")
    LiveData<List<Player>> getPlayersByGame(String game);

    @Query("SELECT * FROM player WHERE id = :id")
    LiveData<Player> getPlayerInfo(String id);

    @Query("DELETE FROM player")
    void deleteAll();
}
