package com.felipeacerbi.nfctest.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.adapters.PostsAdapter;
import com.felipeacerbi.nfctest.models.posts.FeedPostFullViewHolder;
import com.felipeacerbi.nfctest.models.posts.FeedPostMedia;
import com.felipeacerbi.nfctest.models.posts.FeedPostText;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.google.firebase.database.Query;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public abstract class FeedFragment extends Fragment implements View.OnClickListener {

    // Firebase Helper instance
    private FirebaseDBHelper firebaseDBHelper;
    private FloatingActionButton fab;
    private RecyclerView cardsList;
    private LayoutManagerType currentLayoutManagerType;
    private PostsAdapter postsAdapter;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    public FeedFragment() {
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseDBHelper = new FirebaseDBHelper(getActivity());

        postsAdapter = new PostsAdapter(
                getActivity(),
                FeedPostText.class,
                R.layout.feed_card_text,
                FeedPostFullViewHolder.class,
                getQuery(firebaseDBHelper));

        cardsList.setAdapter(postsAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        cardsList = (RecyclerView) rootView.findViewById(R.id.cards_list);
        cardsList.setHasFixedSize(true);
        cardsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });

        /* API 23
        cardsList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY > oldScrollY) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }
        }); */

        currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(currentLayoutManagerType);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                FeedPostMedia feedPost = new FeedPostMedia();
                feedPost.setUser(firebaseDBHelper.getLoginName());
                feedPost.setName(firebaseDBHelper.getUserName());
                feedPost.setTimestamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                feedPost.setProfileImage(NFCReadFragment.downloadFilePath);
                feedPost.setText("Post text test, hey!");
                feedPost.setType(Constants.POST_TYPE_MEDIA);

                feedPost.setMedia(NFCReadFragment.downloadFilePath);

                String postKey = firebaseDBHelper.getPostsReference().push().getKey();
                Map<String, Object> childUpdates = new HashMap<>();

                childUpdates.put(Constants.DATABASE_POSTS_PATH + "/"
                                + postKey,
                                feedPost.toMap());
                childUpdates.put(Constants.DATABASE_USERS_CHILD + "/"
                                + firebaseDBHelper.getLoginName() + "/"
                                + Constants.DATABASE_POSTS_PATH + "/"
                                + postKey,
                                true);

                firebaseDBHelper.getDatabase().updateChildren(childUpdates);
                break;
            default:
        }
    }

    public void setRecyclerViewLayoutManager(FeedFragment.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        if (cardsList.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) cardsList.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        currentLayoutManagerType = FeedFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        cardsList.setLayoutManager(layoutManager);
        cardsList.scrollToPosition(scrollPosition);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (postsAdapter != null) {
            postsAdapter.cleanup();
        }
    }

    public abstract Query getQuery(FirebaseDBHelper firebaseDBHelper);
}
