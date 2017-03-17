package com.tituy.popularmovie.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;
import com.tituy.popularmovie.BuildConfig;
import com.tituy.popularmovie.R;
import com.tituy.popularmovie.adapter.TrailerAdapter;
import com.tituy.popularmovie.database.MovieContract;
import com.tituy.popularmovie.model.MovieVideo;
import com.tituy.popularmovie.model.MovieVideoResponse;
import com.tituy.popularmovie.rest.TmdbApiClient;
import com.tituy.popularmovie.rest.TmdbApiInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by txb on 2016/11/08.
 */

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MovieDetailFragment.class.getSimpleName();
    private static final String ARGS_MOVIE_ID = "args_movie_id";
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;
    private static final int VIDEO_TRAILER_LOADER = 0;
    private static final String REVIEW_DIALOG_TAG = "review_dialog_tag";

    private Cursor mCursor;
    private int mMovieId;
    private TrailerAdapter trailerAdapter;
    private List<MovieVideo> mMovieVideos;
    private String flag;
    private LinearLayoutManager mLayoutManager;
    private DetailQueryHandler detailQueryHandler;

    @BindView(R.id.original_title)
    TextView originalTitle;
    @BindView(R.id.overview_text)
    TextView overViewText;
    @BindView(R.id.overview)
    TextView overView;
    @BindView(R.id.user_rating)
    TextView userRated;
    @BindView(R.id.release_date)
    TextView releasedDate;
    @BindView(R.id.image_detail)
    ImageView imageThumbnail;
    @BindView(R.id.container_detail_layout)
    LinearLayout mLinearLayout;
    @BindView(R.id.detail_horizon_recycler)
    RecyclerView mTrailerRecyclerView;
    @BindView(R.id.favourite_toggle_button)
    MaterialFavoriteButton favouriteButton;

    public MovieDetailFragment() {
    }

    public static MovieDetailFragment newInstance(int movieId) {
        Bundle args = new Bundle();
        args.putInt(ARGS_MOVIE_ID, movieId);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        Cursor c = getActivity().getContentResolver().query(MovieContract.VideoEntry.buildVideoByIDUri(mMovieId), null, null, null, null);
        if (c == null || c.getCount() <= 0) {
            try {
                updateVideoTrailer(Integer.toString(mMovieId));
            } catch (NullPointerException e) {
                Log.v(TAG, e.toString());
            }
        }
        getLoaderManager().initLoader(VIDEO_TRAILER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        favouriteButton.setOnFavoriteAnimationEndListener(new MaterialFavoriteButton.OnFavoriteAnimationEndListener() {
            @Override
            public void onAnimationEnd(MaterialFavoriteButton buttonView, boolean favorite) {

                if (favorite) {
                    toggleFavouriteHandler(1);
                } else {
                    toggleFavouriteHandler(0);
                    Toast.makeText(getContext(), R.string.removed_favourite, Toast.LENGTH_SHORT).show();
                }
            }
        });

        trailerAdapter = new TrailerAdapter(getContext(), null);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mTrailerRecyclerView.setLayoutManager(mLayoutManager);
        mTrailerRecyclerView.setAdapter(trailerAdapter);

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        try {
            if (getArguments().containsKey(ARGS_MOVIE_ID)) {
                mMovieId = getArguments().getInt(ARGS_MOVIE_ID);
            } else {
                mMovieId = 0;
            }
        } catch (NullPointerException e) {
            Log.v(TAG, e.toString());
        }

        detailQueryHandler = new DetailQueryHandler(getContext().getContentResolver());
        detailQueryHandler.startQuery(1, null, MovieContract.MovieEntry.buildMovieByIDUri(mMovieId), null, null, null, null);
//        mCursor = getContext().getContentResolver().query(MovieContract.MovieEntry.buildMovieByIDUri(mMovieId), null, null, null, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(VIDEO_TRAILER_LOADER, null, this);
        detailQueryHandler.startQuery(1, null, MovieContract.MovieEntry.buildMovieByIDUri(mMovieId), null, null, null, null);
    }

    private void bindMovieDetail() {
        if (mCursor != null && mCursor.moveToFirst()) {
            try {
                flag = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IS_FAVORITE));
                originalTitle.setText(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
                overView.setText(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
                userRated.setText(getString(R.string.rated_string) + " " + mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
                releasedDate.setText(getString(R.string.release_string) + " " + mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE)));
                Picasso.with(getContext()).load(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_URL))).resizeDimen(R.dimen.detail_backdrop_width, R.dimen.detail_backdrop_height).centerCrop().into(imageThumbnail);

                if (flag.equals("1")) {
                    favouriteButton.setFavorite(true);
                    Log.v(TAG, flag + " is 1!!!!!");
                }
            } catch (NullPointerException e) {
                Toast.makeText(getContext(), e.toString() + " bind not working", Toast.LENGTH_SHORT).show();
                Log.v(TAG, e.toString());
            }
        } else {
            Toast.makeText(getContext(), " cursor not working", Toast.LENGTH_SHORT).show();
        }
        overViewText.setText(R.string.overview_string);
        mLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri trailerWithID = MovieContract.VideoEntry.buildVideoByIDUri(mMovieId);
        return new CursorLoader(getContext(), trailerWithID, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        trailerAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        trailerAdapter.swapCursor(null);
    }

    public void updateVideoTrailer(String id) {

        TmdbApiInterface mTmdbApiService = TmdbApiClient.getClient().create(TmdbApiInterface.class);

        Call<MovieVideoResponse> call = mTmdbApiService.getMovieVideo(id, API_KEY);
        call.enqueue(new Callback<MovieVideoResponse>() {
            @Override
            public void onResponse(Call<MovieVideoResponse> call, Response<MovieVideoResponse> response) {
                try {
                    int movieId = response.body().getId();
                    mMovieVideos = response.body().getResults();
                    loadTrailerData((ArrayList<MovieVideo>) mMovieVideos, movieId);
                } catch (NullPointerException e) {
                    Log.v(TAG, e.toString());
                }
            }

            @Override
            public void onFailure(Call<MovieVideoResponse> call, Throwable t) {
                try {
                    Toast.makeText(getActivity(), R.string.error_fetch_data, Toast.LENGTH_SHORT).show();
                } catch (NullPointerException e) {
                    Log.v(TAG, e.toString());
                }
            }
        });
    }

    public void loadTrailerData(ArrayList<MovieVideo> trailerArrayList, int movieId) {
        ContentValues[] values = new ContentValues[trailerArrayList.size()];

        for (int i = 0; i < values.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.VideoEntry.COLUMN_VIDEO_ID, trailerArrayList.get(i).getId());
            contentValues.put(MovieContract.VideoEntry.COLUMN_VIDEO_KEY, trailerArrayList.get(i).getKey());
            contentValues.put(MovieContract.VideoEntry.COLUMN_VIDEO_FOREIGN_KEY, movieId);

            values[i] = contentValues;
        }
        getContext().getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI, values);
    }

    @OnClick(R.id.review_button)
    public void reviewButtonOnClick() {
        ReviewDialogFragment.newInstance(mMovieId).show(getFragmentManager(), REVIEW_DIALOG_TAG);
    }

    private void toggleFavouriteHandler(int flagValue) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, flagValue);
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";
        String[] selectionArgs = new String[]{Integer.toString(mMovieId)};
        try {
            getActivity().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, values, selection, selectionArgs);
        } catch (NullPointerException e) {
            Log.v(TAG, e.toString());
        }
    }

    public class DetailQueryHandler extends AsyncQueryHandler {
        public DetailQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            mCursor = cursor;
            bindMovieDetail();
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            super.onInsertComplete(token, cookie, uri);
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            super.onUpdateComplete(token, cookie, result);
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            super.onDeleteComplete(token, cookie, result);
        }
    }

}
