package com.felipeacerbi.nfctest.models;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class FeedPost {

    private int type;
    private String user;
    private String profileImage;
    private String timestamp;
    private String location;
    public Map<String, Boolean> likes;
    public Map<String, Boolean> marked;

    public FeedPost() {
    }

    public FeedPost(int type, String user, String profileImage, String timestamp, String location, Map<String, Boolean> likes, Map<String, Boolean> marked) {
        this.type = type;
        this.user = user;
        this.profileImage = profileImage;
        this.timestamp = timestamp;
        this.location = location;
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
        result.put("likes", likes);
        result.put("marked", marked);

        return result;
    }

    public static String formatTime(String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Integer.parseInt(time));

        String month = String.valueOf(calendar.get(Calendar.MONTH)).substring(0,2);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(calendar.get(Calendar.HOUR));
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        return month + " " + day + ", " + hour + ":" + minute;
    }
}
