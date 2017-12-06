package edu.sjsu.intentrecognitionchatapplication;

import android.os.StrictMode;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.sjsu.intentrecognitionchatapplication.adapter.ManageFriendsAdapter;
import edu.sjsu.intentrecognitionchatapplication.data.Friend;
import edu.sjsu.intentrecognitionchatapplication.utils.Constants;

public class ManageFriendsActivity extends AppCompatActivity {

    private static String END_POINT_URL=Constants.HOST_BASE_URL+"/IntentChatServer/service/friendsPage/";
    List<Friend> friends;
    String method = "";
    private static String myName = "medarametla+sreekar";
    private static String email = "";
    private static String picURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friends);
        HashMap<String, String> user = SessionManager.getSession(getApplicationContext()).getUserDetails();
        myName = user.get(SessionManager.NAME);
        email = user.get(SessionManager.EMAIL);
        picURL = user.get(SessionManager.PHOTO_URL);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        initButtons();


    }
    private void initButtons() {

        friends = new ArrayList<Friend>();
        final TextView friendVanilla = (TextView) findViewById(R.id.friendVanilla);
        final TextView friendRequests = (TextView) findViewById(R.id.friendRequests);
        final TextView friendConfirmations = (TextView) findViewById(R.id.friendConfirmations);
        final ImageView searchButton = (ImageView) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String searchText = ((EditText) findViewById(R.id.searchText)).getText().toString();

                populateList("search",searchText.replaceAll(" ","\\+"));
                inflateUI("search");
            }
        });
        friendVanilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method = "friendVanilla";
                friendVanilla.setBackgroundColor(getResources().getColor(R.color.fillhighlight));
                friendRequests.setBackgroundColor(getResources().getColor(R.color.fill));
                friendConfirmations.setBackgroundColor(getResources().getColor(R.color.fill));
                populateList("friends");
                inflateUI("friends");
            }
        });
        friendRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method = "friendRequests";
                friendVanilla.setBackgroundColor(getResources().getColor(R.color.fill));
                friendRequests.setBackgroundColor(getResources().getColor(R.color.fillhighlight));
                friendConfirmations.setBackgroundColor(getResources().getColor(R.color.fill));

                populateList("requests");
                inflateUI("requests");
            }
        });
        friendConfirmations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method = "friendRequests";
                friendVanilla.setBackgroundColor(getResources().getColor(R.color.fill));
                friendRequests.setBackgroundColor(getResources().getColor(R.color.fill));
                friendConfirmations.setBackgroundColor(getResources().getColor(R.color.fillhighlight));

                populateList("confirmations");
                inflateUI("confirmations");
            }
        });
    }

    private void populateList(String category) {
        populateList(category, "");
    }
    private void inflateUI(String option) {
        ManageFriendsAdapter adapter = null;
        ListView listView = (ListView) findViewById(R.id.friendsView);
        if(friends != null && friends.size() > 0) {
            Log.d("InflateUI","option ,, " +friends.toString());

                adapter = new ManageFriendsAdapter(this, R.layout.friend_individual, friends, myName, option);

        }
        listView.setAdapter(adapter);
    }

    private void populateList(String category , final String searchText) {
        switch(category){
            case "friends":
                friends = new ArrayList<Friend>();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        friends = new ArrayList<Friend>();
                        JSONArray array = null;
                        StringBuilder result = new StringBuilder();
                        try {
                            URL url = new URL(END_POINT_URL + "friends/"+myName);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String line;
                            while ((line = rd.readLine()) != null) {
                                result.append(line);
                            }
                            rd.close();
                            array = new JSONArray(result.toString());
                            int size = array.length();
                            int i = 0;
                            while(i < size ) {
                                JSONObject obj = array.getJSONObject(i);
                                String picURL = "";
                                if (obj.has("picURL")) {
                                    picURL = obj.getString("picURL");
                                }

                                friends.add(new Friend(obj.getString("name"),"false",
                                        picURL));
                                i++;
                            }
                        }
                        catch(Exception e){
                            Log.e("ManageFriends","fetch downstream data",e.fillInStackTrace());
                        }
                        Log.v("ManageFriends",result.toString());
                    }
                });
                break;
            case "requests":
                friends = new ArrayList<Friend>();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        friends = new ArrayList<Friend>();
                        JSONArray array = null;
                        StringBuilder result = new StringBuilder();
                        try {
                            URL url = new URL(END_POINT_URL + "requests/"+myName);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String line;
                            while ((line = rd.readLine()) != null) {
                                result.append(line);
                            }
                            rd.close();
                            array = new JSONArray(result.toString());
                            int size = array.length();
                            int i = 0;
                            while(i < size ) {
                                JSONObject obj = array.getJSONObject(i);
                                friends.add(new Friend(obj.getString("name"),"false",
                                        obj.getString("picURL")));
                                i++;
                            }
                        }
                        catch(Exception e){
                            Log.e("ManageFriends","fetch downstream data",e.fillInStackTrace());
                        }
                        Log.v("ManageFriends",result.toString());
                    }
                });
                break;
            case "confirmations":
                friends = new ArrayList<Friend>();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        friends = new ArrayList<Friend>();
                        JSONArray array = null;
                        StringBuilder result = new StringBuilder();
                        try {
                            URL url = new URL(END_POINT_URL + "confirmations/"+myName);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String line;
                            while ((line = rd.readLine()) != null) {
                                result.append(line);
                            }
                            rd.close();
                            array = new JSONArray(result.toString());
                            int size = array.length();
                            int i = 0;
                            while(i < size ) {
                                JSONObject obj = array.getJSONObject(i);
                                friends.add(new Friend(obj.getString("name"),"false",
                                        obj.getString("picURL")));
                                i++;
                            }
                        }
                        catch(Exception e){
                            Log.e("ManageFriends","fetch downstream data",e.fillInStackTrace());
                        }
                        Log.v("ManageFriends",result.toString());
                    }
                });
                break;
            case "search":
                friends = new ArrayList<Friend>();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        friends = new ArrayList<Friend>();
                        JSONArray array = null;
                        StringBuilder result = new StringBuilder();
                        try {
                            URL url = new URL(END_POINT_URL + "search/"+searchText);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String line;
                            while ((line = rd.readLine()) != null) {
                                result.append(line);
                            }
                            rd.close();
                            array = new JSONArray(result.toString());
                            int size = array.length();
                            int i = 0;
                            while(i < size ) {
                                JSONObject obj = array.getJSONObject(i);
                                friends.add(new Friend(obj.getString("name"),"false",
                                        obj.getString("picURL")));
                                i++;
                            }
                        }
                        catch(Exception e){
                            Log.e("ManageFriends","fetch downstream data",e.fillInStackTrace());
                        }
                        Log.v("ManageFriends",result.toString());
                    }
                });
                break;
            default:
                break;
        }
    }
}


