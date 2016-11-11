package com.felipeacerbi.nfctest.models;

import java.util.Map;

public class FeedPostMap extends FeedPost {

    private Path path;

    public FeedPostMap(int type, String user, String profileImage, String timestamp, String location, Map<String, Boolean> likes, Map<String, Boolean> marked, Path path) {
        super(type, user, profileImage, timestamp, location, likes, marked);
        this.path = path;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> temp = super.toMap();
        temp.put("path", path);
        return temp;
    }
}
