package edu.sjsu.intentrecognitionchatapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.sjsu.intentrecognitionchatapplication.adapter.ManageChatMessages;
import edu.sjsu.intentrecognitionchatapplication.data.ChatMessage;
import edu.sjsu.intentrecognitionchatapplication.utils.JSONUtils;
import edu.sjsu.intentrecognitionchatapplication.websockets.WebSocketClient;

public class TalkToFriendActivity extends AppCompatActivity {

    private static final String TAG = "Conversation";
    private static final int CAMERA_REQUEST_INTENT = 1016;
    private static final int SELECT_PHOTO = 1017;
    private Bitmap capturedImage = null;
    Bitmap friendDP = null;
    Bitmap myDP = null;
    String myName = "";
    String friendName = "";
    private Context context= null;
    private EditText chatText = null;
    String friend = "";
    ListView listChatMessagesView = null;
    List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
    TextView friendText;
    ImageButton imageButton;
    Thread mThread;
    Button capturePicture = null;
    WebSocketClient client = null;
    private boolean isInitialized = false;
    ManageChatMessages adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_to_friend);

        if(getIntent() != null){
            friend = getIntent().getStringExtra("friend");
            friendName = getIntent().getStringExtra("fullname");
            //getInfo(friend);
            //update the name on toolbar
        }
        // debug override natural behavior and test
        /*myName  = "Chat Friend2";
        friendName = "Jayashankar Karnam";*/
        //friendName  = "Chat Friend2";
        myName = "Jayashankar Karnam";
        //friend = "friend";
        friendText = (TextView)findViewById(R.id.Friend);
        if(friendName == null || friendName.length() == 0)
            friendName = "Friend Surname";
        friendText.setText(friendName);

        context = this;
        setWebSockets();

        onStart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mThread != null) {
                mThread.stop();
            }
        }
        catch(Exception e){
            Log.d("ERR","Failed to stop thread");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageButton = (ImageButton)findViewById(R.id.backButton);
        capturePicture = (Button) findViewById(R.id.clickPicture);
        listChatMessagesView = (ListView) findViewById(R.id.chat_listView);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(TalkToFriendActivity.this,ChatFriendsActivity.class);
                startActivity(back);
            }
        });
        Button del = (Button) findViewById(R.id.terminateChat);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listChatMessagesView.setAdapter(null);
                Log.d("TAG","on delete");

            }
        });

        chatText = (EditText) findViewById(R.id.chatText);
        final Button sendChat = (Button) findViewById(R.id.sendChat);
        sendChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("TAG","On click send chat");
                sendChat();
            }
        });
        capturePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(capturedImage == null ) {

                    final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    final LinearLayout layout = new LinearLayout(context);

                    ImageView camera = new ImageView(context);
                    camera.setImageResource(R.drawable.click);

                    camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_INTENT);
                            //alert.setCancelable(true);
                        }
                    });
                    ImageView gallery = new ImageView(context);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                    camera.setLayoutParams(params);
                    gallery.setLayoutParams(params);
                    gallery.setImageResource(R.drawable.photos);
                    gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                        }
                    });
                    layout.addView(camera);
                    layout.addView(gallery);
                    alert.setView(layout);
                    alert.show();
                }
                else {
                    capturedImage = null;
                    capturePicture.setBackgroundResource(R.drawable.click);
                }
            }
        });
    }

    private void setAdapter(){
        final RelativeLayout chatClick = (RelativeLayout) findViewById(R.id.chatWindow);
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);

        adapter = new ManageChatMessages(friendDP, myDP, this, R.layout.chat_message, chatMessages, chatClick, expandedImageView);
        listChatMessagesView.setAdapter(adapter);
    }

    private void sendChat() {

        String value = chatText.getText().toString();
        JSONObject serverObject = JSONUtils.exchangeForString(myName,friendName,value);
        if (client != null) {
            client.send(serverObject.toString());
            ChatMessage messageObject = new ChatMessage(null,value,"ME");
            chatMessages.add(messageObject);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isInitialized) {
                        adapter.notifyDataSetChanged();
                    } else {
                        setAdapter();
                    }
                    chatText.setText("");
                }
            });
        }

        /*RetrieveChatData chatpush = new RetrieveChatData(myName, friend, "POST");

        String[] params = new String[2];
        params[0] = (chatText.getText().length() > 0 ) ? "text" : "image";
        params[1] = chatText.getText().toString()+"";

        if(capturedImage != null)
            params[1] = ImageUtils.getStringImage(capturedImage);
        capturedImage = null;
        capturePicture.setBackgroundResource(R.drawable.click);
        chatpush.execute(params);*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_INTENT && resultCode == Activity.RESULT_OK) {
            capturedImage = (Bitmap) data.getExtras().get("data");
        }
        else if (requestCode == SELECT_PHOTO && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                capturedImage = BitmapFactory.decodeStream(imageStream);
            }
            catch(Exception e) {
                Log.d("GALLERY","Exception in gallery result intent");
            }
        }
        if(capturedImage != null) {
            capturePicture.setBackgroundResource(R.drawable.click3);
        }
    }

    private void setWebSockets() {
        String path = "http://10.0.0.98:8080/IntentChatServer/chat?from="+myName+"&to="+friendName;

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

                //Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isInitialized) {
                            parseMessage(message);
                            isInitialized = true;
                        }
                        else{
                            JSONObject object = getJSONObject(message);
                            String value = "";
                            try {
                                value = object.getString("value");
                                ChatMessage message = new ChatMessage(null,value,"HIM");
                                chatMessages.add(message);
                                adapter.notifyDataSetChanged();
                            }
                            catch (Exception e) {

                                Log.e(TAG,"error in updating incoming message",e.fillInStackTrace());
                            }
                        }
                    }
                });
            }

            @Override
            public void onMessage(byte[] data) {
                Log.d("TAG", String.format("Got binary message! %s"
                        ));

                // Message will be in JSON format
                //parseMessage(bytesToHex(data));
            }

            /**
             * Called when the connection is terminated
             * */
            @Override
            public void onDisconnect(int code, String reason) {

                String message = String.format(Locale.US,
                        "Disconnected! Code: %d Reason: %s", code, reason);

                //showToast(message);

                // clear the session id from shared preferences
                //utils.storeSessionId(null);
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

    private JSONObject getJSONObject(String data) {
        if ( data == null || data.length() == 0)
            return null;
        JSONObject json;
        try {
            json = new JSONObject(data);
        }
        catch(Exception e){
            Log.e(TAG,"json object cannot be created",e.fillInStackTrace());
            return null;
        }
        return json;
    }

    private void parseMessage(String serverData) {

        if (serverData == null || serverData.length() == 0)
            return;
        JSONArray array = null;
        //chatMessages = new ArrayList<ChatMessage>();
        try {
            array = new JSONArray(serverData);
            Log.d(TAG,array+"");

        }
        catch(JSONException e){
            Log.e(TAG,"json array not built",e.fillInStackTrace());
        }
            int size = array.length();
            for (int i = 0; i < size; i++) {
                ChatMessage item = null;
                try {
                    JSONObject object = array.getJSONObject(i);
                    String data = object.getString("value");
                    if (data != null && data.length() < 100) {
                        if (myName.equalsIgnoreCase(object.getString("sender"))) {
                            item = new ChatMessage(null, data, "ME");
                        } else {
                            item = new ChatMessage(null, data, "HIM");
                        }
                    } else {
                        /*Bitmap chatImage = ImageUtils.getBitmapFromBase64(data);
                        if (myName.equalsIgnoreCase(object.getString("sender"))) {
                            item = new ChatMessage(chatImage, "", "ME");
                        } else {
                            item = new ChatMessage(chatImage, "", "HIM");
                        }*/
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                chatMessages.add(item);
            }

            setAdapter();
            isInitialized = true;
    }
}
