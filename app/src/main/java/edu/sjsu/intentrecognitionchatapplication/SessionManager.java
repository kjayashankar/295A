package edu.sjsu.intentrecognitionchatapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;

/**
 * Created by satya on 10/16/17.
 */

public class SessionManager extends AppCompatActivity {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    private int PRIVATE_MODE = 0;
    private static SessionManager sessionManager = null;


    private static final String PREF_NAME = "ChatBotPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PHOTO_URL = "photoURL";
    public static final String LOCAL_LOGIN = "IsLocallyLoggedIn";

    private SessionManager(Context context){
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = prefs.edit();
    }

    public static synchronized SessionManager getSession(Context context){
        if(sessionManager == null)
            sessionManager = new SessionManager(context);
        return sessionManager;
    }

    // Creating login Session
    public void createLoginSession(String name, String email, String photoURL, boolean localLogin){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(PHOTO_URL, photoURL);
        editor.putBoolean(LOCAL_LOGIN, localLogin);
        editor.commit();
    }

    // Return Stored User Session Data
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
        startLoginActivity();
    }

    // Check Login Status
    public void checkLogin(){
        if(!prefs.getBoolean(IS_LOGIN, false))
            startLoginActivity();
    }

    // Helper method to start Login Activity Intent
    private void startLoginActivity(){
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    // Get Login Action
    public boolean isLocalLogin(){
        return prefs.getBoolean(LOCAL_LOGIN, false);
    }

}
