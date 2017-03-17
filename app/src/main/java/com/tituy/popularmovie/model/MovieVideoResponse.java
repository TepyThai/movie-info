package com.tituy.popularmovie.model;

/**
 * Created by txb on 2016/12/07.
 * using Auto Pojo generated code from
 * http://www.jsonschema2pojo.org/
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieVideoResponse implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<MovieVideo> results = null;
    public final static Parcelable.Creator<MovieVideoResponse> CREATOR = new Creator<MovieVideoResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public MovieVideoResponse createFromParcel(Parcel in) {
            MovieVideoResponse instance = new MovieVideoResponse();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.results, (com.tituy.popularmovie.model.MovieVideo.class.getClassLoader()));
            return instance;
        }

        public MovieVideoResponse[] newArray(int size) {
            return (new MovieVideoResponse[size]);
        }

    }
            ;

    /**
     * No args constructor for use in serialization
     *
     */
    public MovieVideoResponse() {
    }

    /**
     *
     * @param id
     * @param results
     */
    public MovieVideoResponse(Integer id, List<MovieVideo> results) {
        super();
        this.id = id;
        this.results = results;
    }

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The results
     */
    public List<MovieVideo> getResults() {
        return results;
    }

    /**
     *
     * @param results
     * The results
     */
    public void setResults(List<MovieVideo> results) {
        this.results = results;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(results);
    }

    public int describeContents() {
        return 0;
    }

}