package com.felipeacerbi.nfctest.models.posts;

import com.felipeacerbi.nfctest.R;
import com.google.firebase.database.DataSnapshot;

public class FeedPostText extends FeedPost {

    public FeedPostText() {
    }

    public FeedPostText(DataSnapshot dataSnapshot) {
        super(dataSnapshot);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.feed_card_text;
    }
}
