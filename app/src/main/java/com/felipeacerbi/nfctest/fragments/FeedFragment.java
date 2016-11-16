package com.felipeacerbi.nfctest.fragments;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.models.posts.FeedPost;
import com.felipeacerbi.nfctest.models.posts.FeedPostMedia;
import com.felipeacerbi.nfctest.models.posts.FeedPostMediaViewHolder;
import com.felipeacerbi.nfctest.models.posts.FeedPostViewHolder;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.felipeacerbi.nfctest.utils.FirebaseStoreHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public abstract class FeedFragment extends Fragment implements View.OnClickListener {

    // Firebase Helper instance
    private FirebaseDBHelper firebaseDBHelper;
    private FirebaseStoreHelper firebaseStoreHelper;
    private FloatingActionButton fab;
    private RecyclerView cardsList;
    private LayoutManagerType currentLayoutManagerType;
    private FirebaseRecyclerAdapter<FeedPostMedia, FeedPostMediaViewHolder> postsAdapter;

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
        firebaseStoreHelper = new FirebaseStoreHelper();

        postsAdapter = new FirebaseRecyclerAdapter<FeedPostMedia, FeedPostMediaViewHolder>(FeedPostMedia.class, R.layout.feed_card_media, FeedPostMediaViewHolder.class, getQuery(firebaseDBHelper)) {
            @Override
            protected void populateViewHolder(final FeedPostMediaViewHolder viewHolder, FeedPostMedia model, int position) {
                DatabaseReference postRef = getRef(position);
                final String postKey = postRef.getKey();

                firebaseDBHelper = new FirebaseDBHelper(getActivity());
                firebaseStoreHelper = new FirebaseStoreHelper();

                viewHolder.getTimeField().setText(FeedPost.formatTime(model.getTimestamp()));
                viewHolder.getContentText().setText(model.getText());

                firebaseDBHelper.setUserName(model.getUser(), viewHolder.getUserField());

                firebaseStoreHelper.downloadImage(
                        model.getProfileImage(),
                        model.getUser(),
                        viewHolder.getProfilePicture(),
                        viewHolder.getProfilePictureProgress(),
                        null);

                viewHolder.getLikeButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseDBHelper.getPostReference(postKey)
                                .child(Constants.DATABASE_LIKES_CHILD)
                                .child(firebaseDBHelper.getLoginName())
                                .setValue(true);
                    }
                });

                firebaseDBHelper.getPostReference(postKey).child(Constants.DATABASE_LIKES_CHILD).
                        addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                long likesCount = dataSnapshot.getChildrenCount();
                                viewHolder.getLikesView().setText(String.valueOf(likesCount));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                viewHolder.getCommentButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Add user comment
                    }
                });

                firebaseDBHelper.getPostReference(postKey).child(Constants.DATABASE_COMMENTS_PATH).
                        addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                long commentsCount = dataSnapshot.getChildrenCount();
                                viewHolder.getCommentsView().setText(String.valueOf(commentsCount));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                FirebaseStoreHelper firebaseStoreHelper2 = new FirebaseStoreHelper();
                firebaseStoreHelper2.downloadImage(
                        model.getMedia(),
                        model.getUser(),
                        viewHolder.getContentPicture(),
                        viewHolder.getMediaProgress(),
                        null);
            }
        };

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
                feedPost.setTimestamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                feedPost.setProfileImage(NFCReadFragment.downloadFilePath);
                feedPost.setText("Post text test, hey!");
                feedPost.setType(Constants.POST_TYPE_MEDIA);

                feedPost.setMedia(NFCReadFragment.downloadFilePath);

                String key = firebaseDBHelper.getPostsReference().push().getKey();
                Map<String, Object> postValues = feedPost.toMap();
                Map<String, Object> childUpdates = new HashMap<>();

                childUpdates.put(Constants.DATABASE_POSTS_PATH + "/"
                                + key,
                        postValues);
                childUpdates.put(Constants.DATABASE_USERS_CHILD + "/"
                                + firebaseDBHelper.getLoginName() + "/"
                                + Constants.DATABASE_POSTS_PATH + "/"
                                + key,
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
