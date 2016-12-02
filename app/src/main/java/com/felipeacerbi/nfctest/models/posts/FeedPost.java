package com.felipeacerbi.nfctest.models.posts;

import android.content.Context;

import com.felipeacerbi.nfctest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public abstract class FeedPost {

    private String id;
    private int type;
    private String pet;
    private String timestamp;
    private String location;
    private String text;
    private Map<String, Boolean> likes;
    private Map<String, Boolean> marked;

    public FeedPost() {
    }

    public FeedPost(DataSnapshot dataSnapshot) {
        fromMap(dataSnapshot);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("pet", pet);
        result.put("timestamp", timestamp);
        result.put("location", location);
        result.put("text", text);
        result.put("likes", likes);
        result.put("marked", marked);

        return result;
    }

    public void fromMap(DataSnapshot dataSnapshot) {
        id = dataSnapshot.getKey();
        type = dataSnapshot.child("type").getValue(Integer.class);
        pet = dataSnapshot.child("pet").getValue(String.class);
        timestamp = dataSnapshot.child("timestamp").getValue(String.class);
        location = dataSnapshot.child("location").getValue(String.class);
        text = dataSnapshot.child("text").getValue(String.class);
        GenericTypeIndicator<Map<String, Boolean>> t = new GenericTypeIndicator<Map<String, Boolean>>() {};
        likes = dataSnapshot.child("likes").getValue(t);
        marked = dataSnapshot.child("marked").getValue(t);
    }

    public static String formatTime(Context context, String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(time));

        String month = context.getResources().getStringArray(R.array.months)[calendar.get(Calendar.MONTH)];
        int dayNum = calendar.get(Calendar.DAY_OF_MONTH);
        String day = (dayNum / 10 >= 1) ? String.valueOf(dayNum) : "0" + dayNum;
        int hourNum = calendar.get(Calendar.HOUR_OF_DAY);
        String hour = (hourNum / 10 >= 1) ? String.valueOf(hourNum) : "0" + hourNum;
        int minuteNum = calendar.get(Calendar.MINUTE);
        String minute = (minuteNum / 10 >= 1) ? String.valueOf(minuteNum) : "0" + minuteNum;
        return month + " " + day + ", " + hour + ":" + minute;
    }

    public abstract int getLayoutResource();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPet() {
        return pet;
    }

    public void setPet(String pet) {
        this.pet = pet;
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

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public Map<String, Boolean> getMarked() {
        return marked;
    }

    public void setMarked(Map<String, Boolean> marked) {
        this.marked = marked;
    }
}
