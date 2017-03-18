package com.example.android.populatmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import static com.example.android.populatmovies.data.MovieContract.MovieEntry.CONTENT_URI;
import static com.example.android.populatmovies.data.MovieContract.MovieEntry.TABLE_NAME;

/**
 * Created by krypt on 15/03/2017.
 */

public class MovieContentProvider extends ContentProvider {

    private MovieDbHelper mMovieDbHelper;

    public static final int MOVIE = 100;
    public static final int MOVIE_WITH_ID = 101;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        Context context = getContext();

        mMovieDbHelper = new MovieDbHelper(context);

        return true;
    }

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);

        return uriMatcher;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        final int match = uriMatcher.match(uri);

        Cursor cursor;

        switch (match){
            case MOVIE:
                cursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);

        Uri returnUri;

        switch (match){
            case MOVIE:
                long id = db.insert(TABLE_NAME, null, values);

                if (id > 0){
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert new row to " + uri);
                }

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        final int match = uriMatcher.match(uri);

        int movieDeleted;

        if (null == selection) {
            selection = "1";
        }

        switch (match){
            case MOVIE:
                movieDeleted = db.delete(TABLE_NAME, selection, selectionArgs);

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        return movieDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
