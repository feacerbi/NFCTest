package com.felipeacerbi.nfctest.fragments;

import android.os.Bundle;

import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.google.firebase.database.Query;

public class FeedFragmentAll extends FeedFragment {

    public static FeedFragmentAll newInstance(int sectionNumber) {
        FeedFragmentAll fragment = new FeedFragmentAll();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Query getQuery(FirebaseDBHelper firebaseDBHelper) {
        return firebaseDBHelper.getCurrentUserReference().child(Constants.DATABASE_FEED_CHILD).orderByKey().limitToLast(20);
    }
}
