package com.example.android.populatmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.populatmovies.data.MovieContract;
import com.example.android.populatmovies.utilities.ApiClient;
import com.example.android.populatmovies.utilities.ApiInterface;
import com.example.android.populatmovies.utilities.Movie;
import com.example.android.populatmovies.utilities.Review;
import com.example.android.populatmovies.utilities.ReviewAdapter;
import com.example.android.populatmovies.utilities.ReviewList;
import com.example.android.populatmovies.utilities.Trailer;
import com.example.android.populatmovies.utilities.TrailerAdapter;
import com.example.android.populatmovies.utilities.TrailerList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by krypt on 09/02/2017.
 */

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerItemClickListener {

    @BindView(R.id.movite_title)
    TextView title;
    @BindView(R.id.detail_movie_image_view)
    ImageView movieImage;
    @BindView(R.id.movie_overview)
    TextView overview;
    @BindView(R.id.movie_release)
    TextView release;
    @BindView(R.id.movie_rating)
    TextView rating;
    @BindView(R.id.favorite_button)
    FloatingActionButton favorite;
    @BindView(R.id.unfavorite_button)
    FloatingActionButton unFavorite;

    ArrayList<Trailer> trailerArrayList;
    public List<Review> reviews;
    private ReviewAdapter reviewAdapter;
    private static final int NUM_LIST_ITEMS = 100;
    public List<Trailer> trailers;
    private Movie movie;
    private static String API_KEY = "0e9cd8218337c6909070f6bbdfe4dd83";



    @Override
    protected void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);


        final TrailerAdapter.TrailerItemClickListener listener = this;
        trailerArrayList = new ArrayList<>();
        updateFavoriteButton();

        Bundle bundle = getIntent().getExtras();
        final Movie movie = bundle.getParcelable("MOVIE_INFO");

        final int mId = movie.getId();
        String id = String.valueOf(mId);

        final RecyclerView trailerRecyclerView = (RecyclerView) findViewById(R.id.trailer_recycler_view);
        trailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trailerRecyclerView.setHasFixedSize(true);

        final RecyclerView reviewRecycleView = (RecyclerView) findViewById(R.id.review_recycler_view);
        reviewRecycleView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecycleView.setHasFixedSize(true);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        final Context context = this;

        ButterKnife.bind(this);

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavorite(v, mId, movie);
            }
        });

        unFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFavorite(movie);
            }
        });

        Call<ReviewList> callReview = apiInterface.getReviews(id, API_KEY);
        callReview.enqueue(new Callback<ReviewList>() {
            @Override
            public void onResponse(Call<ReviewList> call, Response<ReviewList> response) {
                List<Review> review = response.body().getReviews();
                reviewRecycleView.setAdapter(new ReviewAdapter(review, getApplicationContext()));

            }

            @Override
            public void onFailure(Call<ReviewList> call, Throwable t) {
                Log.e(TAG, "ERROR + " + t.toString());
            }
        });

        Call<TrailerList> callTrailer = apiInterface.getTrailers(id, API_KEY);
        callTrailer.enqueue(new Callback<TrailerList>() {
            @Override
            public void onResponse(Call<TrailerList> call, Response<TrailerList> response) {
                List<Trailer> trailer = response.body().getTrailers();
                trailerRecyclerView.setAdapter(new TrailerAdapter(getApplicationContext(), NUM_LIST_ITEMS, trailer, listener));

                for (Trailer singleTrailer : response.body().getTrailers()) {
                    trailerArrayList.add(singleTrailer);
                }

            }

            @Override
            public void onFailure(Call<TrailerList> call, Throwable t) {
                Log.e(TAG, "ERROR + " + t.toString());

            }
        });

        Picasso.with(context).load("http://image.tmdb.org/t/p/w342/" + movie.getPoster()).into(movieImage);

        title.setText(movie.getTitle());
        overview.setText(movie.getSynopsis());
        rating.setText("Rating : " + String.valueOf(movie.getVote()) + " /10");



    }

    @Override
    public void onTrailerItemClick(int clickedTrailerIndex) {
        Context context = getApplicationContext();
        String key = trailerArrayList.get(clickedTrailerIndex).getKey();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(key)));

    }

    public void addToFavorite (View v, int id, final Movie movie){
        final int movieId = id;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (!isFavorite()){
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MovieEntry.COLUMN_ID, movieId);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_COVER, movie.getBackdrop());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_DATE, movie.getReleaseDate());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPoster());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getVote());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movie.getSynopsis());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_POPULAR, 0);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_TOP_RATED, 0);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);

                    Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                updateFavoriteButton();
            }
        }.execute();
    }

    public void deleteFavorite(final Movie movie){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (isFavorite()){
                    getApplicationContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COLUMN_ID + " = " + movie.getId(),
                            null);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                updateFavoriteButton();
            }
        }.execute();
    }

    private boolean isFavorite(){
        Bundle b = getIntent().getExtras();
        Movie movie = b.getParcelable("MOVIE_INFO");

        Cursor favoriteCursor = getApplicationContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_ID},
                MovieContract.MovieEntry.COLUMN_ID + " = " + movie.getId(),
                null,
                null);

        if(favoriteCursor != null && favoriteCursor.moveToFirst()){
            favoriteCursor.close();
            return true;
        } else {
            return false;
        }
    }

    public void updateFavoriteButton(){
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return isFavorite();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if(!isFavorite()){
                    favorite.setVisibility(View.VISIBLE);
                    unFavorite.setVisibility(View.GONE);
                } else {
                    favorite.setVisibility(View.GONE);
                    unFavorite.setVisibility(View.VISIBLE);

                }
            }
        }.execute();
    }
}
