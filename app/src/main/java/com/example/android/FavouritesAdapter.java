package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.Data.MovieContract;

/**
 * Created by john.osorio on 27/02/2017.
 */

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.MovieViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private String[] mMovieArray;

    public FavouritesAdapter (@NonNull Context customContext, FavouritesAdapterOnClickHandler clickHandler) {
        mContext = customContext;
        mClickHandler = clickHandler;
    }

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final FavouritesAdapterOnClickHandler mClickHandler;

    public interface FavouritesAdapterOnClickHandler {
        void onClick(String[] faveMovieArray);
    }

    public FavouritesAdapter (FavouritesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.fave_movie_list_item, parent, false);

        return new MovieViewHolder(view);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView movieTitleTextView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            movieTitleTextView = (TextView) itemView.findViewById(R.id.fave_movie_titles);
            itemView.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

            //Get the indices for the movie database values
            int idIndex = mCursor.getColumnIndex(MovieContract.MovieEntry._ID);
            int movieIdIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            int movieTitleIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
            int moviePlotIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_PLOT);
            int movieReleaseDateIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE);
            int movieScoreIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_SCORE);
            int movieTrailerKeysIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TRAILER_KEYS);
            int moviePosterIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
            int movieReviewsIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_REVIEWS);

            //Determine the values of the wanted data
            final int id = mCursor.getInt(idIndex);
            final String movieId = mCursor.getString(movieIdIndex);
            final String movieTitle = mCursor.getString(movieTitleIndex);
            final String moviePlot = mCursor.getString(moviePlotIndex);
            final String movieReleaseDate = mCursor.getString(movieReleaseDateIndex);
            final String movieScore = mCursor.getString(movieScoreIndex);
            final String movieTrailerKeys = mCursor.getString(movieTrailerKeysIndex);
            final String moviePoster = mCursor.getString(moviePosterIndex);
            final String movieReviews = mCursor.getString(movieReviewsIndex);

            mMovieArray = new String[8];

            mMovieArray[0] = movieId;
            mMovieArray[1] = movieTitle;
            mMovieArray[2] = moviePlot;
            mMovieArray[3] = movieReleaseDate;
            mMovieArray[4] = movieScore;
            mMovieArray[5] = movieTrailerKeys;
            mMovieArray[6] = moviePoster;
            mMovieArray[7] = movieReviews;

            mClickHandler.onClick(mMovieArray);
        }
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(FavouritesAdapter.MovieViewHolder holder, int position) {
        //Get to the right location in the cursor
        mCursor.moveToPosition(position);

        //Get the indices for the movie database values
        int idIndex = mCursor.getColumnIndex(MovieContract.MovieEntry._ID);
        int movieTitleIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);

        //Determine the values of the wanted data
        final int id = mCursor.getInt(idIndex);
        final String movieTitle = mCursor.getString(movieTitleIndex);

        holder.movieTitleTextView.setText(movieTitle);
        holder.itemView.setTag(id);

    }



    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }
}
