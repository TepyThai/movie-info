package com.tituy.popularmovie.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tituy.popularmovie.BuildConfig;
import com.tituy.popularmovie.activity.MainActivity;
import com.tituy.popularmovie.adapter.MovieCursorAdapter;
import com.tituy.popularmovie.database.MovieContract;
import com.tituy.popularmovie.model.Movie;
import com.tituy.popularmovie.model.MovieResponse;
import com.tituy.popularmovie.R;
import com.tituy.popularmovie.rest.TmdbApiClient;
import com.tituy.popularmovie.rest.TmdbApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by txb on 2016/10/18.
 */

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;
    private static final int POPULAR_CURSOR_LOADER_ID = 0;
    private static final int TOP_RATED_CURSOR_LOADER_ID = 1;
    private static final int FAVOURITE_CURSOR_LOADER_ID = 2;
    private static final String ORDER_POPULAR = "popular";
    private static final String ORDER_TOP_RATED = "top_rated";
    private static final String ORDER_FAVOURITE = "favourite";

    //private MovieAdapter mMovieAdapter;
    private MovieCursorAdapter mMovieCursorAdapter;
    private List<Movie> mMovieArrayList;
    private ProgressBar mProgressBar;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public MainActivityFragment() {
    }

    //For changing by popular or top_rated
    public String ORDER_BY = ORDER_POPULAR;
    //For Landscape grid size change
    public Integer GRID_COLUMN = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(savedInstanceState == null || !savedInstanceState.containsKey("orderBy")){
            ORDER_BY = ORDER_POPULAR;
        }else{
            ORDER_BY = savedInstanceState.getString("orderBy");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("orderBy", ORDER_BY);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_activity_main, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefresh);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mMovieArrayList = new ArrayList<>();

        mProgressBar.setVisibility(View.VISIBLE);

        mMovieCursorAdapter = new MovieCursorAdapter(getContext(), null);

        mLayoutManager = new GridLayoutManager(getContext(), GRID_COLUMN);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mMovieCursorAdapter);


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        onOrderChange(ORDER_BY, POPULAR_CURSOR_LOADER_ID);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.popular:
                onOrderChange(ORDER_POPULAR, POPULAR_CURSOR_LOADER_ID);
                return true;
            case R.id.top_rated:
                onOrderChange(ORDER_TOP_RATED, TOP_RATED_CURSOR_LOADER_ID);
                return true;
            case R.id.favourite:
                ORDER_BY = ORDER_FAVOURITE;
                Uri subMovieUri = MovieContract.MovieEntry.buildSubMovieUri(ORDER_BY);
                Cursor cursor = getActivity().getContentResolver().query(subMovieUri,null, null, null, null);
                Log.v(TAG, subMovieUri.toString());
                try {
                    if(cursor == null || cursor.getCount() == 0){
                        Toast.makeText(getContext(), R.string.empty_favourite, Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                }catch (NullPointerException e){
                    Log.v(TAG, e.toString());
                }
                getLoaderManager().initLoader(FAVOURITE_CURSOR_LOADER_ID, null, this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateAsyncTaskDownloader(final String order){
        TmdbApiInterface mTmdbApiService = TmdbApiClient.getClient().create(TmdbApiInterface.class);

        Call<MovieResponse> call = mTmdbApiService.getPopularMovies(order, API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                mMovieArrayList = response.body().getResults();
                loadMovieData((ArrayList<Movie>)mMovieArrayList, order);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Log.e(TAG, t.toString());
                try{
                    Toast.makeText(getActivity(), R.string.error_fetch_data, Toast.LENGTH_SHORT).show();
                }catch (NullPointerException e){
                    Log.v(TAG, e.toString());
                }
            }
        });
    }

    public void loadMovieData(ArrayList<Movie> movieArrayList, String order){
        ContentValues[] values = new ContentValues[movieArrayList.size()];

        int popularFlag = 0;
        int topRatedFlag = 0;
        int isFavouriteFlag = 0;

        switch (order){
            case ORDER_POPULAR:
                popularFlag = 1;
                break;
            case ORDER_TOP_RATED:
                topRatedFlag = 1;
                break;
            default:
                break;
        }
        for (int i = 0; i < values.length; i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieArrayList.get(i).getId());
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieArrayList.get(i).getTitle());
            contentValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_URL, movieArrayList.get(i).getImageUrl());
            contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieArrayList.get(i).getOverview());
            contentValues.put(MovieContract.MovieEntry.COLUMN_DATE, movieArrayList.get(i).getDate());
            contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieArrayList.get(i).getVoteAverage());
            contentValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, isFavouriteFlag);
            contentValues.put(MovieContract.MovieEntry.COLUMN_IS_POPULAR, popularFlag);
            contentValues.put(MovieContract.MovieEntry.COLUMN_IS_TOP_RATED, topRatedFlag);
            contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE_URL, movieArrayList.get(i).getBackdropPath());
            values[i] = contentValues;
        }
        getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, values);
    }

    //loader here
    @Override
    public void onResume() {
        super.onResume();

        switch (ORDER_BY){
            case ORDER_POPULAR:
                getLoaderManager().restartLoader(POPULAR_CURSOR_LOADER_ID, null, this);
                break;
            case ORDER_TOP_RATED:
                getLoaderManager().restartLoader(TOP_RATED_CURSOR_LOADER_ID, null, this);
                break;
            case ORDER_FAVOURITE:
                getLoaderManager().restartLoader(FAVOURITE_CURSOR_LOADER_ID, null, this);
                mProgressBar.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri subMovie = null;
        switch (id){
            case POPULAR_CURSOR_LOADER_ID:
                subMovie = MovieContract.MovieEntry.buildSubMovieUri(ORDER_POPULAR);
                break;
            case TOP_RATED_CURSOR_LOADER_ID:
                subMovie = MovieContract.MovieEntry.buildSubMovieUri(ORDER_TOP_RATED);
                break;
            case FAVOURITE_CURSOR_LOADER_ID:
                subMovie = MovieContract.MovieEntry.buildSubMovieUri(ORDER_FAVOURITE);
        }
        return new CursorLoader(getActivity(), subMovie, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        switch (loader.getId()){
//            case POPULAR_CURSOR_LOADER_ID:
//                cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.buildSubMovieUri(ORDER_POPULAR),null, null, null, null);
//                break;
//            case TOP_RATED_CURSOR_LOADER_ID:
//                cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.buildSubMovieUri(ORDER_TOP_RATED),null, null, null, null);
//                break;
//            case FAVOURITE_CURSOR_LOADER_ID:
//                cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.buildSubMovieUri(ORDER_FAVOURITE), null, null, null, null);
//        }
        mMovieCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieCursorAdapter.swapCursor(null);
    }

    private void onOrderChange(String orderBy, int loaderID){
        ORDER_BY = orderBy;
        Uri subMovieUri = MovieContract.MovieEntry.buildSubMovieUri(ORDER_BY);
        Cursor cursor = getActivity().getContentResolver().query(subMovieUri,null, null, null, null);
        Log.v(TAG, subMovieUri.toString());
        try {
            if(cursor == null || cursor.getCount() == 0){
                updateAsyncTaskDownloader(ORDER_BY);
            }else {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }catch (NullPointerException e){
            Log.v(TAG, e.toString());
        }
        getLoaderManager().initLoader(loaderID, null, this);
    }
}
