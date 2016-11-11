package com.felipeacerbi.nfctest.models;

import java.util.Map;

public class FeedPostMedia extends FeedPost {

    private String media; // Photo or Video path

    public FeedPostMedia(int type, String user, String profileImage, String timestamp, String location, Map<String, Boolean> likes, Map<String, Boolean> marked, String media) {
        super(type, user, profileImage, timestamp, location, likes, marked);
        this.media = media;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> temp = super.toMap();
        temp.put("media", media);
        return temp;
    }
}
