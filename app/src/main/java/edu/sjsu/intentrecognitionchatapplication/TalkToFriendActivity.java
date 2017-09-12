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
import android.widget.TextView;

import org.json.JSONArray;

import java.io.InputStream;

public class TalkToFriendActivity extends AppCompatActivity {

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
    JSONArray responseFetch = null;
    ListView listActiveFriends = null;
    //List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
    TextView friendText;
    ImageButton imageButton;
    Thread mThread;
    Button capturePicture = null;
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
        myName = "Jayashankar Karnam";
        //friend = "friend";
        friendText = (TextView)findViewById(R.id.Friend);
        if(friendName == null || friendName.length() == 0)
            friendName = "Friend Surname";
        friendText.setText(friendName);

        context = this;
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
        listActiveFriends = (ListView) findViewById(R.id.chat_listView);

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
                listActiveFriends.setAdapter(null);
                Log.d("TAG","on delete");
                //RetrieveChatData chatDel = new RetrieveChatData(myName, friend, "DEL");
                //chatDel.execute();
            }
        });

        //getInfo(friend);
        //FetchChat fetchChat = new FetchChat(friend);
        //mThread = new Thread(fetchChat);
        //mThread.start();

        chatText = (EditText) findViewById(R.id.chatText);
        final Button sendChat = (Button) findViewById(R.id.sendChat);
        sendChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("TAG","On click send chat");
                //sendChat();
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

   /* private void sendChat() {

        RetrieveChatData chatpush = new RetrieveChatData(myName, friend, "POST");

        String[] params = new String[2];
        params[0] = (chatText.getText().length() > 0 ) ? "text" : "image";
        params[1] = chatText.getText().toString()+"";
        chatText.setText("");
        if(capturedImage != null)
            params[1] = ImageUtils.getStringImage(capturedImage);
        capturedImage = null;
        capturePicture.setBackgroundResource(R.drawable.click);
        chatpush.execute(params);
    }*/
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
}
