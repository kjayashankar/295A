package edu.sjsu.intentrecognitionchatapplication.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jay on 11/7/17.
 */

public class JSONUtils {

    private static final String TEXT = "TEXT";
    private static final String TAG = "JSONUtils";
    private static final String PIC = "PIC";

    private static Gson gson;

//    public static Gson getGsonParser() {
//        if(null == gson) {
//            GsonBuilder builder = new GsonBuilder();
//            gson = builder.create();
//        }
//        return gson;
//    }


    public static JSONObject exchangeForString(String currentUser, String friend, String chatText) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sender", currentUser);
            jsonObject.put("to", friend);
            jsonObject.put("MIME", TEXT);
            jsonObject.put("value", chatText);
            jsonObject.put("date", System.currentTimeMillis()+"");


        } catch (JSONException e) {
            Log.e(TAG, "json exception", e.fillInStackTrace());
        }
        return jsonObject;
    }


}
