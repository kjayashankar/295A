package edu.sjsu.intentrecognitionchatapplication.websockets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class ClassificationFragment extends DialogFragment {
    private ClassifyListener mListener;

    public void setListener(ClassifyListener listener) {
        mListener = listener;
    }

    public static interface ClassifyListener {
        void onEat(boolean result);
        void onNotEat(boolean result);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                // Set Dialog Icon
                //.setIcon(R.drawable.androidhappy)
                // Set Dialog Title
                .setTitle("Classify to Eat or Not Eat ")
                // Set Dialog Message
                .setMessage("Classification")
                // Positive button
                .setPositiveButton("Eat", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(mListener!=null) {
                            mListener.onEat(true);
                        }

                    }
                })

                // Negative Button
                .setNegativeButton("NotEat", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,	int which) {
                        // Do something else
                        if(mListener!=null) {
                            mListener.onNotEat(true);
                        }
                    }
                }).create();
    }

}
