package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;


/**
 * Created by john.osorio on 11/01/2017.
 */

// I think this adapter is for when you click on a list item?
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private List<Map<String, String>> mMovieBundle;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final MovieAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(Map<String, String> currentMovieBundle);
    }

    /**
     * Creates a MovieAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        //public final TextView mMovieTitle;
        public final ImageView mMoviePoster;

        public MovieAdapterViewHolder(View view){

            super(view);
            //mMovieTitle = (TextView)view.findViewById(R.id.tv_movie_title);
            mMoviePoster = (ImageView)view.findViewById(R.id.tv_movie_poster);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Map<String, String> currentMovieData = mMovieBundle.get(adapterPosition);
            mClickHandler.onClick(currentMovieData);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the movie
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param movieAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param adapterPosition                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int adapterPosition) {

        String currentMoviePoster = mMovieBundle.get(adapterPosition).get("EXTRA_MOVIE_POSTER_PATH");
        Context context = movieAdapterViewHolder.mMoviePoster.getContext();
        Uri moviePosterUri = Uri.parse(currentMoviePoster);
        Picasso.with(context).
                load(moviePosterUri).
                placeholder(R.drawable.waiting).
                error(R.drawable.error_413).
                into(movieAdapterViewHolder.mMoviePoster);

    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        if(mMovieBundle == null) {
            return 0;
        }
        return mMovieBundle.size();
    }

    /**
     * This method is used to set the movie data on a MovieAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MovieAdapter to display it.
     *
     *
     */
    public void setMovieBundle(List<Map<String, String>> movieBundle){
        mMovieBundle = movieBundle;
        notifyDataSetChanged();
    }
}
