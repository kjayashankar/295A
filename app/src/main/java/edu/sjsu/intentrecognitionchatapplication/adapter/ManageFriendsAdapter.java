package edu.sjsu.intentrecognitionchatapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.sjsu.intentrecognitionchatapplication.R;
import edu.sjsu.intentrecognitionchatapplication.SessionManager;
import edu.sjsu.intentrecognitionchatapplication.TalkToFriendActivity;
import edu.sjsu.intentrecognitionchatapplication.data.Friend;
import edu.sjsu.intentrecognitionchatapplication.utils.Constants;

/**
 * Created by jay on 9/11/17.
 */

public class ManageFriendsAdapter extends ArrayAdapter<Friend> {

    private static final String END_POINT_URL = Constants.HOST_BASE_URL+"/IntentChatServer/service/friendsPage/";

    private static final String TAG = "ManageFriendsAdapter";
    private Context context;

    private String option;
    private String myName;

    public ManageFriendsAdapter(Context context, int resourceId,
                                List<Friend> items, String myName, String option) {
        super(context, resourceId, items);
        this.myName = myName;
        this.option = option;
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView name;
        ImageButton chat;
        ImageButton level;
        CircleImageView image;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Friend rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.friend_individual, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.friend_name);
            holder.image = (CircleImageView) convertView.findViewById(R.id.friend_dp);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.chat = (ImageButton) convertView.findViewById(R.id.showChat);
        holder.chat.setBackgroundResource(0);

        final String name = rowItem.getName();
        holder.name.setText(name);
        String imageURL = rowItem.getPic();
        if(imageURL != null && imageURL.length() != 0 && !imageURL.equalsIgnoreCase("NULL")) {
            Uri uri = Uri.parse(imageURL);
            /*final CircleImageView view = holder.image;
            new DownloadDisplayPicture(view,uri).execute();*/
            if( AccessToken.getCurrentAccessToken() == null)
                Glide.with(context).load(uri).into(holder.image);
            else
                Glide.with(context).load(uri).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.image);

            //holder.image.setImageURI(uri);
        }
        else{
            holder.image.setBackgroundResource(R.drawable.download);
        }

        if ("friends".equalsIgnoreCase(option)) {

            holder.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("CHAT","Clicked chat");
                    Intent i = new Intent(context,TalkToFriendActivity.class);
                    i.putExtra("friend",name);
                    i.putExtra("friendName",name);
                    context.startActivity(i);
                }
            });
        }
        if ("requests".equalsIgnoreCase(option)) {
            holder.chat = (ImageButton) convertView.findViewById(R.id.showChat);
            holder.chat.setBackgroundResource(R.drawable.tick);
            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG,"username "+myName + " : "+"friend_username " +name);
                    ManageFriendsAdapter.this.remove(getItem(position));
                    ManageFriendsAdapter.this.notifyDataSetChanged();
                    new FriendOperation(END_POINT_URL+"accept/"+myName+"/"+name).execute();
                }
            });

        }

        if (!"search".equalsIgnoreCase(option)) {
            holder.level = (ImageButton) convertView.findViewById(R.id.level);
            holder.level.setBackgroundResource(android.R.drawable.ic_menu_delete);

            holder.level.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v(TAG, "cancelling friend");
                    ManageFriendsAdapter.this.remove(getItem(position));
                    ManageFriendsAdapter.this.notifyDataSetChanged();
                    new FriendOperation(END_POINT_URL + "deleteRequest/" + myName + "/" + name).execute();
                }
            });
        }
        else {
            holder.level = (ImageButton) convertView.findViewById(R.id.level);
            holder.level.setBackgroundResource(R.drawable.plus);

            holder.level.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v(TAG, "add friend");
                    ManageFriendsAdapter.this.remove(getItem(position));
                    ManageFriendsAdapter.this.notifyDataSetChanged();
                    new FriendOperation(END_POINT_URL + "sendRequest/" + myName + "/" + name).execute();
                }
            });
        }
        return convertView;
    }

    class DownloadDisplayPicture extends AsyncTask<Void,Void,Void>{

        private CircleImageView image;
        private Uri uri;
        Bitmap bitmap;

        public DownloadDisplayPicture(CircleImageView image, Uri uri) {
            this.image = image;
            this.uri = uri;
        }
        @Override
        protected Void doInBackground(Void... voids) {


            InputStream image_stream = null;
            try {
                image_stream = context.getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            bitmap= BitmapFactory.decodeStream(image_stream );


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (bitmap != null)
                image.setImageBitmap(bitmap);
        }
    }
}

class FriendOperation extends AsyncTask<Void,Map,Void>{

    private String TAG = "FriendOperationTAG";
    String endPoint;


    FriendOperation(String endPoint) {
        this.endPoint = endPoint;

    }
    @Override
    protected Void doInBackground(Void... voids) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(endPoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            Log.v(TAG,result.toString());
        }
        catch(Exception e){
            Log.e(TAG,"http response",e.fillInStackTrace());
        }
        return null;
    }


}