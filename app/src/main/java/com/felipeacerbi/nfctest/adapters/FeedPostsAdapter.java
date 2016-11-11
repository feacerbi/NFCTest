//package com.felipeacerbi.nfctest.adapters;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.felipeacerbi.nfctest.R;
//import com.felipeacerbi.nfctest.models.FeedPost;
//import com.felipeacerbi.nfctest.models.FeedPostMedia;
//import com.felipeacerbi.nfctest.utils.Constants;
//import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
//import com.felipeacerbi.nfctest.utils.FirebaseStoreHelper;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.ValueEventListener;
//
//import java.io.File;
//import java.util.Calendar;
//import java.util.List;
//
//public class FeedPostsAdapter extends RecyclerView.Adapter<FeedPostsAdapter.ViewHolder> {
//
//    private List<FeedPost> posts;
//    private Context context;
//    private FirebaseDBHelper firebaseDBHelper;
//    private FirebaseStoreHelper firebaseStoreHelper;
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        private final TextView userField;
//        private final TextView timeField;
//        private final TextView likeButton;
//        private final TextView commentButton;
//        private final ImageView profilePicture;
//        private final ProgressBar profilePictureProgress;
//        private final TextView likesView;
//        private final TextView commentsView;
//
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//
//            profilePicture = (ImageView) itemView.findViewById(R.id.profile_picture);
//            profilePictureProgress = (ProgressBar) itemView.findViewById(R.id.profile_picture_progress);
//            userField = (TextView) itemView.findViewById(R.id.post_title);
//            timeField = (TextView) itemView.findViewById(R.id.post_date_time);
//            likeButton = (TextView) itemView.findViewById(R.id.like_button);
//            commentButton = (TextView) itemView.findViewById(R.id.comment_button);
//            likesView = (TextView) itemView.findViewById(R.id.likes_number);
//            commentsView = (TextView) itemView.findViewById(R.id.comments_number);
//
//        }
//
//        public TextView getUserField() {
//            return userField;
//        }
//
//        public TextView getTimeField() {
//            return timeField;
//        }
//
//        public TextView getLikeButton() {
//            return likeButton;
//        }
//
//        public TextView getCommentButton() {
//            return commentButton;
//        }
//
//        public ImageView getProfilePicture() {
//            return profilePicture;
//        }
//
//        public ProgressBar getProfilePictureProgress() {
//            return profilePictureProgress;
//        }
//
//        public TextView getLikesView() {
//            return likesView;
//        }
//
//        public TextView getCommentsView() {
//            return commentsView;
//        }
//    }
//
//    public static class ViewHolderText extends ViewHolder {
//
//        private final TextView contentField;
//
//        public ViewHolderText(View itemView) {
//            super(itemView);
//
//            contentField = (TextView) itemView.findViewById(R.id.post_content);
//
//        }
//
//        public TextView getContentField() {
//            return contentField;
//        }
//    }
//
//    public static class ViewHolderPhoto extends ViewHolder {
//
//        private final ImageView photoField;
//        private final TextView photoCommentField;
//        private final ProgressBar photoFieldProgress;
//
//        public ViewHolderPhoto(View itemView) {
//            super(itemView);
//
//            photoField = (ImageView) itemView.findViewById(R.id.post_photo);
//            photoCommentField = (TextView) itemView.findViewById(R.id.post_photo_comment);
//            photoFieldProgress = (ProgressBar) itemView.findViewById(R.id.post_photo_progress);
//        }
//
//        public ImageView getPhotoField() {
//            return photoField;
//        }
//
//        public TextView getPhotoCommentField() {
//            return photoCommentField;
//        }
//
//        public ProgressBar getPhotoFieldProgress() {
//            return photoFieldProgress;
//        }
//    }
//
//    public FeedPostsAdapter(List<FeedPost> posts, Context context) {
//        this.posts = posts;
//        this.context = context;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView;
//        switch (viewType) {
//            case Constants.POST_TYPE_TEXT:
//                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_card_text, parent, false);
//                itemView.setTag(0, parent);
//                return new ViewHolderText(itemView);
//            case Constants.POST_TYPE_PHOTO:
//                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_card_photo, parent, false);
//                itemView.setTag(0, parent);
//                return new ViewHolderPhoto(itemView);
//            default:
//                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_card_text, parent, false);
//                itemView.setTag(0, parent);
//                return new ViewHolderText(itemView);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(final ViewHolder holder, int position) {
//        final FeedPost post = getPosts().get(position);
//        firebaseDBHelper = new FirebaseDBHelper(context);
//        firebaseStoreHelper = new FirebaseStoreHelper();
//
//        holder.getUserField().setText(post.getFeedPostDB().getUser());
//        holder.getTimeField().setText(FeedPost.formatTime(post.getTime()));
//        firebaseStoreHelper.downloadImage(
//                new File(post.getFeedPostDB().getImage()),
//                holder.getProfilePicture(),
//                holder.getProfilePictureProgress(),
//                null);
//        holder.getLikeButton().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                firebaseDBHelper.getPostReference(post.getTimeString())
//                        .child(Constants.DATABASE_LIKES_CHILD)
//                        .child(firebaseDBHelper.getLoginName())
//                        .setValue(true);
//            }
//        });
//        firebaseDBHelper.getPostReference(post.getTimeString()).child(Constants.DATABASE_LIKES_CHILD).
//                addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        long likesCount = dataSnapshot.getChildrenCount();
//                        holder.getLikesView().setText(String.valueOf(likesCount));
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//        holder.getCommentButton().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Add user comment
//            }
//        });
//        firebaseDBHelper.getPostReference(post.getTimeString()).child(Constants.DATABASE_COMMENTS_PATH).
//                addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        long commentsCount = dataSnapshot.getChildrenCount();
//                        holder.getCommentsView().setText(String.valueOf(commentsCount));
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//        if(post instanceof FeedPostVideo) {
//            ViewHolderText viewHolderText;
//            FeedPostVideo feedPostVideo = (FeedPostVideo) post;
//
//            if(holder instanceof ViewHolderText) {
//                viewHolderText = (ViewHolderText) holder;
//            } else {
//                ViewGroup viewGroup = (ViewGroup) holder.itemView.getTag(0);
//                viewHolderText = new ViewHolderText(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_card_text, viewGroup, false));
//            }
//
//            viewHolderText.getContentField().setText(feedPostVideo.getContent());
//        } else if(post instanceof FeedPostMedia) {
//            ViewHolderPhoto viewHolderPhoto;
//            FeedPostMedia feedPostMedia = (FeedPostMedia) post;
//
//            if(holder instanceof ViewHolderPhoto) {
//                viewHolderPhoto = (ViewHolderPhoto) holder;
//            } else {
//                ViewGroup viewGroup = (ViewGroup) holder.itemView.getTag(0);
//                viewHolderPhoto = new ViewHolderPhoto(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_card_photo, viewGroup, false));
//            }
//
//            viewHolderPhoto.getPhotoCommentField().setText(feedPostMedia.getComment());
//            firebaseStoreHelper.downloadImage(
//                    new File(feedPostMedia.getPhoto()),
//                    viewHolderPhoto.getPhotoField(),
//                    viewHolderPhoto.getPhotoFieldProgress(),
//                    null);
//        }
//    }
//
//    public void addPost(FeedPost post) {
//        getPosts().add(0, post);
//        notifyItemInserted(0);
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        FeedPost feedPost = getPosts().get(position);
//        return feedPost.getFeedPostDB().getType();
//    }
//
//    public List<FeedPost> getPosts() {
//        return posts;
//    }
//
//    @Override
//    public int getItemCount() {
//        return posts.size();
//    }
//
//    public Calendar getLastUpdateTime() {
//        if(posts.size() > 0) {
//            return posts.get(0).getTime();
//        } else {
//            return Calendar.getInstance();
//        }
//    }
//}
