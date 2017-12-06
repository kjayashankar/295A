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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.sjsu.intentrecognitionchatapplication.R;
import edu.sjsu.intentrecognitionchatapplication.TalkToFriendActivity;
import edu.sjsu.intentrecognitionchatapplication.data.Friend;

/**
 * Created by jay on 9/11/17.
 */

public class ManageChatFriendsAdapter extends ArrayAdapter<Friend> {

    private Context context;
    private String myName;

    public ManageChatFriendsAdapter(Context context, int resourceId,
                                List<Friend> items, String myName) {
        super(context, resourceId, items);
        this.context = context;
        this.myName = myName;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView name;
        CircleImageView image;
        //LinearLayout layout;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
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

            //LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.friend_layout);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        if (!rowItem.isRead())
            //holder.layout.setBackgroundColor();
            holder.name.setBackgroundColor(context.getResources().getColor(R.color.lightgreen));
        holder.image.setBackgroundResource(R.drawable.download);

        String imageURL = rowItem.getPic();
        if(imageURL != null && imageURL.length() != 0 && !imageURL.equalsIgnoreCase("NULL")) {
            Uri uri = Uri.parse(imageURL);
            if( AccessToken.getCurrentAccessToken() == null)
                Glide.with(context).load(uri).into(holder.image);
            else
                Glide.with(context).load(uri).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.image);
            //holder.image.setImageURI(uri);
        }

        final String friendName = rowItem.getName();
        holder.name.setText(friendName);
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Onclick chat","myname : "+myName+"::"+friendName);

                Intent i = new Intent(context, TalkToFriendActivity.class);
                i.putExtra("friend", friendName);
                i.putExtra("friendName",friendName);
                context.startActivity(i);
            }
        });
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Onclick chat","myname : "+myName+"::"+friendName);

                Intent i = new Intent(context, TalkToFriendActivity.class);
                i.putExtra("friend", friendName);
                i.putExtra("friendName",friendName);
                context.startActivity(i);
            }
        });


        return convertView;
    }
}
