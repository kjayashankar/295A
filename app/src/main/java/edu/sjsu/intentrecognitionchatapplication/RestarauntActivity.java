package edu.sjsu.intentrecognitionchatapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import edu.sjsu.intentrecognitionchatapplication.adapter.RestarauntsAdapter;
import edu.sjsu.intentrecognitionchatapplication.data.Restaurant;
import edu.sjsu.intentrecognitionchatapplication.utils.YelpService;
import okhttp3.Call;
import okhttp3.Response;

public class RestarauntActivity extends AppCompatActivity {
    private ListView restarauntsView;
    public ArrayList<Restaurant> restaurants = new ArrayList<>();
    private static final String TAG = "RestarunatActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        restarauntsView=(ListView) findViewById(R.id.restarauntView);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaraunt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getRestaurants("Biryani","SanJose");
    }

    //testing purpose
    private void getRestaurants (String term,String location) {
        final YelpService yelpService = new YelpService();
        yelpService.findRestaraunts(term,location, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response){
                try {
                    String jsonData = response.body().string();

                    Log.v(TAG, jsonData);
                    restaurants = yelpService.processResults(jsonData);
                    RestarauntActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            restarauntsView=(ListView) findViewById(R.id.restarauntView);
                            RestarauntsAdapter restadapter = new RestarauntsAdapter(getApplicationContext(),restaurants);
                            restarauntsView.setAdapter(restadapter);
                            System.out.println(restaurants.size());
                            for (Restaurant restaurant : restaurants) {
                                Log.d(TAG, "Name: " + restaurant.getName());
                                Log.d(TAG, "Phone: " + restaurant.getPhone());
                                Log.d(TAG, "Website: " + restaurant.getWebsite());
                                Log.d(TAG, "Image url: " + restaurant.getImageUrl());
                                Log.d(TAG,"rating"+restaurant.getRating());
                                Log.d(TAG,"price"+restaurant.getPrice());
                                Log.d(TAG, "Address: " + android.text.TextUtils.join(", ", restaurant.getAddress()));
                            }
                        }
                    });

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
