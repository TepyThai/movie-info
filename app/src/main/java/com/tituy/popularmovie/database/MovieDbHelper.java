package com.tituy.popularmovie.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tituy.popularmovie.database.MovieContract.ReviewEntry;
import com.tituy.popularmovie.database.MovieContract.MovieEntry;
import com.tituy.popularmovie.database.MovieContract.VideoEntry;

/**
 * Created by txb on 2016/11/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    //Databse Version (must be incremented when there is changes in db Schema)
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "movie.db";

    //DbHelper Constructor
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //create Movie Table
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_IS_FAVORITE + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_IS_POPULAR + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_IS_TOP_RATED + " INTEGER NOT NULL " +");";

        //create Review Table
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY," +
                ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL ," +
                ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL," +
                ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL," +
                ReviewEntry.COLUMN_REVIEW_FOREIGN_KEY + " INTEGER NOT NULL," +
                // Set up the review id key column as a foreign key to movie table.
                " FOREIGN KEY (" + ReviewEntry.COLUMN_REVIEW_FOREIGN_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + ")" +");";

        //create Video Table
        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " (" +
                VideoEntry._ID + " INTEGER PRIMARY KEY," +
                VideoEntry.COLUMN_VIDEO_ID + " TEXT NOT NULL ," +
                VideoEntry.COLUMN_VIDEO_KEY + " TEXT NOT NULL," +
                VideoEntry.COLUMN_VIDEO_FOREIGN_KEY + " INTEGER NOT NULL," +
                // Set up the review id key column as a foreign key to movie table.
                " FOREIGN KEY (" + VideoEntry.COLUMN_VIDEO_FOREIGN_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + ")" + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        //drop old tables and create new one when the database version changed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}