package edu.sjsu.intentrecognitionchatapplication.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jay on 11/7/17.
 */

public class JSONUtils {

    private static final String TEXT = "TEXT";
    private static final String TAG = "JSONUtils";
    private static final String PIC = "PIC";

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
