package com.example.android.popularmovies.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by john.osorio on 21/02/2017.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.example.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_MOVIES).build();

        //Movie table and column names
        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "movieId";

        public static final String COLUMN_MOVIE_TITLE = "title";

        public static final String COLUMN_MOVIE_PLOT = "plot";

        public static final String COLUMN_MOVIE_RELEASE_DATE = "date";

        public static final String COLUMN_MOVIE_SCORE = "score";

        //I won't save the image of the trailer thumbnails anymore as it's too tedious
        //I'll just save all the Movie trailer keys as comma separated values and then enumerate them at query
        public static final String COLUMN_MOVIE_TRAILER_KEYS = "trailers";

        public static final String COLUMN_MOVIE_POSTER = "poster";

        public static final String COLUMN_MOVIE_REVIEWS = "reviews";

    }
}
