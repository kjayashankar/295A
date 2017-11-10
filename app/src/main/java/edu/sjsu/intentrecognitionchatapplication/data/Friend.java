package edu.sjsu.intentrecognitionchatapplication.data;

/**
 * Created by jay on 9/11/17.
 */

public class Friend {

    private String name;

    private String read;

    public String getName(){
        return name;
    }

    public Friend(String name, String read){
        this.name = name;
        this.read = read;
    }

    public Friend(String name) {
        this.name = name;
    }

    public boolean isRead(){
        return "READ".equalsIgnoreCase(read);
    }

    @Override
    public String toString(){
        return name;
    }
}
