package com.gamma.gamenews.data.network.deserializer;

import android.util.Log;

import com.gamma.gamenews.utils.SharedPreference;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class UserDeserializer implements JsonDeserializer<ArrayList<String>> {

    private static final String TAG = "GN:UserDeserializer";
    @Override
    public ArrayList<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ArrayList<String> f = new ArrayList<>();
        JsonObject userinfo = json.getAsJsonObject();
        if(userinfo != null){
            if (userinfo.has("_id")){
                SharedPreference.write(SharedPreference.USER_ID, userinfo.get("_id").getAsString());
            }
            if(userinfo.has("favoriteNews")){
                JsonArray news = userinfo.getAsJsonArray("favoriteNews");
                for(JsonElement j:news){
                    f.add(j.getAsString());
                }
            }
        }
        return f;
    }
}
