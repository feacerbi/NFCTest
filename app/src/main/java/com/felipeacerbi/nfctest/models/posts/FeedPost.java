package com.felipeacerbi.nfctest.models.posts;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FeedPost {

    private int type;
    private String user;
    private String profileImage;
    private String timestamp;
    private String location;
    private String text;
    public Map<String, Boolean> likes;
    public Map<String, Boolean> marked;

    public FeedPost() {
    }

    public FeedPost(int type, String user, String profileImage, String timestamp, String location, String text, Map<String, Boolean> likes, Map<String, Boolean> marked) {
        this.type = type;
        this.user = user;
        this.profileImage = profileImage;
        this.timestamp = timestamp;
        this.location = location;
        this.text = text;
        this.likes = likes;
        this.marked = marked;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("user", user);
        result.put("profileImage", profileImage);
        result.put("timestamp", timestamp);
        result.put("location", location);
        result.put("text", text);
        result.put("likes", likes);
        result.put("marked", marked);

        return result;
    }

    public static String formatTime(String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(time));

        String month = String.valueOf(calendar.get(Calendar.MONTH)).substring(0,2);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(calendar.get(Calendar.HOUR));
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        return month + "/" + day + ", " + hour + ":" + minute;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
