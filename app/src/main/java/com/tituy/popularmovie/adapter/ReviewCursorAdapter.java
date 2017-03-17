package com.tituy.popularmovie.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tituy.popularmovie.database.MovieContract;
import com.tituy.popularmovie.R;

/**
 * Created by txb on 2016/12/29.
 */

public class ReviewCursorAdapter extends CursorRecyclerViewAdapter<ReviewCursorAdapter.ReviewHolder> {

    private Context mContext;

    public ReviewCursorAdapter(Context context, Cursor cursor){
        super(context, cursor);
        mContext = context;
    }

    public class ReviewHolder extends RecyclerView.ViewHolder{
        public TextView reviewAuthor;
        public TextView reviewContent;

        public ReviewHolder(View view) {
            super(view);
            reviewAuthor = (TextView)view.findViewById(R.id.review_author);
            reviewContent = (TextView)view.findViewById(R.id.review_content);
        }

        public void setData(Cursor cursor){
            reviewAuthor.setText( cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR)));
            reviewContent.setText(cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT)));
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void onBindViewHolder(ReviewHolder viewHolder, Cursor cursor) {
        cursor.moveToPosition(cursor.getPosition());
        viewHolder.setData(cursor);
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View reviewView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_dialog_view, parent, false);
        return new ReviewHolder(reviewView);
    }
}
