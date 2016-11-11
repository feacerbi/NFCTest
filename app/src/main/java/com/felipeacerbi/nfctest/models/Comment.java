package com.felipeacerbi.nfctest.models;

import com.felipeacerbi.nfctest.firebasemodels.CommentDB;

import java.util.Calendar;

public class Comment {

    private String id; // Timestamp + user
    private CommentDB commentDB;

    public Comment() {
    }

    public Comment(String id, CommentDB commentDB) {
        this.id = id;
        this.commentDB = commentDB;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CommentDB getCommentDB() {
        return commentDB;
    }

    public void setCommentDB(CommentDB commentDB) {
        this.commentDB = commentDB;
    }
}
