package com.felipeacerbi.nfctest.models.posts;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.felipeacerbi.nfctest.R;

public class FeedPostFullViewHolder extends RecyclerView.ViewHolder {

    private TextView petField;
    private TextView timeField;
    private TextView likeButton;
    private TextView commentButton;
    private ImageView profilePicture;
    private ProgressBar profilePictureProgress;
    private TextView likesView;
    private TextView commentsView;
    private TextView contentText;

    private ImageView contentPicture;
    private MediaController video;
    private ProgressBar contentProgress;


    public FeedPostFullViewHolder(View itemView) {
        super(itemView);

        profilePicture = (ImageView) itemView.findViewById(R.id.profile_picture);
        profilePictureProgress = (ProgressBar) itemView.findViewById(R.id.profile_picture_progress);
        petField = (TextView) itemView.findViewById(R.id.post_title);
        timeField = (TextView) itemView.findViewById(R.id.post_date_time);
        likeButton = (TextView) itemView.findViewById(R.id.like_button);
        commentButton = (TextView) itemView.findViewById(R.id.comment_button);
        likesView = (TextView) itemView.findViewById(R.id.likes_number);
        commentsView = (TextView) itemView.findViewById(R.id.comments_number);
        contentText = (TextView) itemView.findViewById(R.id.post_content);

        contentPicture = (ImageView) itemView.findViewById(R.id.post_photo);
        video = (MediaController) itemView.findViewById(R.id.post_video);
        contentProgress = (ProgressBar) itemView.findViewById(R.id.post_media_progress);
    }

    public TextView getPetField() {
        return petField;
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

    public ImageView getContentPicture() {
        return contentPicture;
    }

    public MediaController getVideo() {
        return video;
    }

    public ProgressBar getContentProgress() {
        return contentProgress;
    }
}
