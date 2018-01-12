package com.example.fengwei7.timbretuner.DataEntry;

/**
 * Created by fengwei7 on 2016-10-28.
 */
public class DataEntry {
    private String title;
    private String comment;
    private String url;

    // empty constructor

    public DataEntry( String title, String comment, String url) {
        this.title = title;
        this.comment = comment;
        this.url = url;
    }



    // full constructor for reading from database with ID



    // Get/Set Functions for the database
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return this.comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }



    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    // Will be used by the ArrayAdapter in the ListView
    // TODO: Scale this to more than just the name
    @Override
    public String toString() {
        return title;
    }
}