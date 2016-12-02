package com.felipeacerbi.nfctest.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.adapters.PostsAdapter;
import com.felipeacerbi.nfctest.models.Pet;
import com.felipeacerbi.nfctest.models.posts.FeedPost;
import com.felipeacerbi.nfctest.models.posts.FeedPostFullViewHolder;
import com.felipeacerbi.nfctest.models.posts.FeedPostMedia;
import com.felipeacerbi.nfctest.models.posts.FeedPostText;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.felipeacerbi.nfctest.utils.FirebaseStoreHelper;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class FeedFragment extends Fragment implements View.OnClickListener, OnSuccessListener<UploadTask.TaskSnapshot> {

    // Firebase Helper instance
    private FirebaseDBHelper firebaseDBHelper;
    private FloatingActionButton fab;
    private RecyclerView cardsList;
    private LayoutManagerType currentLayoutManagerType;
    private PostsAdapter postsAdapter;
    private ProgressBar progressBar;
    private TextView progress;
    private String postFilePath;
    private Pet currentPet;

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
                FeedPost.class,
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
        fab.setOnClickListener(this);

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
                firebaseDBHelper.getCurrentUserReference()
                        .child(Constants.DATABASE_BUDDIES_CHILD)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {};
                        Map<String, String> buddies = dataSnapshot.getValue(t);
                        if(buddies != null && !buddies.isEmpty()) {
                            if(buddies.size() > 1) {
                                choosePetAndPost(buddies);
                            } else {
                                createPostDialog(buddies.keySet().toArray()[0].toString());
                            }
                        } else {
                            Toast.makeText(getActivity(), "You have no buddies yet", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                break;
            default:
        }
    }

    public void choosePetAndPost(final Map<String, String> buddies) {
        String names[] = new String[buddies.size()];
        names = buddies.values().toArray(names);

        // Create buddies list dialog
        AlertDialog.Builder buddiesDialog = new AlertDialog.Builder(getActivity());
        buddiesDialog.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                createPostDialog(buddies.keySet().toArray()[i].toString());
            }
        })
                .setTitle("Select Buddy")
                .create()
                .show();
    }

    public void createPostDialog(String petId) {
        firebaseDBHelper.getPetReference(petId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentPet = new Pet(dataSnapshot);

                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.new_post_dialog, null);

                        AlertDialog.Builder postDialog = new AlertDialog.Builder(getActivity());
                        postDialog.setView(dialogView);

                        final AutoCompleteTextView content = (AutoCompleteTextView) dialogView.findViewById(R.id.post_content);
                        progressBar = (ProgressBar) dialogView.findViewById(R.id.upload_progress_bar);
                        progress = (TextView) dialogView.findViewById(R.id.upload_message);
                        ImageView uploadButton = (ImageView) dialogView.findViewById(R.id.upload_image_button);

                        ImageView profilePicture = (ImageView) dialogView.findViewById(R.id.profile_picture);
                        ProgressBar profileProgress = (ProgressBar) dialogView.findViewById(R.id.profile_picture_progress);
                        TextView petName = (TextView) dialogView.findViewById(R.id.post_title);
                        TextView time = (TextView) dialogView.findViewById(R.id.post_date_time);
                        time.setVisibility(View.GONE);

                        setHeader(profilePicture, profileProgress, petName);
                        setUploadButton(uploadButton);

                        postDialog.setPositiveButton("Post", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (postFilePath != null && !postFilePath.equals("")) {
                                    FeedPostMedia feedPost = new FeedPostMedia();
                                    feedPost.setType(Constants.POST_TYPE_MEDIA);
                                    feedPost.setMedia(postFilePath);

                                    addNewPost(feedPost, currentPet, content.getText().toString());
                                } else {
                                    FeedPostText feedPostText = new FeedPostText();
                                    feedPostText.setType(Constants.POST_TYPE_TEXT);

                                    addNewPost(feedPostText, currentPet, content.getText().toString());
                                }
                            }
                        })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(getActivity(), "Post cancelled", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .create()
                                .show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setHeader(ImageView profilePicture, ProgressBar profileProgress, TextView petName) {
        FirebaseStoreHelper firebaseStoreHelper = new FirebaseStoreHelper();
        firebaseStoreHelper.downloadImage(
                currentPet.getProfileImage(),
                currentPet.getId(),
                profilePicture,
                profileProgress,
                null);

        petName.setText(currentPet.getName());
    }

    private void addNewPost(final FeedPost feedPost, Pet pet, String content) {
        feedPost.setPet(pet.getId());
        feedPost.setTimestamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        feedPost.setText(content);

        firebaseDBHelper.getPetReference(feedPost.getPet()).child(Constants.DATABASE_FOLLOWERS_CHILD)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String postKey = firebaseDBHelper.getPostsReference().push().getKey();
                        Map<String, Object> childUpdates = new HashMap<>();

                        childUpdates.put(Constants.DATABASE_POSTS_PATH + "/"
                                        + postKey,
                                        feedPost.toMap());

                        for (DataSnapshot follower : dataSnapshot.getChildren()) {
                            childUpdates.put(Constants.DATABASE_USERS_PATH + "/"
                                            + follower.getKey() + "/"
                                            + Constants.DATABASE_FEED_CHILD + "/"
                                            + postKey,
                                            feedPost.toMap());
                        }

                        childUpdates.put(Constants.DATABASE_PETS_PATH + "/"
                                        + feedPost.getPet() + "/"
                                        + Constants.DATABASE_POSTS_CHILD + "/"
                                        + postKey,
                                        feedPost.toMap());

                        childUpdates.put(Constants.DATABASE_USERS_PATH + "/"
                                        + firebaseDBHelper.getLoginName() + "/"
                                        + Constants.DATABASE_FEED_CHILD + "/"
                                        + postKey,
                                        feedPost.toMap());

                        firebaseDBHelper.getDatabase().updateChildren(childUpdates);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        postFilePath = null;
    }

    private void setUploadButton(ImageView uploadButton) {
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (rc == PackageManager.PERMISSION_GRANTED) {
                    startPickImageActivity();
                } else {
                    requestStoragePermission();
                }
            }
        });
    }

    private void startPickImageActivity() {
        Intent gal = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(gal.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(gal, Constants.GET_IMAGE_FROM_PICKER);
        } else {
            Toast.makeText(getActivity(), R.string.no_gallery, Toast.LENGTH_SHORT).show();
        }
    }

    private void requestStoragePermission() {
        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), "Permission Should", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), permissions, Constants.WRITE_EXTERNAL_STORAGE_PERMISSION);
        } else {
            Toast.makeText(getActivity(), R.string.allow_write_storage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseStoreHelper firebaseStoreHelper = new FirebaseStoreHelper();

        if(requestCode == Constants.GET_IMAGE_FROM_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                UploadTask uploadTask = firebaseStoreHelper.uploadImage(
                        new File(getBitmapPath(data)),
                        currentPet.getId(),
                        progressBar,
                        progress);
                uploadTask.addOnSuccessListener(this);
            }
        }
    }

    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
        Uri downloadUrl = taskSnapshot.getDownloadUrl();
        if(downloadUrl != null) {
            postFilePath = downloadUrl.getLastPathSegment().substring(downloadUrl.getLastPathSegment().lastIndexOf("/"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(getActivity(), "Permission Result", Toast.LENGTH_SHORT).show();

        if(requestCode == Constants.WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPickImageActivity();
            }
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

    public String getBitmapPath(Intent data){
        String picturePath = "";
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
        }

        return picturePath;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(postsAdapter != null) {
            postsAdapter.cleanup();
        }
    }

    public abstract Query getQuery(FirebaseDBHelper firebaseDBHelper);
}
