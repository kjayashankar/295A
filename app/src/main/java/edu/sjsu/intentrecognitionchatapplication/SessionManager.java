package edu.sjsu.intentrecognitionchatapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by satya on 10/16/17.
 */

public class SessionManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    private int PRIVATE_MODE = 0;


    private static final String PREF_NAME = "ChatBotPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PHOTO_URL = "photoURL";

    public SessionManager(Context context){
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = prefs.edit();
    }

    // Creating login Session
    public void createLoginSession(String name, String email, String photoURL){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(PHOTO_URL, photoURL);
        editor.commit();
    }

    // Return Stored Session Data
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(NAME, prefs.getString(NAME, null));
        user.put(EMAIL, prefs.getString(EMAIL, null));
        user.put(PHOTO_URL, prefs.getString(PHOTO_URL, null));
        return user;
    }

    // Clear Session and redirect to Login Activity
    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }


    // Get Login Status
    public boolean isLoggedIn(){
        return prefs.getBoolean(IS_LOGIN, false);
    }

}
