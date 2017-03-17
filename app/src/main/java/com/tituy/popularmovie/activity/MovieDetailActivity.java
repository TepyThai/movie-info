package com.tituy.popularmovie.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tituy.popularmovie.adapter.TrailerAdapter;
import com.tituy.popularmovie.R;

/**
 * Created by txb on 2016/10/18.
 */

public class MovieDetailActivity extends AppCompatActivity implements TrailerAdapter.trailerClickHandler{

    private static final String YOUTUBE_WEB_INTENT = "http://www.youtube.com/watch?v=";
    private static final String YOUTUBE_APP_INTENT = "vnd.youtube:";
    private int mItemId;
    private Intent movieIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movieIntent = getIntent();
        mItemId = movieIntent.getIntExtra(MainActivity.MOVIE_INTENT_STRING, 0);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, MovieDetailFragment.newInstance(mItemId))
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int intentResultString = Activity.RESULT_OK;
            Intent getIntent = new Intent();
            getIntent.putExtra(MainActivity.MOVIE_INTENT_STRING, mItemId);
            setResult(intentResultString, getIntent);
            finish();
        }
    }

    @Override
    public void onTrailerClick(String id) {
        youtubeIntent(id);
    }

    public void youtubeIntent(String youtubeID){
        Uri appUri = Uri.parse(YOUTUBE_APP_INTENT + youtubeID);
        Uri webUri = Uri.parse(YOUTUBE_WEB_INTENT + youtubeID);
        Intent youtubeAppIntent = new Intent(Intent.ACTION_VIEW, appUri);
        Intent youtubeWebIntent = new Intent(Intent.ACTION_VIEW, webUri);

        try {
            startActivity(youtubeAppIntent);
        }catch (ActivityNotFoundException e){
            if(youtubeWebIntent.resolveActivity(getPackageManager()) != null){
                startActivity(youtubeWebIntent);
            }else {
                Toast.makeText(getBaseContext(), getString(R.string.no_app_found), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
