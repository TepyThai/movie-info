package com.tituy.popularmovie.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tituy.popularmovie.BuildConfig;
import com.tituy.popularmovie.R;
import com.tituy.popularmovie.activity.MovieDetailActivity;
import com.tituy.popularmovie.adapter.ReviewCursorAdapter;
import com.tituy.popularmovie.adapter.TrailerAdapter;
import com.tituy.popularmovie.database.MovieContract;
import com.tituy.popularmovie.model.MovieReview;
import com.tituy.popularmovie.model.MovieReviewResponse;
import com.tituy.popularmovie.model.MovieVideo;
import com.tituy.popularmovie.model.MovieVideoResponse;
import com.tituy.popularmovie.rest.TmdbApiClient;
import com.tituy.popularmovie.rest.TmdbApiInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    private static final int REVIEW_LOADER = 1;

    LinearLayoutManager mLinearLayoutManager;
    private String flag;
    LinearLayoutManager mLayoutManager;

    private List<MovieReview> mMovieReviews;
    private ReviewCursorAdapter mReviewCursorAdapter;
    private Cursor mCursor;
    private int mMovieId;
    private TrailerAdapter trailerAdapter;
    private List<MovieVideo> mMovieVideos;
    private DetailQueryHandler detailQueryHandler;

    @BindView(R.id.backdrop_image)
    ImageView mBackdropImage;
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
    @BindView(R.id.review_dialog_recycler_view)
    RecyclerView mReviewRecyclerView;
    @BindView(R.id.favourite_fb_icon)
    FloatingActionButton favourite_fb_icon;
    @BindView(R.id.collapsing_toolbar_container)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.review_content_text)
    TextView reviewText;

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

        getTrailerCursor();
        getReviewCursor();

        getLoaderManager().initLoader(VIDEO_TRAILER_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    private void getTrailerCursor(){
        Cursor c = getActivity().getContentResolver().query(MovieContract.VideoEntry.buildVideoByIDUri(mMovieId), null, null, null, null);
        if (c == null || c.getCount() <= 0) {
            try {
                updateVideoTrailer(Integer.toString(mMovieId));
            } catch (NullPointerException e) {
                Log.v(TAG, e.toString());
            }
        }else {
            c.close();
        }
    }

    private void checkReviewNotEmpty(){
        Cursor c = getActivity().getContentResolver().query(MovieContract.ReviewEntry.buildReviewUri(mMovieId), null, null, null, null);
        if((c == null || c.getCount() < 0)){
            reviewText.setText(R.string.no_review_text);
        }else {
            reviewText.setText(R.string.review_title);
            c.close();
        }
    }

    private void getReviewCursor(){
        Cursor c = getContext().getContentResolver().query(MovieContract.ReviewEntry.buildReviewByIDUri(mMovieId)
                , null
                , null
                , null
                , null);
        if(c == null || c.getCount() == 0){
            try {
                updateReviews(Integer.toString(mMovieId));
            }catch (NullPointerException e){
                Log.v(TAG, e.toString());
            }
        }else {
            c.close();
            Log.v(TAG, MovieContract.ReviewEntry.buildReviewByIDUri(mMovieId).toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        favourite_fb_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag.equals("0")){
                    toggleFavouriteHandler(1);
                    favourite_fb_icon.setImageResource(R.drawable.ic_favorite_white_24dp);
                    Toast.makeText(getContext(), R.string.added_favourite, Toast.LENGTH_SHORT).show();
                    flag = "1";
                }else {
                    toggleFavouriteHandler(0);
                    favourite_fb_icon.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    Toast.makeText(getContext(), R.string.removed_favourite, Toast.LENGTH_SHORT).show();
                    flag = "0";
                }
            }
        });

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() instanceof MovieDetailActivity){
                    getActivity().onBackPressed();
                }
            }
        });

        trailerAdapter = new TrailerAdapter(getContext(), null);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mTrailerRecyclerView.setLayoutManager(mLayoutManager);
        mTrailerRecyclerView.setAdapter(trailerAdapter);

        mReviewCursorAdapter = new ReviewCursorAdapter(getContext(), null);
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        mReviewRecyclerView.setLayoutManager(mLinearLayoutManager);
        mReviewRecyclerView.setAdapter(mReviewCursorAdapter);

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
        getLoaderManager().restartLoader(REVIEW_LOADER, null, this);

        detailQueryHandler.startQuery(1, null, MovieContract.MovieEntry.buildMovieByIDUri(mMovieId), null, null, null, null);
    }

    private void bindMovieDetail() {
        if (mCursor != null && mCursor.moveToFirst()) {
            try {
                checkReviewNotEmpty();
                flag = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IS_FAVORITE));

                if(flag.equals("1")){
                    favourite_fb_icon.setImageResource(R.drawable.ic_favorite_white_24dp);
                }else {
                    favourite_fb_icon.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                }

                String originalTitleText = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                mCollapsingToolbarLayout.setTitle(originalTitleText);
                //originalTitle.setText(originalTitleText);
                overView.setText(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
                userRated.setText(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
                releasedDate.setText(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE)));
                Picasso.with(getContext())
                        .load(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_URL)))
                        .resizeDimen(R.dimen.detail_image_width, R.dimen.detail_image_height)
                        .placeholder(R.drawable.ic_movie_black_24dp)
                        .error(R.drawable.ic_error_black_24dp)
                        .into(imageThumbnail);
                Picasso.with(getContext())
                        .load(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE_URL)))
                        .placeholder(R.drawable.ic_movie_black_24dp)
                        .error(R.drawable.ic_error_black_24dp)
                        .fit()
                        .into(mBackdropImage);
            } catch (NullPointerException e) {
                Toast.makeText(getContext(), e.toString() + " bind not working", Toast.LENGTH_SHORT).show();
                Log.v(TAG, e.toString());
            }
        } else {
            Toast.makeText(getContext(), " cursor not working", Toast.LENGTH_SHORT).show();
        }
        mLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri loaderUri = null;
        switch (id){
            case VIDEO_TRAILER_LOADER:
                loaderUri = MovieContract.VideoEntry.buildVideoByIDUri(mMovieId);
                break;
            case REVIEW_LOADER:
                loaderUri = MovieContract.ReviewEntry.buildReviewByIDUri(mMovieId);
                break;
        }
        return new CursorLoader(getContext(), loaderUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()){
            case VIDEO_TRAILER_LOADER:
                trailerAdapter.swapCursor(cursor);
                break;
            case REVIEW_LOADER:
                mReviewCursorAdapter.swapCursor(cursor);
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        trailerAdapter.swapCursor(null);
        mReviewCursorAdapter.swapCursor(null);
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

    public void updateReviews(String id) {
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

    private void toggleFavouriteHandler(int flagValue) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, flagValue);
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";
        String[] selectionArgs = new String[]{Integer.toString(mMovieId)};
        try {
            getActivity().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, values, selection, selectionArgs);
            Toast.makeText(getContext(), flagValue + " added to database!!!", Toast.LENGTH_SHORT).show();
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
