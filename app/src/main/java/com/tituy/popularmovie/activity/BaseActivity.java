package com.tituy.popularmovie.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.tituy.popularmovie.R;

/**
 * Created by txb on 2017/03/21.
 */

public class BaseActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Toolbar mCollapsedToolbar;

    protected Toolbar getToolbar(String title) {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
                mToolbar.setTitle(title);
            }
        }
        return mToolbar;
    }

    protected Toolbar getCollapsingToolbar() {

        if(mCollapsedToolbar == null){
            mCollapsedToolbar = (Toolbar)findViewById(R.id.collapsed_toolbar);
            if(mCollapsedToolbar != null){
                setSupportActionBar(mCollapsedToolbar);
                ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle("dfadf");
            }
        }
        return mCollapsedToolbar;
    }
}
