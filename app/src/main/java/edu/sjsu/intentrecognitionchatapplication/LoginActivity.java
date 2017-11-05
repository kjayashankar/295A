package edu.sjsu.intentrecognitionchatapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by satya on 10/14/17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private Button localLogin;
    private SignInButton googleLogin;
    private LoginButton fbLogin;
    private GoogleApiClient googleApiClient;
    private CallbackManager callbackManager;
    private EditText inputUserName, inputPassword;
    private static final int REQ_CODE = 9001;
    private AlertDialog.Builder alertBuilder;
    public static SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Local Auth Setup
        inputUserName = (EditText) findViewById(R.id.user_name);
        inputPassword = (EditText) findViewById(R.id.user_password);
        localLogin = (Button) findViewById(R.id.bn_local_login);
        session = new SessionManager(getApplicationContext());
        localLogin.setOnClickListener(this);

        //Google Auth Setup
        googleLogin = (SignInButton) findViewById(R.id.bn_google_login);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions).build();
        googleLogin.setOnClickListener(this);

        //FB Auth Setup
        fbLogin = (LoginButton) findViewById(R.id.bn_fb_login);
        fbLogin.setReadPermissions(Arrays.asList("public_profile", "email"));
        callbackManager = CallbackManager.Factory.create();
        doFacebookLogin();
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.bn_local_login :
                doLocalLogin();
                break;
            case R.id.bn_google_login :
                doGoogleLogin();
                break;
            case R.id.bn_logout :
                doLogout();
                break;
        }
    }

    private void doLogout(){
        if(session.isLocalLogin()){
            session.logoutUser();
        }else if(AccessToken.getCurrentAccessToken() != null){
            LoginManager.getInstance().logOut();
            session.logoutUser();
        }else{
            doGoogleLogout();
        }
    }

    ////////// Local Authentication
    private void doLocalLogin(){
        String userName = inputUserName.getText().toString();
        String password = inputPassword.getText().toString();

        if(userName.trim().length()>0 && password.trim().length()>0){
            if(userName.equals("satya") && password.equals("satya")){
                session.createLoginSession(userName, "satya@gmail.com", null, true);
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Logging In..", Toast.LENGTH_SHORT).show();
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

    ////////// Facebook Authentication
    private void doFacebookLogin(){
        fbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                fetchFBUserProfile(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException error) {}
        });
    }

    private void fetchFBUserProfile(AccessToken accessToken){
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse response) {
                try {
                    String name = jsonObject.getString("name");
                    String email =  jsonObject.getString("email");
                    String picURL = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");
                    session.createLoginSession(name, email, picURL, false);
                    Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Logging In..", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    ////////// Google Authentication
    private void doGoogleLogin(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account =  result.getSignInAccount();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String picURL = null;
            if(account.getPhotoUrl() != null)
                picURL = picURL = account.getPhotoUrl().toString();
            session.createLoginSession(name, email, picURL, false);
            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Logging In..", Toast.LENGTH_SHORT).show();
        }
    }

    public void onConnectionFailed(@NonNull ConnectionResult result){
        String abc;
    }

    public void doGoogleLogout(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                session.logoutUser();
            }
        });
    }

}

