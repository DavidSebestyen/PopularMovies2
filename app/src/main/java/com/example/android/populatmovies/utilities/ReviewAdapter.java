package com.example.android.populatmovies.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.populatmovies.R;

import java.util.List;

/**
 * Created by krypt on 28/03/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<Review> review;
    private int rowLayout;

    public ReviewAdapter (List<Review> reviews, Context context){
        review = reviews;
        this.context = context;
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        LinearLayout reviewLayout;
        TextView review;
        TextView author;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            reviewLayout = (LinearLayout) itemView.findViewById(R.id.review_layout);
            review = (TextView) itemView.findViewById(R.id.movie_review_text);
            author = (TextView) itemView.findViewById(R.id.review_author);
        }
    }

    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        int layoutIdForListItem = R.layout.review_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        ReviewViewHolder viewHolder = new ReviewViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewViewHolder holder, int position) {

        Review reviews = review.get(position);
        holder.review.setText(reviews.getContent());
        holder.author.setText(reviews.getAuthor());

    }

    @Override
    public int getItemCount() {
        return review.size();
    }
}
