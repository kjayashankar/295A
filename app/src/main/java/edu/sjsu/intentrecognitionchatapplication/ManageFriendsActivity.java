package edu.sjsu.intentrecognitionchatapplication;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.sjsu.intentrecognitionchatapplication.adapter.ManageFriendsAdapter;
import edu.sjsu.intentrecognitionchatapplication.data.Friend;

public class ManageFriendsActivity extends AppCompatActivity {

    private static String END_POINT_URL="http://10.0.0.98:8080/IntentChatServer/service/friendsPage/";
    List<Friend> friends;
    String method = "";
    private static String myName = "Jayashankar+Karnam";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friends);
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

        friendVanilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method = "friendVanilla";
                friendVanilla.setBackgroundColor(getResources().getColor(R.color.fillhighlight));
                friendRequests.setBackgroundColor(getResources().getColor(R.color.fill));
                friendConfirmations.setBackgroundColor(getResources().getColor(R.color.fill));
                populateList("friends");
                inflateUI();
                //new SearchFriends("friend").execute();
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
                inflateUI();
                //new SearchFriends("requests").execute();
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
                inflateUI();
                //new SearchFriends("requests").execute();
            }
        });
    }

    private void inflateUI() {
        ManageFriendsAdapter adapter = null;
        ListView listView = (ListView) findViewById(R.id.friendsView);
        if(friends != null && friends.size() > 0) {
            if (method.length() > 0) {
                //adapter = new ManageFriendsAdapter(myName, this, R.layout.friend_individual, friends, method);
                adapter = new ManageFriendsAdapter(this, R.layout.friend_individual, friends);


            } else {

            }
        }

        listView.setAdapter(adapter);
    }
    private void populateList(String category) {
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
                                friends.add(new Friend(obj.getString("name")));
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
                                friends.add(new Friend(obj.getString("name")));
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
                                friends.add(new Friend(obj.getString("name")));
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
