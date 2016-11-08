package com.felipeacerbi.nfctest.firebasemodels;

import com.felipeacerbi.nfctest.models.Comment;

import java.util.Calendar;
import java.util.List;

public abstract class FeedPostDB {

    private String user;
    private int type;
    private List<Boolean> likes;

    public FeedPostDB(String user, int type) {
        this.user = user;
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Boolean> getLikes() {
        return likes;
    }

    public void setLikes(List<Boolean> likes) {
        this.likes = likes;
    }

}
