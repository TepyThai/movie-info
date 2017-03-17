package com.tituy.popularmovie.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.tituy.popularmovie.database.MovieContract.MovieEntry;
import com.tituy.popularmovie.database.MovieContract.ReviewEntry;
import com.tituy.popularmovie.database.MovieContract.VideoEntry;
import com.tituy.popularmovie.R;

/**
 * Created by txb on 2016/11/17.
 */

public class MovieProvider extends ContentProvider{

    //URI Matcher to match the path code integer used by content provider
    private static final UriMatcher movieUriMatcher = buildUriMatcher();
    private MovieDbHelper mMovieDbHelper;

    //integer code for Uri Matcher
    //Dir Type
    static final int MOVIE = 200;
    static final int SUB_MOVIE = 300;
    static final int REVIEW = 600;
    static final int VIDEO = 700;
    //Item Type
    static final int MOVIE_WITH_ID = 201;
    static final int REVIEW_WITH_MOVIE_ID = 601;
    static final int VIDEO_WITH_MOVIE_ID = 701;

    //movie.movie_id = ?
    private static final String movieIDSelection = MovieEntry.COLUMN_MOVIE_ID + " = ? ";
    //review.review_foreign_key = ?
    private static final String reviewAndMovieIDSelection = ReviewEntry.COLUMN_REVIEW_FOREIGN_KEY + " = ? ";
    //video.video_foreign_key = ?
    private static final String videoAndMovieIDSelection = VideoEntry.COLUMN_VIDEO_FOREIGN_KEY + " = ? ";
    //movie.is_popular = ?
    private static final String popularMovieSelection = MovieEntry.COLUMN_IS_POPULAR + " = ? ";
    //movie.is_top_rated = ?
    private static final String topRatedMovieSelection = MovieEntry.COLUMN_IS_TOP_RATED + " = ? ";
    //movie.is_favourite = ?
    private static final String favouriteMovieSelection = MovieEntry.COLUMN_IS_FAVORITE + " = ? ";

    private Cursor getMovieByID(Uri uri, String[] projection, String sortOrder){
        String movieID = MovieEntry.getMovieID(uri);
        String selection = movieIDSelection;
        String[] selectionArgs = new String[]{movieID};

        return mMovieDbHelper.getReadableDatabase().query(
            MovieContract.MovieEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
            );
    }

    private Cursor getMovieWithFlag(Uri uri, String[] projection, String sortOrder ){
        String subMovieFlag = MovieEntry.getSubMovieFlag(uri);
        String[] selectionArgs = new String[]{"1"};
        String selection = "";

        switch(subMovieFlag){
            case "popular":
                selection = popularMovieSelection;
                break;
            case "top_rated":
                selection = topRatedMovieSelection;
                break;
            case "favourite":
                selection = favouriteMovieSelection;
                break;
            default:
                break;
        }

        return mMovieDbHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getReviewByID(Uri uri, String[] projection, String sortOrder){
        String movieID = ReviewEntry.getMovieIDReview(uri);
        String selection = reviewAndMovieIDSelection;
        String[] selectionArgs = new String[]{movieID};

        return mMovieDbHelper.getReadableDatabase().query(ReviewEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
            );
    }

    private Cursor getVideoByID(Uri uri, String[] projection, String sortOrder){
        String movieID = VideoEntry.getMovieIDVideo(uri);
        String selection = videoAndMovieIDSelection;
        String[] selectionArgs = new String[]{movieID};

        return mMovieDbHelper.getReadableDatabase().query(VideoEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
            );
    }

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", SUB_MOVIE);

        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", REVIEW_WITH_MOVIE_ID);

        matcher.addURI(authority, MovieContract.PATH_VIDEO, VIDEO);
        matcher.addURI(authority, MovieContract.PATH_VIDEO + "/#", VIDEO_WITH_MOVIE_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor returnCursor;
        switch (movieUriMatcher.match(uri)){
    
            case MOVIE_WITH_ID:{
                returnCursor = getMovieByID(uri, projection, sortOrder);
                break;
            }
            case SUB_MOVIE:{
                returnCursor = getMovieWithFlag(uri, projection, sortOrder);
                break;
            }
            case REVIEW_WITH_MOVIE_ID:{
                returnCursor = getReviewByID(uri, projection, sortOrder);
                break;
            }
            case VIDEO_WITH_MOVIE_ID:{
                returnCursor = getVideoByID(uri, projection, sortOrder);
                break;
            }
            case MOVIE:{
                returnCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEW:{
                returnCursor = mMovieDbHelper.getReadableDatabase().query(
                    ReviewEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                    );
                break;
            }
            case VIDEO:{
                returnCursor = mMovieDbHelper.getReadableDatabase().query(
                    VideoEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                    );
                break;
            }
            default:
                throw new UnsupportedOperationException(R.string.unknown_uri + uri.toString());
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = movieUriMatcher.match(uri);

        switch (match){
            case MOVIE:
            case SUB_MOVIE:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case REVIEW:
            case REVIEW_WITH_MOVIE_ID:
                return ReviewEntry.CONTENT_TYPE;
            case VIDEO:
            case VIDEO_WITH_MOVIE_ID:
                return VideoEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException(R.string.unknown_uri + uri.toString());
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = movieUriMatcher.match(uri);
        Uri returnUri;

        switch(match){
            case MOVIE:{
                long _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException(R.string.insert_throw + uri.toString());
                break;
            }
            case REVIEW:{
                long _id = db.insert(ReviewEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException(R.string.insert_throw + uri.toString());
                break;
            }
            case VIDEO:{
                long _id = db.insert(VideoEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = VideoEntry.buildVideoUri(_id);
                else
                    throw new android.database.SQLException(R.string.insert_throw + uri.toString());
                break;
            }
            default:
                throw new UnsupportedOperationException(R.string.unknown_uri + uri.toString());
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = movieUriMatcher.match(uri);
        int rowsDeleted;

        if(null == selection) selection = "1";
        switch(match){
            case MOVIE:
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VIDEO:
                rowsDeleted = db.delete(VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(R.string.unknown_uri + uri.toString());
        }
        //careful with rowsDeleted being null, it might causes the table to delete all rows
        if(rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = movieUriMatcher.match(uri);
        int rowsUpdated;

        switch(match){
            case MOVIE:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case VIDEO:
                rowsUpdated = db.update(VideoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(R.string.unknown_uri + uri.toString());
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = movieUriMatcher.match(uri);
        int returnCount = 0;
        switch(match){
            case MOVIE:{
                db.beginTransaction();
                try{
                    for(ContentValues value : values){
                        long _id = db.insert(MovieEntry.TABLE_NAME, null, value);
                        if(_id != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally{
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case REVIEW:{
                db.beginTransaction();
                try{
                    for(ContentValues value : values){
                        long _id = db.insert(ReviewEntry.TABLE_NAME, null, value);
                        if(_id != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally{
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case VIDEO:{
                db.beginTransaction();
                try{
                    for(ContentValues value : values){
                        long _id = db.insert(VideoEntry.TABLE_NAME, null, value);
                        if(_id != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally{
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }
}