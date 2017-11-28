package edu.sjsu.intentrecognitionchatapplication.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SyncStatusObserver;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.sjsu.intentrecognitionchatapplication.R;
import edu.sjsu.intentrecognitionchatapplication.data.ChatMessage;
import edu.sjsu.intentrecognitionchatapplication.data.Friend;
import edu.sjsu.intentrecognitionchatapplication.websockets.ClassificationFragment;
import edu.sjsu.intentrecognitionchatapplication.websockets.ClassifyDialogFragment;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by jay on 11/7/17.
 */

public class ManageChatMessages extends ArrayAdapter<ChatMessage> implements ClassifyDialogFragment.Listener,ClassificationFragment.ClassifyListener{

    Bitmap friendDP = null;
    Bitmap myDP = null;
    final RelativeLayout chatClick;
    final ImageView expandedImageView;
    private HashMap<String,Bitmap> imageCache = new HashMap<String,Bitmap>();
    Context context;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private volatile List<ChatMessage> items;
    boolean flag=false;
    private String selectedMessage=null;
    private static String END_POINT_URL="http://10.0.0.25:8080/IntentChatServer/service/friendsPage/";

    public ManageChatMessages(Bitmap friendDP,Bitmap myDP, Context context, int resourceId,
                                    List<ChatMessage> items,View chatClick,ImageView expandedImageView) {
        super(context, resourceId, items);
        this.items = items;
        this.friendDP = friendDP;
        this.myDP = myDP;
        this.context = context;
        this.chatClick = (RelativeLayout) chatClick;
        this.expandedImageView = expandedImageView;
    }
    //classify mode
    public ManageChatMessages(Bitmap friendDP,Bitmap myDP, Activity context, int resourceId,
                              List<ChatMessage> items,View chatClick,ImageView expandedImageView,boolean flag) {
        super(context, resourceId, items);
        this.items = items;
        this.friendDP = friendDP;
        this.myDP = myDP;
        this.context = context;
        this.chatClick = (RelativeLayout) chatClick;
        this.expandedImageView = expandedImageView;
        this.flag=true;
    }


    private class ViewHolder {
        ImageView displayPicture;
        ImageView chatPicture;
        ImageView chatPictureLeft;
        TextView chatText;
        TextView chatTextLeft;
        Button selectMessage;

    }



    public View getView(final int position, View convertView, ViewGroup parent) {
       if(flag){
           ViewHolder holder = null;
           //FragmentActivity activity = (FragmentActivity)(context);
          final FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
           final ChatMessage rowItem = getItem(position);
           //z);
           LayoutInflater mInflater = (LayoutInflater) context
                   .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
           if (convertView == null) {
               convertView = mInflater.inflate(R.layout.chat_message, null);
              holder = new ViewHolder();
               holder.chatText = (TextView) convertView.findViewById(R.id.chat_text);
               holder.displayPicture = (ImageView) convertView.findViewById(R.id.icon);
               holder.chatTextLeft = (TextView) convertView.findViewById(R.id.chat_text_left);
               holder.chatPictureLeft = (ImageView) convertView.findViewById(R.id.chat_image_left);
               holder.chatPicture = (ImageView) convertView.findViewById(R.id.chat_image);
               holder.selectMessage=(Button)  convertView.findViewById(R.id.select);
               holder.selectMessage.setVisibility(View.VISIBLE);
               convertView.setTag(holder);
           } else
               holder = (ViewHolder) convertView.getTag();
           if (rowItem != null) {
               final Bitmap chatPicturersid = rowItem.getChatImage();

               if (rowItem.getUser().equalsIgnoreCase("ME")) {
                   RelativeLayout.LayoutParams paramsImage = (RelativeLayout.LayoutParams) holder.displayPicture.getLayoutParams();
                   paramsImage.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                   holder.displayPicture.setLayoutParams(paramsImage);
                   holder.displayPicture.setImageBitmap(myDP);
                   holder.chatPictureLeft.setImageBitmap(rowItem.getChatImage());
                   holder.chatTextLeft.setText(rowItem.getChatText());
                   holder.chatPictureLeft.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           zoomImageFromThumb(v, chatPicturersid, expandedImageView, chatClick);
                       }
                   });

                   holder.selectMessage.setOnClickListener(new View.OnClickListener() {
                       public void onClick(View view) {
                           ClassifyDialogFragment alertedFragment = new ClassifyDialogFragment();
                           // Show Alert DialogFragment
                           alertedFragment.setListener(ManageChatMessages.this);
                           alertedFragment.show(fm, "Alert Dialog Fragment");
                           // Show Alert DialogFragment


                          // selectedMessage=((TextView) view).getText().toString();
                           setSelectedMessage(rowItem.getChatText());
                           Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                                   Toast.LENGTH_SHORT).show();

                       }
                   });
               } else {
                   holder.chatPicture.setImageBitmap(rowItem.getChatImage());
                   holder.chatText.setText(rowItem.getChatText());
                   //selectedMessage=rowItem.getChatText();
                   holder.displayPicture.setImageBitmap(friendDP);
                   holder.chatPicture.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           zoomImageFromThumb(v, chatPicturersid, expandedImageView, chatClick);
                       }
                   });
               }
           }
           return convertView;


       } else {
           ViewHolder holder = null;
           ChatMessage rowItem = getItem(position);
           //Log.d("TAG", String.format("row Item is! %s", rowItem.getChatText()));
           LayoutInflater mInflater = (LayoutInflater) context
                   .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
           if (convertView == null) {
               convertView = mInflater.inflate(R.layout.chat_message, null);
               holder = new ViewHolder();
               holder.chatText = (TextView) convertView.findViewById(R.id.chat_text);
               holder.displayPicture = (ImageView) convertView.findViewById(R.id.icon);
               holder.chatTextLeft = (TextView) convertView.findViewById(R.id.chat_text_left);
               holder.chatPictureLeft = (ImageView) convertView.findViewById(R.id.chat_image_left);
               holder.chatPicture = (ImageView) convertView.findViewById(R.id.chat_image);
               convertView.setTag(holder);
               holder.selectMessage=(Button)  convertView.findViewById(R.id.select);
               holder.selectMessage.setVisibility(View.GONE);
           } else
               holder = (ViewHolder) convertView.getTag();
           if (rowItem != null) {
               final Bitmap chatPicturersid = rowItem.getChatImage();

               if (rowItem.getUser().equalsIgnoreCase("ME")) {
                   RelativeLayout.LayoutParams paramsImage = (RelativeLayout.LayoutParams) holder.displayPicture.getLayoutParams();
                   paramsImage.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                   holder.displayPicture.setLayoutParams(paramsImage);
                   holder.displayPicture.setImageBitmap(myDP);
                   holder.chatPictureLeft.setImageBitmap(rowItem.getChatImage());
                   holder.chatTextLeft.setText(rowItem.getChatText());
                   holder.chatPictureLeft.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           zoomImageFromThumb(v, chatPicturersid, expandedImageView, chatClick);
                       }
                   });
               } else {
                   holder.chatPicture.setImageBitmap(rowItem.getChatImage());
                   holder.chatText.setText(rowItem.getChatText());
                   holder.displayPicture.setImageBitmap(friendDP);
                   holder.chatPicture.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           zoomImageFromThumb(v, chatPicturersid, expandedImageView, chatClick);
                       }
                   });
               }
           }
           return convertView;
       }
    }
    @Override
    public void onOkay(boolean result) {
        if(result){
            Log.d("TAG", String.format("result of selected item is %s", selectedMessage));
            final FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
            ClassificationFragment classifyFragment = new ClassificationFragment();
            // Show Alert DialogFragment
            classifyFragment.setListener(ManageChatMessages.this);
            classifyFragment.show(fm, "Alert Dialog Fragment");
        }

    }
    @Override
    public void onCancel(boolean result) {
    }

    public void setSelectedMessage(String message){
        selectedMessage=message;
    }

    @Override
    public  void onEat(boolean result) {
        System.out.println("eat selected " + " for" + selectedMessage);
        // /updateCorpus/{classification}/{sentence}
        if (result) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try  {
                        //Your code goes here
                        StringBuilder value = new StringBuilder();
                        try {
                            URL url = new URL(END_POINT_URL + "updateCorpus/eat/" + selectedMessage);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String line;
                            while ((line = rd.readLine()) != null) {
                                value.append(line);
                            }
                            rd.close();
                        } catch (Exception e) {
                            Log.e("Classification Message", "fetch downstream data", e.fillInStackTrace());
                        }
                        Log.v("ClassificationYeMessage", value.toString());


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        }
    }

    @Override
    public  void onNotEat(boolean result) {
        System.out.println("no eat selected " + " for" + selectedMessage);
        if (result) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    StringBuilder value = new StringBuilder();
                    try {
                        URL url = new URL(END_POINT_URL + "updateCorpus/noteat/" + selectedMessage);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line;
                        while ((line = rd.readLine()) != null) {
                            value.append(line);
                        }
                        rd.close();
                    } catch (Exception e) {
                        Log.e("Classification Message", "fetch downstream data", e.fillInStackTrace());
                    }
                    Log.v("ClassificationNoMessage", value.toString());
                }
            });
        }
    }
    @Override
    public int getViewTypeCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return IGNORE_ITEM_VIEW_TYPE;
        // /return position;
    }

    private void zoomImageFromThumb(final View thumbView, Bitmap imageResId,final ImageView expandedImageView,final RelativeLayout chatClick) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.

        expandedImageView.setImageBitmap(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);

        chatClick.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}