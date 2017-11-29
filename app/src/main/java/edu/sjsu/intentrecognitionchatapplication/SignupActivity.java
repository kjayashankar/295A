package edu.sjsu.intentrecognitionchatapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private Button register, login;
    private EditText name, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = (EditText) findViewById(R.id.register_name);
        email = (EditText) findViewById(R.id.register_email);
        password = (EditText) findViewById(R.id.register_password);
        register = (Button) findViewById(R.id.bn_register);
        login = (Button) findViewById(R.id.bn_sign_in);

        register.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.bn_sign_in :
                doSignIn();
                break;
            case R.id.bn_register :
                doRegister();
                break;
        }
    }

    private void doSignIn(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void doRegister() {
        final String userName = name.getText().toString().trim();
        final String userEmail = email.getText().toString().trim();
        final String userPassword = password.getText().toString().trim();

        if(userName.length()<1 || userEmail.length()<1 || userPassword.length()<1){
            LoginActivity.showAlert("Invalid Input", "All fields are mandatory", this);
            return;
        }

        if(userName.contains("*")){
            LoginActivity.showAlert("Invalid Input", "Full Name cannot contain '*' character", this);
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, LoginActivity.REGISTER_ENDPOINT_URL,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if("success".equals(response)){
                    System.out.println("******* Response:"+response);
                    doSignIn();
                    Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                }else{
                    LoginActivity.showAlert("Registration Failed", "Email already exists", SignupActivity.this);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoginActivity.showAlert("Registration Failed", "Please try again", SignupActivity.this);
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", userName);
                params.put("email", userEmail);
                params.put("password", userPassword);
                params.put("authType", "local");
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
        queue.add(request);
    }
}
