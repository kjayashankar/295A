package edu.sjsu.intentrecognitionchatapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.security.auth.callback.Callback;

import edu.sjsu.intentrecognitionchatapplication.adapter.ManageChatFriendsAdapter;
import edu.sjsu.intentrecognitionchatapplication.adapter.RestarauntsAdapter;
import edu.sjsu.intentrecognitionchatapplication.data.ChatMessage;
import edu.sjsu.intentrecognitionchatapplication.data.Friend;
import edu.sjsu.intentrecognitionchatapplication.data.Restaurant;
import edu.sjsu.intentrecognitionchatapplication.utils.Constants;
import edu.sjsu.intentrecognitionchatapplication.utils.YelpService;
import edu.sjsu.intentrecognitionchatapplication.websockets.WebSocketClient;
import okhttp3.Call;
import okhttp3.Response;

public class ChatFriendsActivity extends AppCompatActivity {

    private List<Friend> mFriendList = null;
    private ListView listActiveFriends;

    private static String myName = "medarametla sreekar";
    private static String email = "";
    private static String picURL = "";
    //private String myName = "Chat Friend2";

    private WebSocketClient client = null;
    private static final String TAG = "ChatFriendsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_friends);
        listActiveFriends = (ListView) findViewById(R.id.listView);
        listActiveFriends.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
        //test

        HashMap<String, String> user = SessionManager.getSession(getApplicationContext()).getUserDetails();
        //myName = user.get(SessionManager.NAME);
        email = user.get(SessionManager.EMAIL);
        picURL = user.get(SessionManager.PHOTO_URL);

        /*Intent intent = new Intent(getApplicationContext(), RestarauntActivity.class);
        startActivity(intent);*/

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
        if (client == null)
            setNotificationSocket();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null) {
            client.disconnect();
        }
    }

    private void setNotificationSocket(){
        String path = Constants.HOST_BASE_URL+"/IntentChatServer/notification?from="+myName.replaceAll(" ","\\+");

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
                                String picURL = "";
                                if (object.has("picURL")) {
                                    picURL = object.getString("picURL");
                                }
                                mFriendList.add(new Friend(object.getString("name"), object.getString("value"),
                                        picURL));
                            } catch (Exception e) {
                                Log.e("ERR",e.getMessage(),e.fillInStackTrace());
                            }
                        }
                        ManageChatFriendsAdapter adapter = new ManageChatFriendsAdapter(getApplicationContext(), R.layout.chat_list_item, mFriendList, "");
                        listActiveFriends.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        //listActiveFriends.setOnItemClickListener(ChatFriendsActivity.this);

                    }
                });
            }
            if (message.startsWith("2")) {
                // notification
                String message2 = message.substring(1);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext());
               Intent i = new Intent(getApplicationContext(), RestarauntActivity.class);
               /* i.putExtra("friend", message2.split(";")[1]);
                i.putExtra("friendName",message2.split(";")[1]);
                getApplicationContext().startActivity(i);*/

                //PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, null , 0);

                //mBuilder.setContentIntent(pendingIntent);
                mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
                mBuilder.setAutoCancel(true);
                mBuilder.setSmallIcon(R.drawable.classify);
                mBuilder.setContentTitle("Message notification");
                mBuilder.setContentText("Touch to get nearby options for "+message2.split(";")[0]);
                mBuilder.setDefaults(Notification.DEFAULT_SOUND);
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

                //getApplicationContext().startActivity(i);

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);


                getApplicationContext().startActivity(i);

                mBuilder.setContentIntent(pendingIntent);
                mBuilder.setAutoCancel(true);
                mBuilder.setDefaults(Notification.DEFAULT_SOUND);
                mBuilder.setSmallIcon(R.drawable.classify);
                mBuilder.setContentTitle("Message notification");
                mBuilder.setContentText("You have a message from "+friend);
                mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(001, mBuilder.build());
            }
            else if(message.startsWith("4")) {
                String message2 = message.substring(1);
                String friend = message2.split(";")[1];
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext());
                Intent i = new Intent(getApplicationContext(), TalkToFriendActivity.class);
                i.putExtra("friend", friend);
                i.putExtra("friendName",friend);
                //getApplicationContext().startActivity(i);

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

                mBuilder.setContentIntent(pendingIntent);
                mBuilder.setAutoCancel(true);
                mBuilder.setDefaults(Notification.DEFAULT_SOUND);
                mBuilder.setSmallIcon(R.drawable.classify);
                mBuilder.setContentTitle("Message notification");
                mBuilder.setContentText("You received a picture from "+friend);
                mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
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
            Log.e(TAG, "Error! : " + error.getMessage());
            Log.e(TAG,"err",error.fillInStackTrace());

            // showToast("Error! : " + error);
        }

    }, null);
        client.connect();
    }



}


