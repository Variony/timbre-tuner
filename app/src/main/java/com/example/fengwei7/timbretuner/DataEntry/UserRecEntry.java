package com.example.fengwei7.timbretuner.DataEntry;

/**
 * Created by fengwei7 on 2016-10-28.
 */
public class UserRecEntry {
    private String username;
    private String title;
    private int score;

    // empty constructor

    public UserRecEntry( String username, String title, int score) {
        this.username = username;
        this.title = title;
        this.score = score;
    }


    // full constructor for reading from database with ID



    // Get/Set Functions for the database
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return this.username;
    }
    public void setComment(String username) {
        this.username = username;
    }

    public int getScore() {
        return this.score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    // Will be used by the ArrayAdapter in the ListView


}