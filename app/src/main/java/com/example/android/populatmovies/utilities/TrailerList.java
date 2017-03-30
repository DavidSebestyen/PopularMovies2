package com.example.android.populatmovies.utilities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by krypt on 28/03/2017.
 */

public class TrailerList {
    // List of results ("results")
    @SerializedName("results")
    private List<Trailer> results;

    public List<Trailer> getTrailers() {
        return results;
    }

    public void setTrailers(List<Trailer> results) {
        this.results = results;
    }
}
