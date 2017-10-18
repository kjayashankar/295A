package edu.sjsu.intentrecognitionchatapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by satya on 10/14/17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private Button localLogin, fbLogin, googleLogin;
    private GoogleApiClient googleApiClient;
    private EditText inputUserName, inputPassword;
    private static final int REQ_CODE = 9001;
    private AlertDialog.Builder alertBuilder;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(getApplicationContext());
        localLogin = (Button) findViewById(R.id.bn_local_login);
        fbLogin = (Button) findViewById(R.id.bn_fb_login);
        googleLogin = (Button) findViewById(R.id.bn_google_login);
        inputUserName = (EditText) findViewById(R.id.user_name);
        inputPassword = (EditText) findViewById(R.id.user_password);
        localLogin.setOnClickListener(this);
        fbLogin.setOnClickListener(this);
        googleLogin.setOnClickListener(this);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.bn_google_login :
                doGoogleLogin();
                break;
            case R.id.bn_fb_login :
                doFacebookLogin();
                break;
            case R.id.bn_local_login :
                doLocalLogin();
                break;
        }
    }

    private void doLocalLogin(){
        String userName = inputUserName.getText().toString();
        String password = inputPassword.getText().toString();
        if(userName == null || password == null){
            return;
        }

        if(userName.trim().length()>0 && password.trim().length()>0){
            if(userName.equals("satya") && password.equals("satya")){
                session.createLoginSession(userName, userName, null);
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivity(intent);
            }else{
                alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("Login Failed")
                        .setMessage("Incorrect Username/Password")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setIcon(R.drawable.delete1).show();
            }
        }else{
            alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle("Invalid Input")
                    .setMessage("Please enter Username & Password")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setIcon(R.drawable.delete1).show();
        }

    }

    private void doFacebookLogin(){

    }

    ////////// Google Authentication
    private void doGoogleLogin(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    private void doGoogleLogout(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                // success logout
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }

    private void handleResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account =  result.getSignInAccount();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String picURL = account.getPhotoUrl().toString();
            session.createLoginSession(name, email, picURL);
            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(intent);
        }
    }

    public void onConnectionFailed(@NonNull ConnectionResult result){

    }

}

