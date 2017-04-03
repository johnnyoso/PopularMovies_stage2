package com.example.android.popularmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Movie;

/**
 * Created by john.osorio on 21/02/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "moviesDb.db";

    private static final int VERSION = 6;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    //Called when the movie database is created for the first time


    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE = "CREATE TABLE "                 + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID                        + " INTEGER PRIMARY KEY, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID            + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE         + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_PLOT          + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE  + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_SCORE         + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_TRAILER_KEYS  + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER        + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_REVIEWS       + " TEXT);";

        db.execSQL(CREATE_TABLE);
    }

    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
