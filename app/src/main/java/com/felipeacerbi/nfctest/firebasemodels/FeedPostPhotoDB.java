package com.felipeacerbi.nfctest.firebasemodels;

public abstract class FeedPostPhotoDB extends FeedPostDB {

    private String photo;
    private String comment;

    public FeedPostPhotoDB(String user, int type) {
        super(user, type);
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
