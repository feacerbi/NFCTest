package com.felipeacerbi.nfctest.models.posts;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.models.Path;
import com.google.firebase.database.DataSnapshot;

import java.util.Map;

public class FeedPostMap extends FeedPost {

    private Path path;

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> temp = super.toMap();
        temp.put("path", path);
        return temp;
    }

    @Override
    public void fromMap(DataSnapshot dataSnapshot) {
        super.fromMap(dataSnapshot);
        path = dataSnapshot.child("path").getValue(Path.class);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.feed_card_media; //TODO Create maps layout
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
