package com.felipeacerbi.nfctest.models;

import com.felipeacerbi.nfctest.models.FeedPost;

import java.util.Calendar;

public class FeedPostText extends FeedPost {

    private String content;

    public FeedPostText(Calendar time, String user, int type) {
        super(time, user, type);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
