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

public class MovieReview implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("url")
    @Expose
    private String url;
    public final static Parcelable.Creator<MovieReview> CREATOR = new Creator<MovieReview>() {


        @SuppressWarnings({
            "unchecked"
        })
        public MovieReview createFromParcel(Parcel in) {
            MovieReview instance = new MovieReview();
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.author = ((String) in.readValue((String.class.getClassLoader())));
            instance.content = ((String) in.readValue((String.class.getClassLoader())));
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public MovieReview[] newArray(int size) {
            return (new MovieReview[size]);
        }

    }
    ;

    /**
    * No args constructor for use in serialization
    *
    */
    public MovieReview() {
    }

    /**
    *
    * @param content
    * @param id
    * @param author
    * @param url
    */
    public MovieReview(String id, String author, String content, String url) {
        super();
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    /**
    *
    * @return
    * The id
    */
    public String getId() {
        return id;
    }

    /**
    *
    * @param id
    * The id
    */
    public void setId(String id) {
        this.id = id;
    }

    /**
    *
    * @return
    * The author
    */
    public String getAuthor() {
        return author;
    }

    /**
    *
    * @param author
    * The author
    */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
    *
    * @return
    * The content
    */
    public String getContent() {
        return content;
    }

    /**
    *
    * @param content
    * The content
    */
    public void setContent(String content) {
        this.content = content;
    }

    /**
    *
    * @return
    * The url
    */
    public String getUrl() {
        return url;
    }

    /**
    *
    * @param url
    * The url
    */
    public void setUrl(String url) {
        this.url = url;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(author);
        dest.writeValue(content);
        dest.writeValue(url);
    }

    public int describeContents() {
        return 0;
    }

}