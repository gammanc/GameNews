package com.gamma.gamenews.data.network.deserializer;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("_id")
    private String id;
    private String user;
    private String password;
    private String[] favoriteNews;

    public User(String id, String user, String password) {
        this.id = id;
        this.user = user;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String[] getFavoriteNews() {
        return favoriteNews;
    }
}