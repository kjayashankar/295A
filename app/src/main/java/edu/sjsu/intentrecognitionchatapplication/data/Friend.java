package edu.sjsu.intentrecognitionchatapplication.data;

/**
 * Created by jay on 9/11/17.
 */

public class Friend {

    private String name;

    private String read;

    private String picURL;

    public String getName(){
        return name;
    }

    public Friend(String name, String read, String picURL){
        this.name = name;
        this.read = read;
        this.picURL = picURL;
    }

    /*public Friend(String name) {
        this.name = name;
    }*/

    public String getPic(){
        return this.picURL;
    }

    public boolean isRead(){
        return "READ".equalsIgnoreCase(read);
    }

    @Override
    public String toString(){
        return name;
    }
}
