package com.example.android.populatmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.populatmovies.data.MovieContract;
import com.example.android.populatmovies.utilities.ApiClient;
import com.example.android.populatmovies.utilities.ApiInterface;
import com.example.android.populatmovies.utilities.Movie;
import com.example.android.populatmovies.utilities.MovieAdapter;
import com.example.android.populatmovies.utilities.MovieCursorAdapter;
import com.example.android.populatmovies.utilities.MovieResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.PosterItemClickListener, LoaderManager.LoaderCallbacks<Cursor>,  MovieCursorAdapter.PosterCursorItemClickListener{


    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private static final int NUM_LIST_ITEMS = 100;

    ArrayList<Movie> moviesArrayList;

    private static String API_KEY = "0e9cd8218337c6909070f6bbdfe4dd83";

    private MovieCursorAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final MovieAdapter.PosterItemClickListener listener = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);

        moviesArrayList = new ArrayList<>();

        ButterKnife.bind(this);

        if (API_KEY.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please obtain your API KEY first from themoviedb.org", Toast.LENGTH_SHORT).show();
            return;
        }

        getTopRated(mRecyclerView);

        final MovieCursorAdapter.PosterCursorItemClickListener listenerCursor = this;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Context context = this;
        ButterKnife.bind(this);
        switch (item.getItemId()){
            case R.id.sort_by_popularity:
                getPopular(mRecyclerView);
                return true;
            case R.id.sort_by_rating:
                getTopRated(mRecyclerView);
                return true;
            case R.id.get_favorites:
                getFavorites(mRecyclerView);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPosterItemClick(int clickedPosterIndex) {
        Context context = getApplicationContext();
        Intent i = new Intent(context, DetailActivity.class);
        i.putExtra("MOVIE_INFO", moviesArrayList.get(clickedPosterIndex));
        startActivity(i);
    }

    @Override
    public void onPosterItemCursorClick(int clickedCursorPosterIndex) {
        Context context = getApplicationContext();
        Intent i = new Intent(context, DetailActivity.class);
        i.putExtra("MOVIE_INFO", moviesArrayList.get(clickedCursorPosterIndex));
        startActivity(i);
    }

    public void getFavorites(RecyclerView mRecyclerView) {
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    public void getTopRated (RecyclerView mRecyclerView){

        final RecyclerView recyclerView = mRecyclerView;

        final MovieAdapter.PosterItemClickListener listener = this;

        mRecyclerView.setVisibility(View.VISIBLE);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<MovieResponse> call = apiService.getTopRatedMovies(API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                final List<Movie> movies = response.body().getResults();
                moviesArrayList.clear();
                List<Movie> moviesResult = movies;
                for (Movie singleMovie : response.body().getResults()) {
                    moviesArrayList.add(singleMovie);
                }
                recyclerView.setAdapter(new MovieAdapter(getApplicationContext(), NUM_LIST_ITEMS, movies, listener));
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("ERROR", t.toString());

            }
        });
    }

    public void getPopular (RecyclerView mRecyclerView){
        final RecyclerView recyclerView = mRecyclerView;

        final MovieAdapter.PosterItemClickListener listener = this;

        mRecyclerView.setVisibility(View.VISIBLE);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<MovieResponse> call = apiService.getPopularMovies(API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                final List<Movie> movies = response.body().getResults();
                moviesArrayList.clear();
                List<Movie> moviesResult = movies;
                for (Movie singleMovie : response.body().getResults()) {
                    moviesArrayList.add(singleMovie);
                }
                recyclerView.setAdapter(new MovieAdapter(getApplicationContext(), NUM_LIST_ITEMS, moviesResult, listener));
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("ERROR", t.toString());

            }
        });

    }

    @Override
    public void onResume() {
        Context context = getApplicationContext();
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(),
                MovieContract.MovieEntry.CONTENT_URI,
                null, "isFavorite = 1", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        MovieCursorAdapter.PosterCursorItemClickListener listenerCursor = null;

        listenerCursor = this;

        if(data.moveToFirst()){
            moviesArrayList.clear();
            data.moveToFirst();

            Movie movie = new Movie(data.getInt(data.getColumnIndex("movie_id")),
                    data.getString(data.getColumnIndex("title")),
                    data.getString(data.getColumnIndex("poster_path")),
                    data.getString(data.getColumnIndex("backdrop_path")),
                    data.getString(data.getColumnIndex("release_date")),
                    data.getDouble(data.getColumnIndex("vote_average")),
                    data.getString(data.getColumnIndex("overview")));
            moviesArrayList.add(movie);

            while (data.moveToNext()) {
                movie = new Movie(data.getInt(data.getColumnIndex("movie_id")),
                        data.getString(data.getColumnIndex("title")),
                        data.getString(data.getColumnIndex("poster_path")),
                        data.getString(data.getColumnIndex("backdrop_path")),
                        data.getString(data.getColumnIndex("release_date")),
                        data.getDouble(data.getColumnIndex("vote_average")),
                        data.getString(data.getColumnIndex("overview")));
                moviesArrayList.add(movie);
            }

            this.mAdapter = new MovieCursorAdapter(getApplicationContext(), data, listenerCursor);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mRecyclerView.setVisibility(View.GONE);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);

    }


}
