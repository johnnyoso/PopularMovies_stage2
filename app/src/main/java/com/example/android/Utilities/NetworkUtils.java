package com.example.android.popularmovies.Utilities;

/**
 * Created by john.osorio on 9/01/2017.
 */

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static android.content.ContentValues.TAG;

/**
 * These utilities will be used to communicate with moviesDB
 */

public class NetworkUtils {
    //This is the base movie URL
    private static String MOVIEDB_BASE_URL = "https://api.themoviedb.org";

    //This only shows the first page
    private static String POPULAR_MOVIES = "3/movie/popular?page=1&language=en-US&api_key=b97894209e7ccf194bb3753807766c01";

    //This only shows the first page
    private static String TOP_RATED_MOVIES = "3/movie/top_rated?page=1&language=en-US&api_key=b97894209e7ccf194bb3753807766c01";

    //Movie reviews
    private static String MOVIE_REVIEWS = "reviews?api_key=b97894209e7ccf194bb3753807766c01&language=en-US";

    //Movie trailer Uri
    private static String MOVIE_TRAILER_1 = "3/movie";

    private static String MOVIE_TRAILER_2 = "videos?api_key=b97894209e7ccf194bb3753807766c01&language=en-US";

    private static String YOUTUBE_BASE_URL = "https://m.youtube.com/watch";

    private static String YOUTUBE_BASE_URL_1 = "?v=";

    private static String YOUTUBE_BASE_THUMBNAIL_URL = "http://img.youtube.com/vi";

    private static String YOUTUBE_THUMBNAIL_START = "0.jpg";

    //Url to watch youtube trailer = "https://www.youtube.com/watch?v={Trailer key}"

    //Url to bring up youtube trailer thumbnail = http://img.youtube.com/vi/{Trailer Key}/0.jpg

    //base case: http://image.tmdb.org/t/p/image-size/poster_path
    //example: http://image.tmdb.org/t/p/w185//ndlQ2Cuc3cjTL7lTynw6I4boP4S.jpg

    //Hi again, these are the the sizes that I know: "w92", "w154", "w185", "w342", "w500", "w780", or "original";
    //and I think there isn't any other sizes "original" will give you a very large poster, if you're on mobile "w185"
    //is the best choice

    //Use this to create a URL string for popular movies
    public static URL buildPopularMoviesUrl(){
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendEncodedPath(POPULAR_MOVIES)
                .build();

        URL url = null;
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        //Log.v(TAG, "Built Popular Movies URI " + url);

        return url;
    }

    //Use this to create a URL string for top rated movies
    public static URL buildTopRatedMoviesUrl(){
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendEncodedPath(TOP_RATED_MOVIES)
                .build();

        URL url = null;
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        //Log.v(TAG, "Built Top Rated Movies URI " + url);

        return url;
    }

    //Use this to create a URL string for the movie trailers list
    public static URL buildMovieTrailerUrl(String movieID) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendEncodedPath(MOVIE_TRAILER_1)
                .appendEncodedPath(movieID)
                .appendEncodedPath(MOVIE_TRAILER_2)
                .build();

        URL url = null;

        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        //Log.v(TAG, "Built Movie Trailer " + url);

        return url;
    }

    //Use this to create the URL for the specific Trailer thumbnail image
    public static URL buildMovieTrailerThumbnailUrl(String movieID){
        Uri builtUri = Uri.parse(YOUTUBE_BASE_THUMBNAIL_URL).buildUpon()
                .appendEncodedPath(movieID)
                .appendEncodedPath(YOUTUBE_THUMBNAIL_START)
                .build();

        URL url = null;

        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        //Log.v(TAG, "Built Movie Trailer Thumbnail " + url);

        return url;
    }

    //Use this to create the URL for the movie's reviews
    public static URL buildMovieReviewUrl(String movieID) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendEncodedPath(MOVIE_TRAILER_1)
                .appendEncodedPath(movieID)
                .appendEncodedPath(MOVIE_REVIEWS)
                .build();

        URL url = null;

        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        //Log.v(TAG, "Built Movie Reviews " + url);

        return url;
    }


    //The code here is embarrassing, I mean who appends strings within parsing it as a Uri? But i keep getting errors
    //because of the "?" thinking that it's a placeholder and just swaps the movie trailer key with "?"
    //e.g. the trailer becomes "https://m.youtube.com/watch/3xoxeCWpZyUv=" instead of https://m.youtube.com/watch?v=3xoxeCWpZyU
    //Use this to create the URL for the specific Trailer link
    public static URL buildMovieTrailerLinkUrl(String movieID){
        Uri builtUri = Uri.parse(YOUTUBE_BASE_URL+YOUTUBE_BASE_URL_1+movieID).buildUpon()
                .build();

        URL url = null;

        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        //Log.v(TAG, "Built Movie Trailer Link " + url);

        return url;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
