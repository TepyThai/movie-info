package com.tituy.popularmovie.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tituy.popularmovie.database.MovieContract;
import com.tituy.popularmovie.R;

/**
 * Created by txb on 2016/12/27.
 */

public class TrailerAdapter extends CursorRecyclerViewAdapter<TrailerAdapter.TrailerHolderView> {

    private Context mContext;
    private static final String YOUTUBE_THUMBNAIL_PREFIX = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_THUMBNAIL_SUFFIX = "/hqdefault.jpg";

    private String trailerID;

    public TrailerAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    public interface trailerClickHandler{
        void onTrailerClick(String id);
    }

    public class TrailerHolderView extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener{
        ImageView trailerThumbnail;

        public TrailerHolderView(View view) {
            super(view);
            trailerThumbnail = (ImageView)view.findViewById(R.id.movie_trailer_thumbnail);
            trailerThumbnail.setOnClickListener(this);
        }

        public void setData(Cursor cursor){
            trailerID = cursor.getString(cursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_VIDEO_KEY));
            Picasso.with(mContext).load(YOUTUBE_THUMBNAIL_PREFIX +
                    trailerID +
                    YOUTUBE_THUMBNAIL_SUFFIX)
                    .resizeDimen(R.dimen.trailer_width, R.dimen.trailer_height)
                    .placeholder(R.drawable.ic_movie_black_24dp)
                    .error(R.drawable.ic_error_black_24dp)
                    .into(trailerThumbnail);
        }

        @Override
        public void onClick(View view) {
            ((trailerClickHandler)mContext).onTrailerClick(trailerID);
        }


    }

    @Override
    public TrailerHolderView onCreateViewHolder(ViewGroup parent, int viewType) {
        View trailerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_trailer_view, parent, false);
        return new TrailerHolderView(trailerView);
    }


    @Override
    public void onBindViewHolder(TrailerHolderView viewHolder, Cursor cursor) {
        cursor.moveToPosition(cursor.getPosition());
        viewHolder.setData(cursor);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }
}
