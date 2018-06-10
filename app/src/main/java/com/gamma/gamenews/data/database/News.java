package com.gamma.gamenews.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * News entity definition
 */
@Entity(tableName = "news")
public class News {

    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    private String id;
    private String title;
    private String body;
    private String game;
    private String coverImage;
    private String description;
    private Date created_date;

    public News(String id, String title, String body, String game,
                String coverImage, String description, Date created_date) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.game = game;
        this.coverImage = coverImage;
        this.description = description;
        this.created_date = created_date;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getGame() {
        return game;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getDescription() {
        return description;
    }

    public Date getCreated_date() {
        return created_date;
    }

}
