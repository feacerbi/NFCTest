package com.felipeacerbi.nfctest.models;

import com.felipeacerbi.nfctest.models.FeedPost;

import java.util.Calendar;

public class FeedPostPhoto extends FeedPost {

    private String photo;
    private String comment;

    public FeedPostPhoto(Calendar time, String user, int type) {
        super(time, user, type);
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
