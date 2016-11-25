package com.felipeacerbi.nfctest.models.posts;

import com.felipeacerbi.nfctest.R;
import com.google.firebase.database.DataSnapshot;

import java.util.Map;

public class FeedPostMedia extends FeedPost {

    private String media; // Photo or Video path

    public FeedPostMedia() {
    }

    public FeedPostMedia(DataSnapshot dataSnapshot) {
        fromMap(dataSnapshot);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> temp = super.toMap();
        temp.put("media", media);
        return temp;
    }

    @Override
    public void fromMap(DataSnapshot dataSnapshot) {
        super.fromMap(dataSnapshot);
        media = dataSnapshot.child("media").getValue(String.class);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.feed_card_media;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }
}
