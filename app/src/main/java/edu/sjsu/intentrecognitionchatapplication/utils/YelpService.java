package edu.sjsu.intentrecognitionchatapplication.utils;

/**
 * Created by admin on 11/29/2017.
 */
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import edu.sjsu.intentrecognitionchatapplication.data.Restaurant;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Call;
import okhttp3.Response;


public class YelpService {
    public static void findRestaraunts(String term,String location,Callback callback){
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.YELP_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(Constants.YELP_LOCATION_QUERY_PARAMETER, "SanJose");
        urlBuilder.addQueryParameter(Constants.YELP_LIMIT,"5");
        //urlBuilder.addQueryParameter("limit","20");
        String url = urlBuilder.build().toString();
        System.out.println("-------------->url is"+url);
        Request request= new Request.Builder()
                .url(url)
                .header("Authorization", Constants.YELP_TOKEN)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public ArrayList<Restaurant> processResults(String jsonData) {

        ArrayList<Restaurant> restaurants = new ArrayList<>();


        try {
            Log.v("till here","reached here");
            Log.v("json data",jsonData);
            JSONObject yelpJSON = new JSONObject(jsonData);
            JSONArray businessesJSON = yelpJSON.getJSONArray("businesses");
            for (int i = 0; i < businessesJSON.length(); i++) {
                JSONObject restaurantJSON = businessesJSON.getJSONObject(i);
                String name = restaurantJSON.getString("name");
                String phone = restaurantJSON.optString("display_phone", "Phone not available");
                String website = restaurantJSON.getString("url");
                String imageUrl = restaurantJSON.getString("image_url");
                double latitude = (double) restaurantJSON.getJSONObject("coordinates").getDouble("latitude");
                double longitude = (double) restaurantJSON.getJSONObject("coordinates").getDouble("longitude");
                String rating=restaurantJSON.getString("rating");
                String price=restaurantJSON.getString("price");


                ArrayList<String> address = new ArrayList<>();
                JSONArray addressJSON = restaurantJSON.getJSONObject("location")
                        .getJSONArray("display_address");
                for (int y = 0; y < addressJSON.length(); y++) {
                    address.add(addressJSON.get(y).toString());
                }
                Restaurant restaurant = new Restaurant(name, phone, website,
                        imageUrl, address, latitude, longitude,rating,price);
                restaurants.add(restaurant);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return restaurants;
    }
}
