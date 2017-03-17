package com.tituy.popularmovie.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by txb on 2016/11/15.
 */

public class TmdbApiClient {

    //define base url for the movie api
    public static final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/";
    private static Retrofit sRetrofit = null;
    //create api client using retrofit builder method
    public static Retrofit getClient() {
        if(sRetrofit == null){
            sRetrofit = new Retrofit.Builder()
                    .baseUrl(MOVIE_DB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return sRetrofit;
    }
}
