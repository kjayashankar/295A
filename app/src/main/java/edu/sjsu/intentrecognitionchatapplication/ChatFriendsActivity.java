package edu.sjsu.intentrecognitionchatapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.sjsu.intentrecognitionchatapplication.adapter.ManageChatFriendsAdapter;
import edu.sjsu.intentrecognitionchatapplication.data.Friend;

public class ChatFriendsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<Friend> mFriendList = null;
    private ListView listActiveFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_friends);
        listActiveFriends = (ListView) findViewById(R.id.listView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView allFriends = (ImageView) findViewById(R.id.newChat);
        allFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(getApplicationContext(), AllFriendsChatActivity.class);
                //startActivity(i);
            }
        });
        getActiveFriends();
    }

    private void getActiveFriends(){
        mFriendList = new ArrayList<Friend>();

        mFriendList.add(new Friend("Chat Friend1"));
        mFriendList.add(new Friend("Chat Friend2"));
        mFriendList.add(new Friend("Chat Friend3"));
        mFriendList.add(new Friend("Chat Friend4"));
        mFriendList.add(new Friend("Chat Friend5"));

        ManageChatFriendsAdapter adapter = new ManageChatFriendsAdapter(this, R.layout.chat_list_item, mFriendList);
        listActiveFriends.setAdapter(adapter);
        listActiveFriends.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

        Intent i = new Intent(this, TalkToFriendActivity.class);
        i.putExtra("friend", "Jay Karnam");
        i.putExtra("fullname",mFriendList.get(pos).getName());
        startActivity(i);
    }
}
