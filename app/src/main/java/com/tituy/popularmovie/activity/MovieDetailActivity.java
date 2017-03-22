package com.tituy.popularmovie.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.tituy.popularmovie.R;
import com.tituy.popularmovie.adapter.TrailerAdapter;

/**
 * Created by txb on 2016/10/18.
 */

public class MovieDetailActivity extends BaseActivity implements TrailerAdapter.trailerClickHandler{

    private static final String YOUTUBE_WEB_INTENT = "http://www.youtube.com/watch?v=";
    private static final String YOUTUBE_APP_INTENT = "vnd.youtube:";
    private int mItemId;
    Intent movieIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

//        mCollapsingToolbarLayout.setTitle("dfadfdsf");
        getCollapsingToolbar();

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
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            // Respond to the action bar's Up/Home button
//            case android.R.id.home:
//                Intent upIntent = NavUtils.getParentActivityIntent(this);
//                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
//                    // This activity is NOT part of this app's task, so create a new task
//                    // when navigating up, with a synthesized back stack.
//                    TaskStackBuilder.create(this)
//                            // Add all of this activity's parents to the back stack
//                            .addNextIntentWithParentStack(upIntent)
//                            // Navigate up to the closest parent
//                            .startActivities();
//                } else {
//                    // This activity is part of this app's task, so simply
//                    // navigate up to the logical parent activity.
//                    NavUtils.navigateUpTo(this, upIntent);
//                }
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
