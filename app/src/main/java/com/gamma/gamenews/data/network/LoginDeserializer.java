package com.gamma.gamenews.data.network;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class LoginDeserializer implements JsonDeserializer<String> {
    @Override
    public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject logininfo = json.getAsJsonObject();
        if(logininfo != null){
            if(logininfo.has("token"))
                return "token:"+logininfo.get("token").getAsString();
            else
                return "message:"+logininfo.get("message").getAsString();
        }
        return "message:Login Failed. Please try again later";
    }
}
