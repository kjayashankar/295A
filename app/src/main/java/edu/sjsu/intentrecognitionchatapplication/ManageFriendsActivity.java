package edu.sjsu.intentrecognitionchatapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.sjsu.intentrecognitionchatapplication.adapter.ManageFriendsAdapter;
import edu.sjsu.intentrecognitionchatapplication.data.Friend;

public class ManageFriendsActivity extends AppCompatActivity {

    List<Friend> friends;
    String method = "";

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

        initButtons();


    }
    private void initButtons() {

        friends = new ArrayList<Friend>();
        final TextView friendVanilla = (TextView) findViewById(R.id.friendVanilla);
        final TextView friendRequests = (TextView) findViewById(R.id.friendRequests);

        friendVanilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method = "friendVanilla";
                friendVanilla.setBackgroundColor(getResources().getColor(R.color.fillhighlight));
                friendRequests.setBackgroundColor(getResources().getColor(R.color.fill));

                populateList("friend");
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

                populateList("requests");
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
            case "requests":
                friends = new ArrayList<Friend>();
                friends.add(new Friend("Request1"));
                friends.add(new Friend("Request2"));
                friends.add(new Friend("Request3"));
                friends.add(new Friend("Request4"));
                break;
            case "friend":
                friends = new ArrayList<Friend>();
                friends.add(new Friend("Friend1"));
                friends.add(new Friend("Friend2"));
                friends.add(new Friend("Friend3"));
                friends.add(new Friend("Friend4"));
                break;
            default:
                break;
        }
    }
}
