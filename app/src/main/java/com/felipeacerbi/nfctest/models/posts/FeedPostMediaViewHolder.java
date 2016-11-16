package com.felipeacerbi.nfctest.models.posts;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;

import com.felipeacerbi.nfctest.R;

public class FeedPostMediaViewHolder extends FeedPostViewHolder {

    private ImageView contentPicture;
    private MediaController video;
    private ProgressBar contentProgress;

    public FeedPostMediaViewHolder(View itemView) {
        super(itemView);

        contentPicture = (ImageView) itemView.findViewById(R.id.post_photo);
        video = (MediaController) itemView.findViewById(R.id.post_video);
        contentProgress = (ProgressBar) itemView.findViewById(R.id.post_media_progress);
    }

    public ImageView getContentPicture() {
        return contentPicture;
    }

    public MediaController getVideo() {
        return video;
    }

    public ProgressBar getMediaProgress() {
        return contentProgress;
    }
}
