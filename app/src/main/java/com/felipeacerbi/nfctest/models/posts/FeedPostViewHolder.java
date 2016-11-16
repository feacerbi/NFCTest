package com.felipeacerbi.nfctest.models.posts;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.felipeacerbi.nfctest.R;

public class FeedPostViewHolder extends RecyclerView.ViewHolder {

    private TextView userField;
    private TextView timeField;
    private TextView likeButton;
    private TextView commentButton;
    private ImageView profilePicture;
    private ProgressBar profilePictureProgress;
    private TextView likesView;
    private TextView commentsView;
    private TextView contentText;


    public FeedPostViewHolder(View itemView) {
        super(itemView);

        profilePicture = (ImageView) itemView.findViewById(R.id.profile_picture);
        profilePictureProgress = (ProgressBar) itemView.findViewById(R.id.profile_picture_progress);
        userField = (TextView) itemView.findViewById(R.id.post_title);
        timeField = (TextView) itemView.findViewById(R.id.post_date_time);
        likeButton = (TextView) itemView.findViewById(R.id.like_button);
        commentButton = (TextView) itemView.findViewById(R.id.comment_button);
        likesView = (TextView) itemView.findViewById(R.id.likes_number);
        commentsView = (TextView) itemView.findViewById(R.id.comments_number);
        contentText = (TextView) itemView.findViewById(R.id.post_content);

    }

    public TextView getUserField() {
        return userField;
    }

    public TextView getTimeField() {
        return timeField;
    }

    public TextView getLikeButton() {
        return likeButton;
    }

    public TextView getCommentButton() {
        return commentButton;
    }

    public ImageView getProfilePicture() {
        return profilePicture;
    }

    public ProgressBar getProfilePictureProgress() {
        return profilePictureProgress;
    }

    public TextView getLikesView() {
        return likesView;
    }

    public TextView getCommentsView() {
        return commentsView;
    }

    public TextView getContentText() {
        return contentText;
    }
}
