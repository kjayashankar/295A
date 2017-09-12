package edu.sjsu.intentrecognitionchatapplication.data;

/**
 * Created by jay on 9/11/17.
 */

public class Friend {

    private String name;

    public String getName(){
        return name;
    }

    public Friend(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
}
