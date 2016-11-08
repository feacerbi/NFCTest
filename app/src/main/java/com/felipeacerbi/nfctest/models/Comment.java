package com.felipeacerbi.nfctest.models;

import java.util.Calendar;

public class Comment {

    private Calendar time;
    private String user;
    private String comment;

    public Comment(Calendar time, String user, String comment) {
        this.time = time;
        this.user = user;
        this.comment = comment;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
