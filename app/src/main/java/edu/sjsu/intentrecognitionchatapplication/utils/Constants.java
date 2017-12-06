package edu.sjsu.intentrecognitionchatapplication.utils;

import edu.sjsu.intentrecognitionchatapplication.BuildConfig;

/**
 * Created by Jay on 11/27/2017.
 */

public class Constants {

    public static final String HOST_NAME = "10.0.0.98";

    public static final int PORT = 8080;
    public static final String YELP_TOKEN = BuildConfig.YELP_TOKEN;
    public static final String YELP_BASE_URL = "https://api.yelp.com/v3/businesses/search?term=restaurants";
    public static final String YELP_LOCATION_QUERY_PARAMETER = "location";
    public static final String YELP_TERM="term";
    public static final String YELP_LIMIT="limit";
}
