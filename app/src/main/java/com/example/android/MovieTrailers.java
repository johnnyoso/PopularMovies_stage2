package com.example.android.popularmovies;

import android.content.Context;
import android.os.Bundle;

import com.example.android.popularmovies.Utilities.JSONUtils;
import com.example.android.popularmovies.Utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;

/**
 * Created by john.osorio on 22/02/2017.
 */

public class MovieTrailers {

    private static Context mContext;

    //I need this just to get the total number of trailers in a movie. Zero if there are no trailers duh!
    public static int trailerCount = 0;

    //Use this to get the Youtube URL for the movie trailer
    public static String[] getYoutubeMovieTrailerKeyFromJson(Context context, String responseFromWebsiteAsURL)
            throws JSONException {

        final String YOUTUBE_TRAILER_INFO = "results";

        final String YOUTUBE_TRAILER_NAME = "name";

        final String YOUTUBE_TRAILER_KEY = "key";

        //Time to segregate the trailer IDs

        JSONObject movieTrailerJson = new JSONObject(responseFromWebsiteAsURL);

        JSONArray trailerArray = movieTrailerJson.getJSONArray(YOUTUBE_TRAILER_INFO);

        if (trailerArray.length() > 0) {

            final String[] trailerKey = new String[trailerArray.length()];

            trailerCount = trailerArray.length();

            //There are 2 elements in the movie trailer array apparently
            for (int i = 0; i < trailerArray.length(); i++) {

                String key;

                JSONObject currentMovieTrailer = trailerArray.getJSONObject(i);

                key = currentMovieTrailer.getString(YOUTUBE_TRAILER_KEY);

                trailerKey[i] = key;
            }
            return trailerKey;
        }

        else {
            return null;
        }
    }

    //A method to grab data from the URL and return a string json data in array
    public static String[] getMovieTrailerKeyData(String movieID){

        URL trailerUrl = NetworkUtils.buildMovieTrailerUrl(movieID);

        try{
            String jsonTrailerResponse = NetworkUtils.getResponseFromHttpUrl(trailerUrl);

            if (getYoutubeMovieTrailerKeyFromJson(mContext, jsonTrailerResponse) != null) {
                String[] jsonTrailerData = getYoutubeMovieTrailerKeyFromJson(mContext, jsonTrailerResponse);

                return jsonTrailerData;

            } else {
                return null;
            }

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //Get the movie reviews from the Url
    public static String getMovieReviewFromJson(Context context, String responseFromWebsiteAsURL)
        throws JSONException {

        final String MOVIE_REVIEW_INFO = "results";

        final String MOVIE_REVIEW_AUTHOR = "author";

        final String MOVIE_REVIEW_CONTENT = "content";

        //Time to segregate the trailer IDs

        JSONObject movieReviewJson = new JSONObject(responseFromWebsiteAsURL);

        JSONArray reviewArray = movieReviewJson.getJSONArray(MOVIE_REVIEW_INFO);

        if (reviewArray.length() > 0) {

            int reviewCount = reviewArray.length();

            String[] movieReviewsArray = new String[reviewCount];

            for (int i = 0; i < reviewArray.length(); i++) {

                final String movieReviewContent;

                final String movieReviewAuthor;

                JSONObject movieReviewJSON = reviewArray.getJSONObject(i);

                movieReviewAuthor = movieReviewJSON.getString(MOVIE_REVIEW_AUTHOR);

                movieReviewContent = movieReviewJSON.getString(MOVIE_REVIEW_CONTENT);

                movieReviewsArray[i] = "Author: " + movieReviewAuthor + "\n\n" + "Review: " + "\n\n" + movieReviewContent + "\n\n";
            }

            //Merge all contents of the String array into one single String value
            StringBuilder builder = new StringBuilder();
            for(String s : movieReviewsArray) {
                builder.append(s);
            }
            String totalMovieReviews = builder.toString();
            return totalMovieReviews;
        }

        else {
            return null;
        }
    }

    //A method to grab data from the URL and return a string json data in array
    public static String getMovieReviewReviewData(String movieID){

        URL ReviewUrl = NetworkUtils.buildMovieReviewUrl(movieID);

        try{
            String jsonReviewResponse = NetworkUtils.getResponseFromHttpUrl(ReviewUrl);

            if (getMovieReviewFromJson(mContext, jsonReviewResponse) != null) {
                String jsonReviewData = getMovieReviewFromJson(mContext, jsonReviewResponse);

                return jsonReviewData;

            } else {
                return null;
            }

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
