package com.tituy.popularmovie.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.tituy.popularmovie.R;
import com.tituy.popularmovie.adapter.MovieCursorAdapter;
import com.tituy.popularmovie.adapter.TrailerAdapter;
import com.tituy.popularmovie.fragment.MovieDetailFragment;

public class MainActivity extends BaseActivity implements MovieCursorAdapter.OnItemClickCallBack, TrailerAdapter.trailerClickHandler{

    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "MOVIE_DETAIL_TAG";
    private static final int REQUEST_CODE_INTENT = 1;
    public static final String MOVIE_INTENT_STRING = "MovieObject";
    private static final String YOUTUBE_WEB_INTENT = "http://www.youtube.com/watch?v=";
    private static final String YOUTUBE_APP_INTENT = "vnd.youtube:";

    public static boolean dualScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getToolbar();
        getToolbar(getResources().getString(R.string.app_name));
        //getDrawerBuilder(this);
        if(findViewById(R.id.movie_detail_container) != null){

            //in Dual screens mode
            dualScreen = true;
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), MOVIE_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        }else{
            dualScreen = false;
        }

    }

    @Override
    public void onItemClick(int itemId) {
        if(dualScreen){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container,  MovieDetailFragment.newInstance(itemId), MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();
        }else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MOVIE_INTENT_STRING, itemId);
            startActivityForResult(intent, REQUEST_CODE_INTENT);
        }
    }

    @Override
    public void onTrailerClick(String id) {
        youtubeIntent(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTENT && resultCode == RESULT_OK){
            int itemIdResult = data.getIntExtra(MOVIE_INTENT_STRING, 0);
            onItemClick(itemIdResult);
        }
    }

    public void youtubeIntent(String youtubeID){
        Uri appUri = Uri.parse(YOUTUBE_APP_INTENT + youtubeID);
        Uri webUri = Uri.parse(YOUTUBE_WEB_INTENT + youtubeID);
        Intent youtubeAppIntent = new Intent(Intent.ACTION_VIEW, appUri);
        Intent youtubeWebIntent = new Intent(Intent.ACTION_VIEW, webUri);

        try {
            startActivity(youtubeAppIntent);
        }catch (ActivityNotFoundException e){
            startActivity(youtubeWebIntent);
        }
    }
}
