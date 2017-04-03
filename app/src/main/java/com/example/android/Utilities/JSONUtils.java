package com.example.android.popularmovies.Utilities;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.util.Log;

import com.example.android.popularmovies.MainActivity;
import com.example.android.popularmovies.MovieTrailers;
import com.example.android.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by john.osorio on 9/01/2017.
 */

public class JSONUtils {

    /**
     * This method takes in a JSON format Request Token and returns a string array response
     *
     * Movies: poster_path, overview, release_date, vote_average, original_title
     */
    public static List<Map<String, String>> getMovieBundleFromJson(Context context, String responseFromWebsiteAsURL)
            throws JSONException {

        //POPULAR movies page number
        final String MOVIE_PAGE = context.getString(R.string.page_number);

        //POPULAR movies information. Store it in a list
        final String MOVIE_LIST = context.getString(R.string.results_page);

        final String MOVIE_ID = context.getString(R.string.movie_id);

        //Movie original title
        final String MOVIE_TITLE = context.getString(R.string.original_title);

        //Movie plot / synopsis
        final String MOVIE_OVERVIEW = context.getString(R.string.overview_string);

        //Movie release date
        final String MOVIE_RELEASE_DATE = context.getString(R.string.release_d);

        //movie rating / score
        final String MOVIE_SCORE = context.getString(R.string.vote_score);

        //movie poster path
        final String MOVIE_POSTER = context.getString(R.string.poster_p);

        //This is a list of dictionary values of all movie details
        List<Map<String, String>> movieBundle = new ArrayList<Map<String, String>>();

        JSONObject moviePageJson = new JSONObject(responseFromWebsiteAsURL);

        //I have no way to know if there is an error

        JSONArray movieArray = moviePageJson.getJSONArray(MOVIE_LIST);

        //Bundle to hold each movie's data string

        //iterate through the movie database
        for(int i = 0; i < movieArray.length(); i++){

            //Create a dictionary to store movie details
            Map<String, String> myMap = new HashMap<String, String>();

            String id;
            String title;
            String overview;
            String releaseDate;
            String score;
            String posterPath;
            String posterSize = context.getString(R.string.poster_size);

            //Get the JSON Object representing the current movie
            JSONObject currentMovie = movieArray.getJSONObject(i);

            //Get the trailer URL/s for the movie
            id = currentMovie.getString(MOVIE_ID);
            Log.v(TAG, "Current Movie ID is: " + id);

            //Check if the movie has a trailer or not
            if(MovieTrailers.getMovieTrailerKeyData(id) != null){
                String[] movieTrailerData = MovieTrailers.getMovieTrailerKeyData(id);
                int x = MovieTrailers.trailerCount;
                //Log.v(TAG, "Trailer count is: " + String.valueOf(MovieTrailers.trailerCount));

                //I need to somehow dynamically add map keys as I add trailer keys :/
                //not sure if this will work or if there is a more efficient way
                for (int j = 0; j < x; j++) {

                    myMap.put("EXTRA_TRAILER_KEY" + String.valueOf(j), movieTrailerData[j]);
                    //Log.v(TAG, myMap.get("EXTRA_TRAILER_KEY" + String.valueOf(j)));
                }
                myMap.put("EXTRA_TRAILER_COUNT", String.valueOf(x));

            } else {
                myMap.put("EXTRA_TRAILER_COUNT", "0");
            }

            //Get the movie reviews
            String movieReviewDataTotal = MovieTrailers.getMovieReviewReviewData(id);
            //Log.v(TAG, "Movie review: " + movieReviewDataTotal);

            title = currentMovie.getString(MOVIE_TITLE);
            overview = currentMovie.getString(MOVIE_OVERVIEW);
            releaseDate = currentMovie.getString(MOVIE_RELEASE_DATE);
            score = currentMovie.getString(MOVIE_SCORE);
            posterPath = context.getString(R.string.base_poster_path) + posterSize + currentMovie.getString(MOVIE_POSTER);

            //Let's bundle up the movie information
            myMap.put("EXTRA_MOVIE_ID", id);
            myMap.put("EXTRA_MOVIE_TITLE", title);
            myMap.put("EXTRA_MOVIE_OVERVIEW", overview);
            myMap.put("EXTRA_MOVIE_RELEASE_DATE", releaseDate);
            myMap.put("EXTRA_MOVIE_SCORE", score);
            myMap.put("EXTRA_MOVIE_POSTER_PATH", posterPath);
            myMap.put("EXTRA_MOVIE_REVIEW", movieReviewDataTotal);

            movieBundle.add(i, myMap);
        }
        return movieBundle;
    }

}
