package com.felipeacerbi.nfctest.models.posts;

import java.util.Map;

public class FeedPostMedia extends FeedPost {

    private String media; // Photo or Video path

    public FeedPostMedia() {
    }

    public FeedPostMedia(int type, String user, String profileImage, String timestamp, String location, String text, Map<String, Boolean> likes, Map<String, Boolean> marked, String media) {
        super(type, user, profileImage, timestamp, location, text, likes, marked);
        this.media = media;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> temp = super.toMap();
        temp.put("media", media);
        return temp;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }
}
