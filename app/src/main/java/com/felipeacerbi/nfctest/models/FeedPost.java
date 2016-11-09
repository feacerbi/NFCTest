package com.felipeacerbi.nfctest.models;

import java.util.Calendar;
import java.util.List;

public abstract class FeedPost {

    private Calendar time;
    private String user;
    private String imagePath;
    private int type;
    private List<Boolean> likes;
    private List<Comment> comments;

    public FeedPost(Calendar time, String user, int type) {
        this.time = time;
        this.user = user;
        this.type = type;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public static String formatTime(Calendar time) {
        String month = String.valueOf(time.get(Calendar.MONTH)).substring(0,2);
        String day = String.valueOf(time.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(time.get(Calendar.HOUR));
        String minute = String.valueOf(time.get(Calendar.MINUTE));
        return month + " " + day + ", " + hour + ":" + minute;
    }
}
