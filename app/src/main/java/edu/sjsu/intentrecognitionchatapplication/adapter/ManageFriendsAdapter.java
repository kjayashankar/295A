package edu.sjsu.intentrecognitionchatapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import edu.sjsu.intentrecognitionchatapplication.R;
import edu.sjsu.intentrecognitionchatapplication.TalkToFriendActivity;
import edu.sjsu.intentrecognitionchatapplication.data.Friend;

/**
 * Created by jay on 9/11/17.
 */

public class ManageFriendsAdapter extends ArrayAdapter<Friend> {

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
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Friend rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.friend_individual, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.friend_name);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.chat = (ImageButton) convertView.findViewById(R.id.showChat);
        holder.chat.setBackgroundResource(0);

        final String name = rowItem.getName();
        holder.name.setText(name);
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

                   // new FriendOperation("accept",myMap).execute();
                }
            });

        }


        holder.level = (ImageButton) convertView.findViewById(R.id.level);
        holder.level.setBackgroundResource(android.R.drawable.ic_menu_delete);

        holder.level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG,"cancelling friend");
                //new FriendOperation("cancel",myMap).execute();
            }
        });
        return convertView;
    }
}
