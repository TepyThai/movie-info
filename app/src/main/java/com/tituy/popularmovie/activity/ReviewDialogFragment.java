package com.tituy.popularmovie.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.tituy.popularmovie.BuildConfig;
import com.tituy.popularmovie.adapter.ReviewCursorAdapter;
import com.tituy.popularmovie.database.MovieContract;
import com.tituy.popularmovie.model.MovieReview;
import com.tituy.popularmovie.model.MovieReviewResponse;
import com.tituy.popularmovie.R;
import com.tituy.popularmovie.rest.TmdbApiClient;
import com.tituy.popularmovie.rest.TmdbApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by txb on 2016/12/28.
 */

public class ReviewDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ReviewDialogFragment.class.getSimpleName();
    private static final String ARGS_MOVIE_ID = "args_movie_id";
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;
    private static final int REVIEW_LOADER = 0;

    private int mMovieId;
    private List<MovieReview> mMovieReviews;
    private ReviewCursorAdapter mReviewCursorAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private RecyclerView mRecyclerView;

    public static ReviewDialogFragment newInstance(int movieId) {
        Bundle arguments = new Bundle();
        arguments.putInt(ARGS_MOVIE_ID, movieId);
        ReviewDialogFragment fragment = new ReviewDialogFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.review_dialog, null);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.review_dialog_recycler_view);

        mReviewCursorAdapter = new ReviewCursorAdapter(getContext(), null);
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mReviewCursorAdapter);

        builder.setView(rootView);
        builder.setTitle(R.string.review_title);
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Cursor c = getContext().getContentResolver().query(MovieContract.ReviewEntry.buildReviewByIDUri(mMovieId)
                , null
                , null
                , null
                , null);
        if(c == null || c.getCount() == 0){
            try {
                updateReviewDialog(Integer.toString(mMovieId));
            }catch (NullPointerException e){
                Log.v(TAG, e.toString());
            }
        }else {
            Log.v(TAG, MovieContract.ReviewEntry.buildReviewByIDUri(mMovieId).toString());
        }
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        try {
            if (getArguments().containsKey(ARGS_MOVIE_ID))
                mMovieId = getArguments().getInt(ARGS_MOVIE_ID);
            else
                mMovieId = 0;
        } catch (NullPointerException e) {
            Log.v(TAG, e.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(REVIEW_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri reviewWithID = MovieContract.ReviewEntry.buildReviewByIDUri(mMovieId);
        return new CursorLoader(getContext(), reviewWithID, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor = getContext().getContentResolver().query(MovieContract.ReviewEntry.buildReviewByIDUri(mMovieId), null, null, null, null);
        mReviewCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mReviewCursorAdapter.swapCursor(null);
    }

    public void updateReviewDialog(String id) {
        TmdbApiInterface mTmdbApiService = TmdbApiClient.getClient().create(TmdbApiInterface.class);

        Call<MovieReviewResponse> call = mTmdbApiService.getMovieReview(id, API_KEY);
        call.enqueue(new Callback<MovieReviewResponse>() {
            @Override
            public void onResponse(Call<MovieReviewResponse> call, Response<MovieReviewResponse> response) {
                try {
                    int movieId = response.body().getId();
                    mMovieReviews = response.body().getResults();
                    loadReviewData((ArrayList<MovieReview>) mMovieReviews, movieId);
                } catch (NullPointerException e) {
                    Log.v(TAG, e.toString());
                }
            }

            @Override
            public void onFailure(Call<MovieReviewResponse> call, Throwable t) {
                try{
                    Toast.makeText(getActivity(), R.string.error_fetch_data, Toast.LENGTH_SHORT).show();
                }catch (NullPointerException e){
                    Log.v(TAG, e.toString());
                }
            }
        });
    }

    public void loadReviewData(ArrayList<MovieReview> movieReviews, int movieId) {
        ContentValues[] values = new ContentValues[movieReviews.size()];

        for (int i = 0; i < values.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, movieReviews.get(i).getId());
            contentValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, movieReviews.get(i).getAuthor());
            contentValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, movieReviews.get(i).getContent());
            contentValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_FOREIGN_KEY, movieId);

            values[i] = contentValues;
        }
        getContext().getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, values);
    }
}
