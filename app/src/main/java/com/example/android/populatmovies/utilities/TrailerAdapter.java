package com.example.android.populatmovies.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.populatmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by krypt on 28/03/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    List<Trailer> mTrailer;
    private Context context;
    private int mNumItems;
    final private TrailerItemClickListener mOnClickListener;


    public interface TrailerItemClickListener{
        void onTrailerItemClick(int clickedTrailerIndex);
    }

    public TrailerAdapter (Context context, int numOfItems, List<Trailer> trailer, TrailerItemClickListener listener){
        this.context = context;
        mNumItems = numOfItems;
        mTrailer = trailer;
        mOnClickListener = listener;
    }
    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        TrailerViewHolder viewHolder = new TrailerViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerViewHolder holder, int position) {

        Trailer trailer = mTrailer.get(position);

        ImageView trailerImage = holder.trailerImage;

        Picasso.with(context).load(trailer.getTrailerImage()).into(trailerImage);

        holder.trailerTitle.setText(trailer.getVideoTitle());

    }

    @Override
    public int getItemCount() {
        mNumItems = mTrailer.size();
        return mNumItems;
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private String mItem;
        LinearLayout trailerLayout;
        TextView trailerTitle;
        ImageView trailerImage;

        public TrailerViewHolder(View itemView){
            super(itemView);

            trailerLayout = (LinearLayout) itemView.findViewById(R.id.trailer_layout);
            trailerTitle = (TextView) itemView.findViewById(R.id.trailer_title);
            trailerImage = (ImageView) itemView.findViewById(R.id.trailer_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();

            mOnClickListener.onTrailerItemClick(clickedPosition);

        }
    }
}
