package edu.sjsu.intentrecognitionchatapplication.data;

import java.util.ArrayList;

/**
 * Created by admin on 12/1/2017.
 */

public class Restaurant {
    private String name;
    private String phone;
    private String website;
    private String imageUrl;
    private String rating;
    private String price;
    private ArrayList<String> address = new ArrayList<>();
    private double latitude;
    private double longitude;
    private ArrayList<String> categories = new ArrayList<>();
    public Restaurant(String name, String phone, String website,
                      String imageUrl, ArrayList<String> address,
                      double latitude, double longitude,String rating,String price) {
        this.name = name;
        this.phone = phone;
        this.website = website;
        this.imageUrl = imageUrl;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.categories = categories;
        this.rating=rating;
        this.price=price;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return  website;
    }
    public String getRating(){
        return rating;
    }
    public String getPrice(){
        return price;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public ArrayList<String> getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
