package edu.sjsu.intentrecognitionchatapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

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
        //LinearLayout layout;
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
            //LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.friend_layout);
            if (!rowItem.isRead())
                //holder.layout.setBackgroundColor();
                holder.name.setBackgroundColor(context.getResources().getColor(R.color.lightgreen));
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

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


        return convertView;
    }
}
