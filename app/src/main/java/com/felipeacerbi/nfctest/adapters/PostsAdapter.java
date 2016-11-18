package com.felipeacerbi.nfctest.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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

public class PostsAdapter extends FirebaseRecyclerAdapter {

    private FirebaseDBHelper firebaseDBHelper;
    private FirebaseStoreHelper firebaseStoreHelper;
    private Context context;

    public PostsAdapter(Context context, Class modelClass, int modelLayout, Class viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
        firebaseDBHelper = new FirebaseDBHelper(context);
        firebaseStoreHelper = new FirebaseStoreHelper();
    }

    @Override
    public int getItemViewType(int position) {
        FeedPost item = (FeedPost) getItem(position);
        return item.getLayoutResource();
    }

    @Override
    protected Object parseSnapshot(DataSnapshot snapshot) {
        switch (snapshot.child("type").getValue(Integer.class)) {
            case Constants.POST_TYPE_TEXT:
                return snapshot.getValue(FeedPostText.class);
            case Constants.POST_TYPE_MEDIA:
                return snapshot.getValue(FeedPostMedia.class);
            default:
                return super.parseSnapshot(snapshot);
        }
    }

    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
        DatabaseReference postRef = getRef(position);
        final String postKey = postRef.getKey();

        firebaseDBHelper = new FirebaseDBHelper(context);
        firebaseStoreHelper = new FirebaseStoreHelper();

        FeedPost post = (FeedPost) model;
        final FeedPostFullViewHolder postViewHolder = (FeedPostFullViewHolder) viewHolder;

        postViewHolder.getUserField().setText(post.getName());
        postViewHolder.getTimeField().setText(FeedPost.formatTime(context, post.getTimestamp()));
        postViewHolder.getContentText().setText(post.getText());

        firebaseStoreHelper.downloadImage(
                post.getProfileImage(),
                post.getUser(),
                postViewHolder.getProfilePicture(),
                postViewHolder.getProfilePictureProgress(),
                null);

        postViewHolder.getLikeButton().setOnClickListener(new View.OnClickListener() {
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
                        postViewHolder.getLikesView().setText(String.valueOf(likesCount));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        postViewHolder.getCommentButton().setOnClickListener(new View.OnClickListener() {
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
                        postViewHolder.getCommentsView().setText(String.valueOf(commentsCount));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        if(post.getType() == Constants.POST_TYPE_MEDIA) {
            FeedPostMedia postMedia = (FeedPostMedia) model;

            FirebaseStoreHelper firebaseStoreHelper2 = new FirebaseStoreHelper();
            firebaseStoreHelper2.downloadImage(
                    postMedia.getMedia(),
                    postMedia.getUser(),
                    postViewHolder.getContentPicture(),
                    postViewHolder.getContentProgress(),
                    null);
        }
    }
}
