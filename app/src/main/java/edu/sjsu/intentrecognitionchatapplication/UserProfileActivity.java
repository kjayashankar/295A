package edu.sjsu.intentrecognitionchatapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;

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
            if(user.get(SessionManager.PHOTO_URL).trim().length() < 1)
                return;
            if( AccessToken.getCurrentAccessToken() == null)
                Glide.with(this).load(user.get(SessionManager.PHOTO_URL)).into(profilePic);
            else
                Glide.with(this).load(user.get(SessionManager.PHOTO_URL)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(profilePic);

        }
    }
}
