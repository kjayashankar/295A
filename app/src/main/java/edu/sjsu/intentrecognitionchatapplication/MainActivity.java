package edu.sjsu.intentrecognitionchatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    @Override
    protected void onResume() {
        super.onResume();
        com.spark.submitbutton.SubmitButton chatActivity=(com.spark.submitbutton.SubmitButton) findViewById(R.id.chat_button);
        chatActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int DELAY = 1600;

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("from thred");
                        Intent intent = new Intent(MainActivity.this, ChatFriendsActivity.class);
                        startActivity(intent);
                    }
                }, DELAY);
            }
        });

        com.spark.submitbutton.SubmitButton ProfileActivity=(com.spark.submitbutton.SubmitButton) findViewById(R.id.profile);
        ProfileActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int DELAY = 1600;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("from thred");
                        Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                        startActivity(intent);
                    }
                }, DELAY);
            }
        });

        com.spark.submitbutton.SubmitButton FriendsActivity=(com.spark.submitbutton.SubmitButton) findViewById(R.id.Friends);
        FriendsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                int DELAY = 1600;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("from thread");
                        Intent intent = new Intent(MainActivity.this, ManageFriendsActivity.class);
                        startActivity(intent);
                    }
                }, DELAY);

            }
        });

    }

    @Override
    public void onRestart() {
        super.onRestart();
        setContentView(R.layout.activity_main);
    }
}
