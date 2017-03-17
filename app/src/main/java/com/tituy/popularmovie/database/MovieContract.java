package com.tituy.popularmovie.database;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by txb on 2016/11/17.
 */

public class MovieContract{

    //content authority
    public static final String CONTENT_AUTHORITY = "com.tituy.popularmovie";
    //base content Uri
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //Uri path
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_VIDEO = "video";

    //table contents for movie and favorite table
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            +"/"+ CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE 
            +"/"+ CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        //define Table name
        public static final String TABLE_NAME = "movie";

        //Foreign Key column
        public static final String COLUMN_MOVIE_ID = "movie_id";
        //define Table Columns
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";
        public static final String COLUMN_IS_POPULAR = "is_popular";
        public static final String COLUMN_IS_TOP_RATED = "is_top_rated";

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieByIDUri(int movieID){
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(movieID)).build();
        }

        public static Uri buildSubMovieUri(String flag){
            return CONTENT_URI.buildUpon().appendPath(flag).build();
        }

        public static String getSubMovieFlag(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static String getMovieID(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static final class ReviewEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE 
            +"/"+ CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE 
            +"/"+ CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        //define Table name
        public static final String TABLE_NAME = "review";
        //define Table Columns
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_REVIEW_AUTHOR = "author";
        public static final String COLUMN_REVIEW_CONTENT = "review_content";

        //define foregin key
        public static final String COLUMN_REVIEW_FOREIGN_KEY = "review_foreign_key";

        public static Uri buildReviewUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildReviewByIDUri(int movieID){
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(movieID)).build();
        }
        public static String getMovieIDReview(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

    public static final class VideoEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_VIDEO).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                +"/"+ CONTENT_AUTHORITY + "/" + PATH_VIDEO;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                +"/"+ CONTENT_AUTHORITY + "/" + PATH_VIDEO;

        //define Video Table
        public static final String TABLE_NAME =  "video";
        //define table columns
        public static final String COLUMN_VIDEO_ID  = "video_id";
        public static final String COLUMN_VIDEO_KEY = "video_key";
        //define foregin key
        public static final String COLUMN_VIDEO_FOREIGN_KEY = "video_foreign_key";

        public static Uri buildVideoUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildVideoByIDUri(int movieID){
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(movieID)).build();
        }

        public static String getMovieIDVideo(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

}