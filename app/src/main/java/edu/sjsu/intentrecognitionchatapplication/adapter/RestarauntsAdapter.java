package edu.sjsu.intentrecognitionchatapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import edu.sjsu.intentrecognitionchatapplication.R;
import edu.sjsu.intentrecognitionchatapplication.data.ChatMessage;
import edu.sjsu.intentrecognitionchatapplication.data.Restaurant;

/**
 * Created by admin on 12/2/2017.
 */

public class RestarauntsAdapter extends ArrayAdapter<Restaurant> {

    Bitmap restaurantImage = null;
    private List<Restaurant> restaurants;
    private Context context;
    int count = 0;
//
//    @Override
//    public int getPosition(Restaurant item) {
//        return super.getPosition(item);
//    }
//
//    @Override
//    public int getCount() {
//        return restaurants.size();
//    }

    public RestarauntsAdapter(Context context, List<Restaurant> restaurants) {
        super(context, R.layout.content_restaraunt, restaurants);
        this.restaurants = restaurants;
        this.context = context;
    }

//    @Override
//    public int getItemViewType(int position) {
//        return IGNORE_ITEM_VIEW_TYPE;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return this.restaurants.size();
//    }

    private class ViewHolder {
        ImageView restaruantPicture;
        TextView restaruantName;
        TextView RestaruantPrice;
        TextView RestaruantRating;
        TextView Address;
        ImageView restaruantLogo;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        System.out.println("here");
        ViewHolder holder = null;
        final Restaurant rowItem = getItem(position);
        final View result;
//        LayoutInflater mInflater = (LayoutInflater) context
//                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.content_restaraunt, parent, false);
            holder.restaruantName = (TextView) convertView.findViewById(R.id.restaraunt_name);
            holder.restaruantPicture = (ImageView) convertView.findViewById(R.id.restaraunt_image);
            holder.RestaruantPrice= (TextView) convertView.findViewById(R.id.restarauntPrice);
            holder.RestaruantRating=(TextView) convertView.findViewById(R.id.restaraunt_rating);
            holder.restaruantLogo=(ImageView) convertView.findViewById(R.id.restaraunt_url);
           // holder.Address=(TextView) convertView.findViewById(R.id.restaraunt_address);
            result = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

            final String name = rowItem.getName();
            holder.restaruantName.setText(name);
            holder.RestaruantPrice.setText(rowItem.getPrice());
            holder.RestaruantRating.setText(rowItem.getRating());
            //holder.Address.setText(android.text.TextUtils.join(", ", rowItem.getAddress()));
            System.out.println(restaurants.size());
            new DownloadImageTask(holder.restaruantPicture).execute(rowItem.getImageUrl());
            holder.restaruantLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(rowItem.getWebsite()));
                     context.startActivity(browserIntent);

                }
            });


//        try {
//            url = new URL(rowItem.getImageUrl());
//        }catch (MalformedURLException e){
//            e.printStackTrace();
//        }
//
//        try {
//            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            holder.restaruantPicture.setImageBitmap(BitmapFactory.decodeFile(rowItem.getImageUrl()));
//        }catch (IOException e){
//            e.printStackTrace();
//        }
            System.out.println("view returned");
            return convertView;
        }

        private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
            ImageView bmImage;

            public DownloadImageTask(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            protected Bitmap doInBackground(String... urls) {
                String urldisplay = urls[0];
                Bitmap mIcon11 = null;
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                return mIcon11;
            }

            protected void onPostExecute(Bitmap result) {
                bmImage.setImageBitmap(result);
            }
        }
    }

