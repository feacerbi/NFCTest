package com.felipeacerbi.nfctest.firebasemodels;

import java.util.List;

public abstract class FeedPostTextDB extends FeedPostDB {

    private String content;

    public FeedPostTextDB(String user, int type) {
        super(user, type);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
