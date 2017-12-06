package edu.sjsu.intentrecognitionchatapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.HashMap;
import java.util.Map;

import edu.sjsu.intentrecognitionchatapplication.utils.Constants;

/**
 * Created by satya on 10/14/17.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private Button localLogin, localSignUp;
    private SignInButton googleLogin;
    private LoginButton fbLogin;
    private GoogleApiClient googleApiClient;
    private CallbackManager callbackManager;
    private EditText inputUserName, inputPassword;
    private static final int REQ_CODE = 9001;
    public static AlertDialog.Builder alertBuilder;
    public static SessionManager session;

    public static final String REGISTER_ENDPOINT_URL = Constants.HOST_BASE_URL + "/IntentChatServer/service/friendsPage/registerUser";
    public static final String AUTHENTICATE_ENDPOINT_URL = Constants.HOST_BASE_URL + "/IntentChatServer/service/friendsPage/authenticateUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Local Auth Setup
        inputUserName = (EditText) findViewById(R.id.user_name);
        inputPassword = (EditText) findViewById(R.id.user_password);
        localLogin = (Button) findViewById(R.id.bn_local_login);
        localSignUp = (Button) findViewById(R.id.bn_local_signup);
        session = SessionManager.getSession(getApplicationContext());
        localLogin.setOnClickListener(this);
        localSignUp.setOnClickListener(this);


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
            case R.id.bn_local_signup :
                doSignUp();
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

    private void doSignUp(){
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    ////////// Local Authentication
    private void doLocalLogin(){
        String userName = inputUserName.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if(userName.length()<1 || password.length()<1){
            showAlert("Invalid Input", "Please enter Username & Password", this);
            return;
        }
        authenticateUserFromDatabase(userName, password);
    }

    private void authenticateUserFromDatabase(final String userName, final String password) {
        StringRequest request = new StringRequest(Request.Method.POST, AUTHENTICATE_ENDPOINT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if("***fail".equals(response))
                            showAlert("Login Failed", "Incorrect Username/Password", LoginActivity.this);
                        else{
                            session.createLoginSession(response, userName, null, true);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Logging In..", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showAlert("Login Failed", "Please try again", LoginActivity.this);
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName", userName);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(request);
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
                    registerUserInDatabase(name, email, picURL, "Facebook");
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
                picURL = account.getPhotoUrl().toString();
            registerUserInDatabase(name, email, picURL, "Google");
        }
    }

    public void onConnectionFailed(@NonNull ConnectionResult result){}

    public void doGoogleLogout(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                session.logoutUser();
            }
        });
    }

    ////////// Helper methods to show Alert and register user in database
    public static void showAlert(String title, String message, Context context){
        alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle(title).setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setIcon(R.drawable.delete1).show();
    }

    private void registerUserInDatabase(final String name, final String email, final String picURL, final String authType) {
        StringRequest request = new StringRequest(Request.Method.POST, REGISTER_ENDPOINT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        session.createLoginSession(name, email, picURL, false);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Logging In..", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showAlert("Registration Failed", "Please try again", LoginActivity.this);
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                if(picURL != null)
                    params.put("picURL", picURL);
                params.put("authType", authType);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(request);
    }

}

