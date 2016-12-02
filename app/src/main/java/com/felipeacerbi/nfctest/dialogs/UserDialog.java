package com.felipeacerbi.nfctest.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.models.Pet;
import com.felipeacerbi.nfctest.models.User;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseStoreHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class UserDialog extends EditProfileDialog implements View.OnClickListener {

    private User user;
    private ImageView userPhoto;

    public static UserDialog newInstance(User user) {
        UserDialog fragment = new UserDialog();
        Bundle args = new Bundle();
        args.putSerializable(Constants.USER_EXTRA, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && !getArguments().isEmpty()) {
            user = (User) getArguments().getSerializable(Constants.USER_EXTRA);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.user_dialog, null);

        userPhoto = (ImageView) view.findViewById(R.id.user_photo);
        final AutoCompleteTextView userAge = (AutoCompleteTextView) view.findViewById(R.id.user_age);
        final TextView userGender = (TextView) view.findViewById(R.id.user_gender);
        final AutoCompleteTextView userDescription = (AutoCompleteTextView) view.findViewById(R.id.user_description);
        progressBar = (ProgressBar) view.findViewById(R.id.upload_progress_bar);
        progress = (TextView) view.findViewById(R.id.upload_message);

        userPhoto.setOnClickListener(this);
        userGender.setOnClickListener(this);

        firebaseDBHelper.getCurrentUserReference().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user = new User(dataSnapshot);
                        FirebaseStoreHelper firebaseStoreHelper = new FirebaseStoreHelper();
                        firebaseStoreHelper.downloadImage(user.getProfilePicture(),
                                firebaseDBHelper.getLoginName(),
                                userPhoto,
                                progressBar,
                                null);

                        userAge.setText(user.getAge());
                        userGender.setText(String.valueOf(user.getGender()));
                        userDescription.setText(user.getDescription());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        user.setAge(userAge.getText().toString());
                        user.setGender(userGender.getText().toString().equals("Male") ?
                                Constants.MALE :
                                Constants.FEMALE);
                        user.setDescription(userDescription.getText().toString());

                        firebaseDBHelper.updateUser(user);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UserDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onClick(final View view) {
        AlertDialog.Builder builder;
        switch (view.getId()) {
            case R.id.user_photo:
                onClickPhoto();
                break;
            case R.id.user_gender:
                builder = new AlertDialog.Builder(getActivity());
                builder.setItems(R.array.genders, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0) {
                            user.setGender(Constants.MALE);
                        } else {
                            user.setGender(Constants.FEMALE);
                        }
                        ((TextView) view).setText(getActivity().getResources().getStringArray(R.array.genders)[i]);
                    }
                })
                        .create()
                        .show();
                break;
        }
    }

    @Override
    public void resultOk(File path) {
        FirebaseStoreHelper firebaseStoreHelper = new FirebaseStoreHelper();
        UploadTask uploadTask = firebaseStoreHelper.uploadImage(
                path,
                firebaseDBHelper.getLoginName(),
                progressBar,
                progress);
        uploadTask.addOnSuccessListener(this);

        userPhoto.setImageBitmap(BitmapFactory.decodeFile(path.getAbsolutePath()));
    }

    @Override
    public void successUrl(Uri downloadUrl) {
        user.setProfilePicture(downloadUrl.getLastPathSegment().substring(downloadUrl.getLastPathSegment().lastIndexOf("/")));
    }
}
