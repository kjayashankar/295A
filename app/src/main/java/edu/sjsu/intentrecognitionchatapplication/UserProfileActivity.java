package edu.sjsu.intentrecognitionchatapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.login.Login;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.HashMap;

public class UserProfileActivity extends LoginActivity {

    private ImageView profilePic;
    private TextView userFullName, userEmail;
    private Button logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profilePic = (ImageView) findViewById(R.id.profile_pic);
        userFullName = (TextView) findViewById(R.id.user_full_name);
        userEmail = (TextView) findViewById(R.id.user_email);

        logOut = (Button) findViewById(R.id.bn_logout);
        logOut.setOnClickListener(this);

        HashMap<String, String> user = session.getUserDetails();
        userFullName.setText(user.get(SessionManager.NAME));
        userEmail.setText(user.get(SessionManager.EMAIL));
        if(user.get(SessionManager.PHOTO_URL) != null){
            if(user.get(SessionManager.PHOTO_URL).trim().length() > 0)
                Glide.with(this).load(user.get(SessionManager.PHOTO_URL)).into(profilePic);
        }
    }
}
