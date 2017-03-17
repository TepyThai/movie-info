package com.tituy.popularmovie.rest;

import com.tituy.popularmovie.model.MovieResponse;
import com.tituy.popularmovie.model.MovieReviewResponse;
import com.tituy.popularmovie.model.MovieVideoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by txb on 2016/11/15.
 */

public interface TmdbApiInterface {
    String API_ID = "api_key";
    String ORDER = "order";
    String ID = "id";

    @GET("movie/{order}")
    Call<MovieResponse> getPopularMovies(@Path(ORDER) String order, @Query(API_ID) String apiKey);
    @GET("movie/{id}/videos")
    Call<MovieVideoResponse> getMovieVideo(@Path(ID) String id, @Query(API_ID) String apiKey);
    @GET("movie/{id}/reviews")
    Call<MovieReviewResponse> getMovieReview(@Path(ID) String id, @Query(API_ID) String apiKey);
}
