package edu.sjsu.intentrecognitionchatapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView profilePic;
    private TextView userFullName, userEmail;
    private Button logOut;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profilePic = (ImageView) findViewById(R.id.profile_pic);
        userFullName = (TextView) findViewById(R.id.user_full_name);
        userEmail = (TextView) findViewById(R.id.user_email);
        logOut = (Button) findViewById(R.id.bn_logout);
        logOut.setOnClickListener(this);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        userFullName.setText(user.get(SessionManager.NAME));
        userEmail.setText(user.get(SessionManager.EMAIL));
        if(user.get(SessionManager.PHOTO_URL).trim().length() > 0)
            Glide.with(this).load(user.get(SessionManager.PHOTO_URL)).into(profilePic);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}
