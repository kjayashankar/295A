package edu.sjsu.intentrecognitionchatapplication.websockets;

/**
 * Created by admin on 11/23/2017.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class ClassifyDialogFragment extends DialogFragment  {

    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }

   public static interface Listener {
        void onOkay(boolean result);
        void onCancel(boolean result);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                // Set Dialog Icon
                //.setIcon(R.drawable.androidhappy)
                // Set Dialog Title
                .setTitle("Select To Classify or Not")
                // Set Dialog Message
                .setMessage("Classification")
                // Positive button
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                           if(mListener!=null) {
                               mListener.onOkay(true);
                           }

                    }
                })

                // Negative Button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,	int which) {
                        // Do something else
                        if(getActivity() instanceof Listener)
                            ((Listener)getActivity()).onCancel(false);
                    }
                }).create();
    }
}
