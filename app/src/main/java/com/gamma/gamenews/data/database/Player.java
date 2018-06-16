package com.gamma.gamenews.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;


@Entity(tableName = "player")
public class Player {
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    private String id;
    private String name;
    private String biografia;
    private String avatar;
    private String coverImage;
    private String game;

    public Player(@NonNull String id, String name, String biografia,
                  String avatar, String coverImage, String game) {
        this.id = id;
        this.name = name;
        this.biografia = biografia;
        this.avatar = avatar;
        this.coverImage = coverImage;
        this.game = game;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBiografia() {
        return biografia;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getGame() {
        return game;
    }
}
