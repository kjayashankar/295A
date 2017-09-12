package edu.sjsu.intentrecognitionchatapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.sjsu.intentrecognitionchatapplication.R;
import edu.sjsu.intentrecognitionchatapplication.data.Friend;

/**
 * Created by jay on 9/11/17.
 */

public class ManageFriendsAdapter extends ArrayAdapter<Friend> {

    private Context context;

    public ManageFriendsAdapter(Context context, int resourceId,
                                List<Friend> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView name;
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

        holder.name.setText(rowItem.getName());

        return convertView;
    }
}
