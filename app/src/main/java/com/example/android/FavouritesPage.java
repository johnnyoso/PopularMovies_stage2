package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Data.MovieContract;

/**
 * Created by john.osorio on 27/02/2017.
 */

public class FavouritesPage extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        FavouritesAdapter.FavouritesAdapterOnClickHandler{

    private static final String TAG = FavouritesPage.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 20;

    private FavouritesAdapter mFavouritesAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    private ProgressBar mLoadingIndicator;

    private TextView mDisplayEmptyPageText;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites_page);

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_fave_list);

        mLoadingIndicator = (ProgressBar)findViewById(R.id.fave_loading_indicator);

        mDisplayEmptyPageText = (TextView) findViewById(R.id.fave_empty_page_display);

        LinearLayoutManager faveLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(faveLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mFavouritesAdapter = new FavouritesAdapter(this, this);

        mRecyclerView.setAdapter(mFavouritesAdapter);

        showLoading();

         /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete

                //[Hint] Use getTag (from the adapter code) to get the id of the swiped item
                int id = (int)viewHolder.itemView.getTag();

                String stringId = Integer.toString(id);
                Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();


                getContentResolver().delete(uri, null, null);

                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, FavouritesPage.this);

            }
        }).attachToRecyclerView(mRecyclerView);

        if(getSupportLoaderManager().getLoader(MOVIE_LOADER_ID) == null){

            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);

        } else {

            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        }

    }

    /**
     * Called by the {@link android.support.v4.app.LoaderManagerImpl} when a new Loader needs to be
     * created. This Activity only uses one loader, so we don't necessarily NEED to check the
     * loaderId, but this is certainly best practice.
     *
     * @param loaderId The loader ID for which we need to create a loader
     * @param bundle   Any arguments supplied by the caller
     * @return A new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {


        switch (loaderId) {

            case MOVIE_LOADER_ID:
                /* URI for all rows of weather data in our weather table */
                Uri favouriteMovieQueryUri = MovieContract.MovieEntry.CONTENT_URI;

                return new CursorLoader(this,
                        favouriteMovieQueryUri,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    /**
     * Called when a Loader has finished loading its data.
     *
     * NOTE: There is one small bug in this code. If no data is present in the cursor do to an
     * initial load being performed with no access to internet, the loading indicator will show
     * indefinitely, until data is present from the ContentProvider. This will be fixed in a
     * future version of the course.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        mFavouritesAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);

        if (data.getCount() != 0) {
            showFavouritesMovieView();
        } else {

            //Display the message "page is empty" and then set the loading indicator invisible
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mDisplayEmptyPageText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*
         * Since this Loader's data is now invalid, we need to clear the Adapter that is
         * displaying the data.
         */
        mFavouritesAdapter.swapCursor(null);
    }

    /**
     * This method is for responding to clicks from our list.
     *
     * @param faveMovieArray String describing weather details for a particular day
     */
    @Override
    public void onClick(String[] faveMovieArray) {

        Context context = this;
        Class destinationClass = MovieInformation.class;
        Intent intentFavouriteData = new Intent(context, destinationClass);

        Bundle extrasFaveData = new Bundle();

        extrasFaveData.putString("Source", FavouritesPage.class.getSimpleName());

        //These are the generic movie information
        extrasFaveData.putString("MOVIE_ID", faveMovieArray[0]);
        extrasFaveData.putString("MOVIE_TITLE", faveMovieArray[1]);
        extrasFaveData.putString("MOVIE_OVERVIEW", faveMovieArray[2]);
        extrasFaveData.putString("MOVIE_RELEASE_DATE", faveMovieArray[3]);
        extrasFaveData.putString("MOVIE_SCORE", faveMovieArray[4]);
        extrasFaveData.putString("MOVIE_TRAILER_KEYS", faveMovieArray[5]);
        extrasFaveData.putString("MOVIE_POSTER_PATH", faveMovieArray[6]);
        extrasFaveData.putString("MOVIE_REVIEW", faveMovieArray[7]);

        intentFavouriteData.putExtras(extrasFaveData);

        startActivity(intentFavouriteData);
    }


    /**
     * This method will make the View for the weather data visible and hide the error message and
     * loading indicator.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showFavouritesMovieView() {
        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mDisplayEmptyPageText.setVisibility(View.INVISIBLE);
        /* Finally, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    /**
     * This method will make the loading indicator visible and hide the weather View and error
     * message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showLoading() {
        /* Then, hide the weather data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }
}
