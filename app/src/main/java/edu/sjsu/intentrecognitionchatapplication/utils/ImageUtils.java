package edu.sjsu.intentrecognitionchatapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Jay on 12/1/2017.
 */

public final class ImageUtils {

    private static final String directoryName = "SmartChat Images";

    public static String getStringImage(Bitmap bitmap) {
        try{
            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                String temp = Base64.encodeToString(b, Base64.DEFAULT);
                return temp;

            }
            return "";
        }
        catch(Exception e){
            Log.d("MEMORY","Out of Memory errors");
        }
        return "";
    }

     public static void SaveBitmap(String path, String fileName, Bitmap bmp){

        path = path+ "/" + directoryName;
        File file = new File(path);
        if(!file.exists()){
            file.mkdir();
        }
        try {
            file = new File(path+ "/" + fileName);
            FileOutputStream Filestream = new FileOutputStream(path + "/" + fileName);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Filestream.write(byteArray);
            Filestream.close();
            bmp.recycle();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Bitmap getImage(Context context, String name){

        String path = context.getCacheDir().getAbsolutePath() + "/" + directoryName;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            File file = new File(path+ "/" + name);
            if(file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path+ "/" + name, options);
                return bitmap;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void SaveBitmapFromString(Context applicationContext, String string, String image) {
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);

        //byte[] decodedBytes = Base64.decode(input, Base64.DEFAULT);
        Bitmap bitm =  BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        //Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        SaveBitmap(applicationContext.getCacheDir().getAbsolutePath(),string,bitm);
        Log.v("ImageUtils","saved successfully!");
    }
}
