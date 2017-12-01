package edu.sjsu.intentrecognitionchatapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.sjsu.intentrecognitionchatapplication.adapter.ManageChatFriendsAdapter;
import edu.sjsu.intentrecognitionchatapplication.data.ChatMessage;
import edu.sjsu.intentrecognitionchatapplication.data.Friend;
import edu.sjsu.intentrecognitionchatapplication.utils.Constants;
import edu.sjsu.intentrecognitionchatapplication.websockets.WebSocketClient;

public class ChatFriendsActivity extends AppCompatActivity {

    private List<Friend> mFriendList = null;
    private ListView listActiveFriends;
    private String myName = "Jayashankar Karnam";
    private WebSocketClient client = null;
    private static final String TAG = "ChatFriendsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_friends);
        listActiveFriends = (ListView) findViewById(R.id.listView);
        listActiveFriends.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
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
        if(client==null) {
            setNotificationSocket();
        }
    }

    private void setNotificationSocket(){
        String path = "http://"+ Constants.HOST_NAME+":"+Constants.PORT+"/IntentChatServer/notification?from="+myName.replaceAll(" ","\\+");

        Log.d(TAG,path);
        client = new WebSocketClient(URI.create(path.replaceAll(" ","+")), new WebSocketClient.Listener() {
        @Override
        public void onConnect() {
            Log.d("TAG", "connection opened");
        }

        /**
         * On receiving the message from web socket server
         * */
        @Override
        public void onMessage(final String message) {
            Log.d("TAG", String.format("Got string message! %s", message));

            if (message.startsWith("1")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFriendList = new ArrayList<Friend>();

                        JSONArray array = null;
                        try {

                            array = new JSONArray(message.substring(1));
                        } catch (Exception e) {

                        }
                        if (array == null) {
                            listActiveFriends.setAdapter(null);
                            return;
                        }
                        int size = array.length();

                        while (size-- > 0) {
                            JSONObject object;
                            try {
                                object = array.getJSONObject(size);
                                mFriendList.add(new Friend(object.getString("name"), object.getString("value")));
                            } catch (Exception e) {
                            }
                        }
                        ManageChatFriendsAdapter adapter = new ManageChatFriendsAdapter(getApplicationContext(), R.layout.chat_list_item, mFriendList, "");
                        listActiveFriends.setAdapter(adapter);
                        //listActiveFriends.setOnItemClickListener(ChatFriendsActivity.this);

                    }
                });
            }
            if (message.startsWith("2")) {
                // notification
                String message2 = message.substring(1);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext());
               /* Intent i = new Intent(getApplicationContext(), TalkToFriendActivity.class);
                i.putExtra("friend", message2.split(";")[1]);
                i.putExtra("friendName",message2.split(";")[1]);
                getApplicationContext().startActivity(i);*/

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, null , 0);

                mBuilder.setContentIntent(pendingIntent);

                mBuilder.setSmallIcon(R.drawable.classify);
                mBuilder.setContentTitle("Message notification");
                mBuilder.setContentText("Touch to get nearby options for "+message2.split(";")[0]);

                NotificationManager mNotificationManager =

                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(001, mBuilder.build());

            }
            if(message.startsWith("3")) {
                String message2 = message.substring(1);
                String friend = message2.split(";")[1];
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext());
                Intent i = new Intent(getApplicationContext(), TalkToFriendActivity.class);
                i.putExtra("friend", friend);
                i.putExtra("friendName",friend);
                getApplicationContext().startActivity(i);

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);

                mBuilder.setContentIntent(pendingIntent);

                mBuilder.setSmallIcon(R.drawable.classify);
                mBuilder.setContentTitle("Message notification");
                mBuilder.setContentText("You have a message from "+friend);

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(001, mBuilder.build());
            }
        }

        @Override
        public void onMessage(byte[] data) {
            Log.d("TAG", String.format("Got binary message! %s"
            ));
        }

        /**
         * Called when the connection is terminated
         * */
        @Override
        public void onDisconnect(int code, String reason) {

            String message = String.format(Locale.US,
                    "Disconnected! Code: %d Reason: %s", code, reason);
        }

        @Override
        public void onError(Exception error) {
            Log.e("TAG", "Error! : " + error.getMessage());
            Log.e("TAG","err",error.fillInStackTrace());

            // showToast("Error! : " + error);
        }

    }, null);
        client.connect();
    }
}
