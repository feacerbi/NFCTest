package com.felipeacerbi.nfctest.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.felipeacerbi.nfctest.R;

public class PetViewHolder extends RecyclerView.ViewHolder {

    private TextView nameField;
    private TextView infosField;
    private ImageView profilePicture;
    private ProgressBar profilePictureProgress;


    public PetViewHolder(View itemView) {
        super(itemView);

        profilePicture = (ImageView) itemView.findViewById(R.id.profile_picture);
        profilePictureProgress = (ProgressBar) itemView.findViewById(R.id.profile_picture_progress);
        nameField = (TextView) itemView.findViewById(R.id.pet_name);
        infosField = (TextView) itemView.findViewById(R.id.pet_infos);
    }

    public TextView getNameField() {
        return nameField;
    }

    public TextView getInfosField() {
        return infosField;
    }

    public ImageView getProfilePicture() {
        return profilePicture;
    }

    public ProgressBar getProfilePictureProgress() {
        return profilePictureProgress;
    }
}
