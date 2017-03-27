package com.tituy.popularmovie.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.tituy.popularmovie.R;

/**
 * Created by txb on 2017/03/21.
 */

public class BaseActivity extends AppCompatActivity {

    private Toolbar mCollapsedToolbar;
//    private Drawer mDrawerBuilder;

    protected Toolbar getToolbar(String title) {
        getToolbar().setTitle(title);
        return getToolbar();
    }

    protected Toolbar getToolbar() {

        if(mCollapsedToolbar == null){
            mCollapsedToolbar = (Toolbar)findViewById(R.id.toolbar);
            if(mCollapsedToolbar != null){
                setSupportActionBar(mCollapsedToolbar);
            }
        }
        return mCollapsedToolbar;
    }

//    protected Drawer getDrawerBuilder(AppCompatActivity appCompatActivity){
//        mDrawerBuilder = new DrawerBuilder().withActivity(appCompatActivity).build();
//        return mDrawerBuilder;
//    }
}
