package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by john.osorio on 23/02/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private String[][] mMovieTrailer;
    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final TrailerAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface TrailerAdapterOnClickHandler  {
        void onClick(String currentTrailer);
    }

    /**
     * Creates a MovieAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public TrailerAdapter(TrailerAdapter.TrailerAdapterOnClickHandler clickHandler) {

        mClickHandler = clickHandler;
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mTrailerThumbnail;

        public TrailerAdapterViewHolder(View view){

            super(view);
            mTrailerThumbnail = (ImageView)view.findViewById(R.id.tv_trailer_thumbnail);
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

            //This gets the youtube video play link
            String currentMovieTrailer = mMovieTrailer[adapterPosition][1];
            mClickHandler.onClick(currentMovieTrailer);
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
    public TrailerAdapter.TrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailerAdapter.TrailerAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the movie
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param trailerAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param adapterPosition                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerAdapterViewHolder trailerAdapterViewHolder, int adapterPosition) {

        //Just grabs the youtube thumbnail url and shows it using Picasso
        String currentMoviePoster = mMovieTrailer[adapterPosition][0];
        Context context = trailerAdapterViewHolder.mTrailerThumbnail.getContext();
        Uri moviePosterUri = Uri.parse(currentMoviePoster);
        Picasso.with(context).
                load(moviePosterUri).
                placeholder(R.drawable.waiting).
                error(R.drawable.error_413).
                into(trailerAdapterViewHolder.mTrailerThumbnail);

    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        if(mMovieTrailer == null) {
            return 0;
        }
        return mMovieTrailer.length;
    }

    /**
     * This method is used to set the movie data on a MovieAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MovieAdapter to display it.
     *
     *
     */

    public void setMovieTrailer(String[][] movieTrailer){
        mMovieTrailer = movieTrailer;
        notifyDataSetChanged();
    }
}
