package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Data.MovieContract;
import com.example.android.popularmovies.Data.MovieDbHelper;
import com.example.android.popularmovies.Utilities.NetworkUtils;
import com.squareup.picasso.Picasso;
import java.util.Arrays;
import java.util.List;

/**
 * Created by john.osorio on 11/01/2017.
 */

public class MovieInformation extends AppCompatActivity implements
        TrailerAdapter.TrailerAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<String[][]>{

    private static final String TAG = MovieInformation.class.getSimpleName();

    //For the Async Task Loader
    private int MOVIE_TRAILER_ID;

    //RecyclerView stuff
    private RecyclerView mRecyclerView;
    private TrailerAdapter mTrailerAdapter;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private TextView movieTitleTextView;
    private TextView movieOverviewTextView;
    private TextView movieReleaseDateTextView;
    private TextView movieScoreTextView;
    private ImageView moviePosterThumbnail;

    //Experiment on a Clickable star imageview
    private ImageView movieFavouriteStar;

    //For showing the movie reviews
    private TextView movieReviewTextView;
    private TextView movieReviewLabelTextView;

    //This will be the unique movie ID for checking database for identicals
    private String movieId;

    //Movie information
    private String movieTitle;
    private String movieOverview;
    private String movieReleaseDate;
    private String movieScore;
    private String moviePosterPath;

    private int movieTrailerCount;
    private String[] movieTrailerKeyArray;

    private String movieTrailerKeys;

    private String movieReview;

    private SQLiteDatabase mDb;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_information);

        //Get a reference to the database
        MovieDbHelper dbHelper = new MovieDbHelper(this);
        mDb = dbHelper.getReadableDatabase();


        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_trailer_list);
        mLoadingIndicator = (ProgressBar)findViewById(R.id.trailer_loading_indicator);
        mErrorMessageDisplay = (TextView)findViewById(R.id.trailer_error_message_display);

        int currentOrientation = getResources().getConfiguration().orientation;
        Log.v(TAG, "Current orientation is: " + currentOrientation);

        //I think PORTRAIT is 1, LANDSCAPE is 2, SQUARE is 0

        //Find out what orientation it's currently in so we can modify the recyclerView layout manager
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        if(currentOrientation == 1){

            GridLayoutManager gridManagerTrailer = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(gridManagerTrailer);

        } else {
            GridLayoutManager gridManagerTrailer = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(gridManagerTrailer);
        }

         /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        /*
         * The MovieAdapter is responsible for linking our weather data with the Views that
         * will end up displaying our weather data.
         */
        mTrailerAdapter = new TrailerAdapter(this);
        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mTrailerAdapter);


        movieTitleTextView = (TextView)findViewById(R.id.tv_movie_title);
        movieOverviewTextView = (TextView)findViewById(R.id.tv_movie_overview);
        movieReleaseDateTextView = (TextView)findViewById(R.id.tv_movie_release_date);
        movieScoreTextView = (TextView)findViewById(R.id.tv_movie_score);
        moviePosterThumbnail = (ImageView)findViewById(R.id.movie_thumbnail);
        movieReviewTextView = (TextView)findViewById(R.id.tv_movie_review);
        movieReviewLabelTextView = (TextView)findViewById(R.id.tv_movie_review_label);

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();

        final String intentClassName = extras.getString("Source");
        Log.v(TAG, "Class origin name is " + intentClassName);

        //TODO : Perhaps get an Intent selector where you can determine if intent came from Main Activity or Favourites Page
        if (intentClassName.equals(MainActivity.class.getSimpleName())){

            MOVIE_TRAILER_ID = 10;

            movieId = extras.getString("MOVIE_ID");
            movieTitle = extras.getString("MOVIE_TITLE");
            movieOverview = extras.getString("MOVIE_OVERVIEW");
            movieReleaseDate = extras.getString("MOVIE_RELEASE_DATE");
            movieScore = extras.getString("MOVIE_SCORE");
            moviePosterPath = extras.getString("MOVIE_POSTER");
            movieReview = extras.getString("MOVIE_REVIEW");
        }

        else if (intentClassName.equals(FavouritesPage.class.getSimpleName())){

            MOVIE_TRAILER_ID = 11;

            movieId = extras.getString("MOVIE_ID");
            movieTitle = extras.getString("MOVIE_TITLE");
            movieOverview = extras.getString("MOVIE_OVERVIEW");
            movieReleaseDate = extras.getString("MOVIE_RELEASE_DATE");
            movieScore = extras.getString("MOVIE_SCORE");
            moviePosterPath = extras.getString("MOVIE_POSTER_PATH");
            movieReview = extras.getString("MOVIE_REVIEW");

            //This one is a comma separated values containing all trailer keys
            movieTrailerKeys = extras.getString("MOVIE_TRAILER_KEYS");
        }

        //This is a clickable imageView for adding movies to favourites
        movieFavouriteStar = (ImageView)findViewById(R.id.tv_favourite_star);

        //Perhaps check movie is already in the database so the star remains yellow if true?
        if(checkMovieDbIfExisting(movieId)){
            movieFavouriteStar.setImageResource(android.R.drawable.btn_star_big_on);

        } else{
            //turns off the star button if movie isn't in the favourites list
            movieFavouriteStar.setImageResource(android.R.drawable.btn_star_big_off);
        }

        //Show the movie title
        movieTitleTextView.setText(movieTitle);

        //Show the movie plot
        movieOverviewTextView.setText("Plot \n" + movieOverview);

        //Show the movie release date
        movieReleaseDateTextView.setText("Release Date \n" + movieReleaseDate);

        //Show the movie score
        movieScoreTextView.setText("Score \n" + movieScore);

        //Show the movie poster
        Picasso.with(this).
                load(moviePosterPath).
                placeholder(R.drawable.waiting).
                error(R.drawable.error_413).
                into(moviePosterThumbnail);

        //TODO initialise the Async task loader for the movie trailers
        int movietrailerId = MOVIE_TRAILER_ID;

        LoaderManager.LoaderCallbacks<String[][]> callback = MovieInformation.this;

        if(getSupportLoaderManager().getLoader(MOVIE_TRAILER_ID) == null) {
            //Initialize Popular Movies Async Task Loader
            getSupportLoaderManager().initLoader(movietrailerId, extras, callback);
        } else {
            //Restart Popular Movies Async Task Loader
            getSupportLoaderManager().restartLoader(movietrailerId, extras, callback);
        }

    }

    //Awwww man turns out i had to create a separate Async Task Loader for the trailers
    //TODO create an Async Task Loader for getting the Youtube trailer and thumbnail URLs
    @Override
    public Loader<String[][]> onCreateLoader(final int trailerLoaderID, final Bundle extras) {

        return new AsyncTaskLoader<String[][]>(this) {

            String[][] mMovieTrailer = null;

            @Override
            protected void onStartLoading() {

                if(mMovieTrailer != null){
                    deliverResult(mMovieTrailer);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String[][] loadInBackground() {

                if(trailerLoaderID == 10) {
                    try {
                        //this is to get how many trailers this movie has
                        movieTrailerCount = Integer.parseInt(extras.getString("MOVIE_TRAILER_COUNT"));

                        //initialise the String arrays please
                        mMovieTrailer = new String[movieTrailerCount][2];
                        movieTrailerKeyArray = new String[movieTrailerCount];

                        for (int i = 0; i < movieTrailerCount; i++) {
                        /*
                        * this is where you dynamically create the buttons/images/trailer url links whatever
                        * not sure if I should build the trailer URLs in an async task loader.....
                        * then within it create the imageViews, make it clickable and store the trailer URLs in database???
                        */
                            String trailerKeyId = extras.getString("MOVIE_TRAILER_KEY" + String.valueOf(i));

                            //Store all the availble trailer Key Ids in the array just in case we need to store them in the database later
                            movieTrailerKeyArray[i] = trailerKeyId + ", ";

                            Log.v(TAG, "Movie key number " + String.valueOf(i) + " is: " + trailerKeyId);
                            //TODO build the trailer URL and the thumbnail URL

                        /* I don't think you need an Async Task just to build the URL for the
                         *   trailer links to video and image
                         */

                            String trailerThumbnailUrl = (NetworkUtils.buildMovieTrailerThumbnailUrl(trailerKeyId)).toString();
                            String trailerLinkUrl = (NetworkUtils.buildMovieTrailerLinkUrl(trailerKeyId)).toString();
                            Log.v(TAG, "Movie trailer video URL is: " + trailerLinkUrl);
                            mMovieTrailer[i][0] = trailerThumbnailUrl;
                            mMovieTrailer[i][1] = trailerLinkUrl;
                        }
                        //Merge all contents of the String array into one single String value
                        StringBuilder trailerBuilder = new StringBuilder();
                        for (String s : movieTrailerKeyArray) {
                            trailerBuilder.append(s);
                        }
                        String totalMovieTrailerKeys = trailerBuilder.toString();
                        Log.v(TAG, "movie trailer array " + totalMovieTrailerKeys);

                        return mMovieTrailer;

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                else if (trailerLoaderID == 11) {
                    //Firstly find out how many trailers are there in the String
                    try {
                        List<String> movieTrailerKeyList = Arrays.asList(movieTrailerKeys.split("\\s*,\\s*"));
                        int trailerCount = movieTrailerKeyList.size();

                        //initialise the String arrays please
                        mMovieTrailer = new String[trailerCount][2];

                        if (trailerCount > 0) {
                            int j = 0;
                            for (String trailerKeyId : movieTrailerKeyList) {
                                //trailerKeyId = movieTrailerKeyList.get(j);
                                Log.v(TAG, "Trailer key is: " + trailerKeyId);

                                String trailerThumbnailUrl = (NetworkUtils.buildMovieTrailerThumbnailUrl(trailerKeyId)).toString();
                                String trailerLinkUrl = (NetworkUtils.buildMovieTrailerLinkUrl(trailerKeyId)).toString();
                                Log.v(TAG, "Movie trailer video URL is: " + trailerLinkUrl);
                                mMovieTrailer[j][0] = trailerThumbnailUrl;
                                mMovieTrailer[j][1] = trailerLinkUrl;
                                j++;
                            }
                        }

                        return mMovieTrailer;

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }

                }
                return null;
            }

            @Override
            public void deliverResult(String[][] data) {
                mMovieTrailer = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[][]> loader, String[][] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mTrailerAdapter.setMovieTrailer(data);
        if(data != null){
            showMovieDataView();
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[][]> loader) {

    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(String currentTrailerLink) {

        Log.v(TAG, currentTrailerLink);

        Uri uriTrailer = Uri.parse(currentTrailerLink);
        playYoutubeVideo(uriTrailer);
    }

    public void playYoutubeVideo(Uri uriParameter) {
        Intent uriIntent = new Intent(Intent.ACTION_VIEW);
        uriIntent.setData(uriParameter);
        if(uriIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(uriIntent);
        }
    }




    //This is where you add the favourite movie details in the database
    public void addToFavourites(View view){

        //This should change the star image when clicked but somehow it doesn't stay that way when you go back to it
        movieFavouriteStar.setImageResource(android.R.drawable.btn_star_big_on);

        // TODO First check if the movie is already in the database by calling a method that checks it

        if(checkMovieDbIfExisting(movieId)){
            Toast.makeText(getBaseContext(), "Movie already in favourites!", Toast.LENGTH_LONG).show();
            return;
        }


        ContentValues movieContentValues = new ContentValues();

        //Put all the move information excluding trailers and reviews into the movieContentValues
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movieTitle);
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_PLOT, movieOverview);
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movieReleaseDate);
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SCORE, movieScore);
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEWS, movieReview);
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, moviePosterPath);

        //Merge all contents of the String array into one single String value
        StringBuilder trailerBuilder = new StringBuilder();
        for(String s : movieTrailerKeyArray) {
            trailerBuilder.append(s);
        }
        String totalMovieTrailerKeys = trailerBuilder.toString();

        movieContentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TRAILER_KEYS, totalMovieTrailerKeys);

        Uri movieDetailsUri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieContentValues);

        //Display a message that the movie has been added to favourites movie database
        if(movieDetailsUri != null) {
            Toast.makeText(getBaseContext(), "Movie added to favourites!", Toast.LENGTH_LONG).show();
        }

        //finish activity (this returns to MainActivity
        finish();

    }

    //Check if there are any duplicate movies in the favourties list
    private boolean checkMovieDbIfExisting(String movieId){
        String[] movieIdArray = {MovieContract.MovieEntry.COLUMN_MOVIE_ID};

        Cursor mDbQuery =  mDb.query(
                MovieContract.MovieEntry.TABLE_NAME,
                movieIdArray,
                null,
                null,
                null,
                null,
                null
        );

        if (mDbQuery != null){
            mDbQuery.moveToFirst();
            int columnIndex = mDbQuery.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);

            while(!mDbQuery.isAfterLast()) {
                Log.v(TAG, "Movie Id is " + mDbQuery.getString(columnIndex));
                if(movieId.equals(mDbQuery.getString(columnIndex))){
                    mDbQuery.close();
                    return true;
                } else {
                    mDbQuery.moveToNext();
                }
            }
        }
        mDbQuery.close();
        return false;
    }

    public void showMovieReviews(View view) {
        movieReviewLabelTextView.setText("Review/s");
        movieReviewTextView.setText(movieReview);
    }

}
