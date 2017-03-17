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

public class MovieReviewResponse implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("results")
    @Expose
    private List<MovieReview> results = null;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;
    public final static Parcelable.Creator<MovieReviewResponse> CREATOR = new Creator<MovieReviewResponse>() {


        @SuppressWarnings({
            "unchecked"
        })
        public MovieReviewResponse createFromParcel(Parcel in) {
            MovieReviewResponse instance = new MovieReviewResponse();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.page = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.results, (com.tituy.popularmovie.model.MovieReview.class.getClassLoader()));
            instance.totalPages = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.totalResults = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public MovieReviewResponse[] newArray(int size) {
            return (new MovieReviewResponse[size]);
        }

    }
    ;

    /**
    * No args constructor for use in serialization
    *
    */
    public MovieReviewResponse() {
    }

    /**
    *
    * @param id
    * @param results
    * @param totalResults
    * @param page
    * @param totalPages
    */
    public MovieReviewResponse(Integer id, Integer page, List<MovieReview> results, Integer totalPages, Integer totalResults) {
        super();
        this.id = id;
        this.page = page;
        this.results = results;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
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
    * The page
    */
    public Integer getPage() {
        return page;
    }

    /**
    *
    * @param page
    * The page
    */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
    *
    * @return
    * The results
    */
    public List<MovieReview> getResults() {
        return results;
    }

    /**
    *
    * @param results
    * The results
    */
    public void setResults(List<MovieReview> results) {
        this.results = results;
    }

    /**
    *
    * @return
    * The totalPages
    */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
    *
    * @param totalPages
    * The total_pages
    */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    /**
    *
    * @return
    * The totalResults
    */
    public Integer getTotalResults() {
        return totalResults;
    }

    /**
    *
    * @param totalResults
    * The total_results
    */
    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(page);
        dest.writeList(results);
        dest.writeValue(totalPages);
        dest.writeValue(totalResults);
    }

    public int describeContents() {
        return 0;
    }

}