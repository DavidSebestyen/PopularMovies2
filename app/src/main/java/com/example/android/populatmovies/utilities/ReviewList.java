package com.example.android.populatmovies.utilities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by krypt on 28/03/2017.
 */

public class ReviewList {
    // List of results ("results")
    @SerializedName("results")
    private List<Review> results;

    public List<Review> getReviews() {
        return results;
    }

    public void setReviews(List<Review> results) {
        this.results = results;
    }
}
