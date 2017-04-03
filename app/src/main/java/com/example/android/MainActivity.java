package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.android.popularmovies.MovieAdapter.MovieAdapterOnClickHandler;
import com.example.android.popularmovies.Utilities.JSONUtils;
import com.example.android.popularmovies.Utilities.NetworkUtils;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Map<String, String>>>{

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;


    private int POPULAR_MOVIE_LOADER_ID = 0;

    private int TOP_RATED_MOVIE_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_movielist);

        mErrorMessageDisplay = (TextView)findViewById(R.id.tv_error_message_display);
        /*
         * LinearLayoutManager can support HORIZONTAL or VERTICAL orientations. The reverse layout
         * parameter is useful mostly for HORIZONTAL layouts that should reverse for right to left
         * languages.
         */
        int currentOrientation = getResources().getConfiguration().orientation;
        Log.v(TAG, "Current orientation is: " + currentOrientation);

        //I think PORTRAIT is 1, LANDSCAPE is 2, SQUARE is 0

        //Find out what orientation it's currently in so we can modify the recyclerView layout manager
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
        mRecyclerView.setHasFixedSize(true);

        /*
         * The MovieAdapter is responsible for linking our weather data with the Views that
         * will end up displaying our weather data.
         */
        mMovieAdapter = new MovieAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mMovieAdapter);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        //Load popular movies first as default welcome page
        //Async Task Loader for popular movies
        int popularMovieLoaderID = POPULAR_MOVIE_LOADER_ID;
        Bundle bundle = null;
        LoaderManager.LoaderCallbacks<List<Map<String, String>>> callback = MainActivity.this;

        if(getSupportLoaderManager().getLoader(POPULAR_MOVIE_LOADER_ID) == null) {
            //Initialize Popular Movies Async Task Loader
            getSupportLoaderManager().initLoader(popularMovieLoaderID, bundle, callback);
        } else {
            //Restart Popular Movies Async Task Loader
            getSupportLoaderManager().restartLoader(popularMovieLoaderID, bundle, callback);
        }

    }

    @Override
    public Loader<List<Map<String, String>>> onCreateLoader(final int MovieLoaderID, Bundle args) {

        return new AsyncTaskLoader<List<Map<String, String>>>(this) {

            List<Map<String, String>> mMovieData = null;

            @Override
            protected void onStartLoading() {

                if(mMovieData != null){
                    deliverResult(mMovieData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public List<Map<String, String>> loadInBackground() {

                if(MovieLoaderID == 0) {
                    URL movieUrl = NetworkUtils.buildPopularMoviesUrl();
                    try {
                        String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieUrl);
                        //Log.v(TAG, "JsonMovieResponse is: " + jsonMovieResponse);

                        List<Map<String, String>> jsonMovieData = JSONUtils.getMovieBundleFromJson(MainActivity.this, jsonMovieResponse);
                        //Log.v(TAG, "JsonMovieData is: " + jsonMovieData);

                        return jsonMovieData;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                else if (MovieLoaderID == 1){
                    URL movieUrl = NetworkUtils.buildTopRatedMoviesUrl();

                    try {
                        String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieUrl);

                        //Log.v(TAG, "JsonMovieResponse is: " + jsonMovieResponse);

                        List<Map<String, String>> jsonMovieData = JSONUtils.getMovieBundleFromJson(MainActivity.this, jsonMovieResponse);
                        //Log.v(TAG, "JsonMovieData is: " + jsonMovieData);

                        return jsonMovieData;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                return null;
            }
            @Override
            public void deliverResult(List<Map<String, String>> data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }


    @Override
    public void onLoadFinished(Loader<List<Map<String, String>>> loader, List<Map<String, String>> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mMovieAdapter.setMovieBundle(data);
        if(data != null){
            showMovieDataView();
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Map<String, String>>> loader) {

    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param movieBundle The weather for the day that was clicked
     */

    //This also displays the information about the clicked movie
    @Override
    public void onClick(Map<String, String> movieBundle) {

        Context context = this;

        //Let's do an intent here and pass on the data to the MovieInformation class
        Class destinationActivity = MovieInformation.class;
        Intent intentMovieData = new Intent(context, destinationActivity);
        Bundle extras = new Bundle();

        extras.putString("Source", MainActivity.class.getSimpleName());

        //These are the generic movie information
        extras.putString("MOVIE_ID", movieBundle.get("EXTRA_MOVIE_ID"));
        extras.putString("MOVIE_TITLE", movieBundle.get("EXTRA_MOVIE_TITLE"));
        extras.putString("MOVIE_OVERVIEW", movieBundle.get("EXTRA_MOVIE_OVERVIEW"));
        extras.putString("MOVIE_RELEASE_DATE", movieBundle.get("EXTRA_MOVIE_RELEASE_DATE"));
        extras.putString("MOVIE_SCORE", movieBundle.get("EXTRA_MOVIE_SCORE"));
        extras.putString("MOVIE_POSTER", movieBundle.get("EXTRA_MOVIE_POSTER_PATH"));

        //Awwwww man I hope this works, this is so tedious
        //Determine how many trailers this movie has and iterate through it
        int movieCount = Integer.parseInt(movieBundle.get("EXTRA_TRAILER_COUNT"));

        for(int i = 0; i < movieCount; i++){
            extras.putString("MOVIE_TRAILER_KEY" + String.valueOf(i), movieBundle.get("EXTRA_TRAILER_KEY" + String.valueOf(i)));
        }

        //Send the number of movie trailers
        extras.putString("MOVIE_TRAILER_COUNT", movieBundle.get("EXTRA_TRAILER_COUNT"));

        //Add in the movie reviews if any
        extras.putString("MOVIE_REVIEW", movieBundle.get("EXTRA_MOVIE_REVIEW"));

        intentMovieData.putExtras(extras);
        startActivity(intentMovieData);
    }
    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
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
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_sort_by_popular){
            int popularMovieLoaderID = POPULAR_MOVIE_LOADER_ID;
            Bundle bundle = null;
            LoaderManager.LoaderCallbacks<List<Map<String, String>>> callback = MainActivity.this;
            getSupportLoaderManager().initLoader(popularMovieLoaderID, bundle, callback);
            return true;
        }

        if(id == R.id.action_sort_by_top_rated){
            int topRatedMovieLoaderID = TOP_RATED_MOVIE_LOADER_ID;
            Bundle bundle = null;
            LoaderManager.LoaderCallbacks<List<Map<String, String>>> callback = MainActivity.this;
            getSupportLoaderManager().initLoader(topRatedMovieLoaderID, bundle, callback);
            return true;
        }

        if(id == R.id.action_show_favourites) {

            //Go to the favourites page
            Intent intent = new Intent(MainActivity.this, FavouritesPage.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }
}
