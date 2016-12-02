package com.felipeacerbi.nfctest.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.felipeacerbi.nfctest.models.Pet;
import com.felipeacerbi.nfctest.models.posts.FeedPost;
import com.felipeacerbi.nfctest.models.posts.FeedPostFullViewHolder;
import com.felipeacerbi.nfctest.models.posts.FeedPostMedia;
import com.felipeacerbi.nfctest.models.posts.FeedPostText;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.felipeacerbi.nfctest.utils.FirebaseStoreHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostsAdapter extends FirebaseRecyclerAdapter {

    private FirebaseDBHelper firebaseDBHelper;
    private Context context;
    private Pet currentPet;

    public PostsAdapter(Context context, Class modelClass, int modelLayout, Class viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
        firebaseDBHelper = new FirebaseDBHelper(context);
    }

    @Override
    public int getItemViewType(int position) {
        FeedPost item = (FeedPost) getItem(position);
        return item.getLayoutResource();
    }

    @Override
    protected Object parseSnapshot(DataSnapshot snapshot) {
        switch (snapshot.child(Constants.DATABASE_TYPE_CHILD).getValue(Integer.class)) {
            case Constants.POST_TYPE_TEXT:
                return snapshot.getValue(FeedPostText.class);
            case Constants.POST_TYPE_MEDIA:
                return snapshot.getValue(FeedPostMedia.class);
            default:
                return super.parseSnapshot(snapshot);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final FeedPost item = (FeedPost) getItem(position);
        firebaseDBHelper.getPetReference(item.getPet()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentPet = new Pet(dataSnapshot);
                        populateViewHolder(viewHolder, item, position);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
        DatabaseReference postRef = getRef(position);
        final String postKey = postRef.getKey();

        firebaseDBHelper = new FirebaseDBHelper(context);

        final FeedPost post = (FeedPost) model;
        final FeedPostFullViewHolder postViewHolder = (FeedPostFullViewHolder) viewHolder;

        postViewHolder.getPetField().setText(currentPet.getName());
        postViewHolder.getTimeField().setText(FeedPost.formatTime(context, post.getTimestamp()));
        postViewHolder.getContentText().setText(post.getText());

        FirebaseStoreHelper firebaseStoreHelper = new FirebaseStoreHelper();
        firebaseStoreHelper.downloadImage(
                currentPet.getProfileImage(),
                post.getPet(),
                postViewHolder.getProfilePicture(),
                postViewHolder.getProfilePictureProgress(),
                null);

        postViewHolder.getLikeButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDBHelper.getPetReference(post.getPet()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> childUpdates = new HashMap<>();

                        childUpdates.put(Constants.DATABASE_POSTS_PATH + "/"
                                        + postKey + "/"
                                        + Constants.DATABASE_LIKES_CHILD + "/"
                                        + firebaseDBHelper.getLoginName(),
                                         true);

                        for(DataSnapshot follower : dataSnapshot.child(Constants.DATABASE_FOLLOWERS_CHILD).getChildren()) {
                            childUpdates.put(Constants.DATABASE_USERS_PATH + "/"
                                            + follower.getKey() + "/"
                                            + Constants.DATABASE_FEED_CHILD + "/"
                                            + postKey + "/"
                                            + Constants.DATABASE_LIKES_CHILD + "/"
                                            + firebaseDBHelper.getLoginName(),
                                            true);
                        }

                        childUpdates.put(Constants.DATABASE_PETS_PATH + "/"
                                        + post.getPet() + "/"
                                        + Constants.DATABASE_POSTS_PATH + "/"
                                        + postKey + "/"
                                        + Constants.DATABASE_LIKES_CHILD + "/"
                                        + firebaseDBHelper.getLoginName(),
                                        true);

                        firebaseDBHelper.getDatabase().updateChildren(childUpdates);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        firebaseDBHelper.getPostReference(postKey).child(Constants.DATABASE_LIKES_CHILD).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long likesCount = dataSnapshot.getChildrenCount();
                        postViewHolder.getLikesView().setText(String.valueOf(likesCount));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        /* TODO Add user comment
        postViewHolder.getCommentButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        firebaseDBHelper.getPostReference(postKey).child(Constants.DATABASE_COMMENTS_PATH).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long commentsCount = dataSnapshot.getChildrenCount();
                        postViewHolder.getCommentsView().setText(String.valueOf(commentsCount));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
         */

        if(post.getType() == Constants.POST_TYPE_MEDIA) {
            FeedPostMedia postMedia = (FeedPostMedia) model;

            FirebaseStoreHelper firebaseStoreHelperMedia = new FirebaseStoreHelper();
            firebaseStoreHelperMedia.downloadImage(
                    postMedia.getMedia(),
                    postMedia.getPet(),
                    postViewHolder.getContentPicture(),
                    postViewHolder.getContentProgress(),
                    null);
        }
    }
}
