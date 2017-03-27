package com.tituy.popularmovie.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tituy.popularmovie.database.MovieContract.MovieEntry;
import com.tituy.popularmovie.R;
/**
 * Created by txb on 2016/12/17.
 * Credit to skyfishjy gist:
 * Github gist:  https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the code structure
 */

public class MovieCursorAdapter extends CursorRecyclerViewAdapter<MovieCursorAdapter.MyViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public interface OnItemClickCallBack{
        void onItemClick(int itemId);
    }

    public MovieCursorAdapter(Context context,Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener{
        public ImageView movieImage;
        public TextView movieTitle;

        public MyViewHolder(View view){
            super(view);
            movieImage = (ImageView) view.findViewById(R.id.movie_image);
            movieTitle = (TextView) view.findViewById(R.id.movie_title);
            movieTitle.setOnClickListener(this);
            movieImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mCursor = getCursor();
            mCursor.moveToPosition(getAdapterPosition());
            ((OnItemClickCallBack)mContext).onItemClick(mCursor.getInt(mCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID)));

        }

        public void setData(Cursor cursor){
            Picasso.with(mContext)
                    .load(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_IMAGE_URL)))
                    .placeholder(R.drawable.ic_movie_black_24dp)
                    .error(R.drawable.ic_error_black_24dp)
                    .into(movieImage);
            movieTitle.setText(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE)));
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View movieView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_view, parent, false);
        return new MyViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, Cursor cursor) {
        cursor.moveToPosition(cursor.getPosition());
        holder.setData(cursor);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }
}
