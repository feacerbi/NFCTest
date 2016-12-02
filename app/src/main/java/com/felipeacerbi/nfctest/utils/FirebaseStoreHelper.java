package com.felipeacerbi.nfctest.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.fragments.NFCReadFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class FirebaseStoreHelper implements OnFailureListener, OnSuccessListener,
        OnPausedListener, OnProgressListener {

    private final FirebaseDBHelper firebaseDBHelper;
    private UploadTask currentUploadTask;
    private FileDownloadTask currentDownloadTask;
    private ProgressBar uploadProgressBar;
    private TextView uploadProgress;
    private ProgressBar downloadProgressBar;
    private TextView downloadProgress;
    private ImageView downloadImageView;
    private File localFile;

    public FirebaseStoreHelper() {
        firebaseDBHelper = new FirebaseDBHelper();
    }

    public StorageReference getUserImagesReference(String name) {
        return getImagesReference().child(name);
    }

    public StorageReference getCurrentUserImagesReference() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        return storage.getReference(Constants.STORAGE_IMAGES_PATH + firebaseDBHelper.getLoginName());
    }

    public StorageReference getImagesReference() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        return storage.getReference(Constants.STORAGE_IMAGES_PATH);
    }

    public UploadTask uploadImage(File file, String user, ProgressBar progressBar, TextView progress) {
        Uri uri = Uri.fromFile(file);

        uploadProgressBar = progressBar;
        uploadProgress = progress;

        currentUploadTask = getUserImagesReference(user).child(file.getName()).putFile(uri);
        currentUploadTask.addOnFailureListener(this)
                .addOnPausedListener(this)
                .addOnProgressListener(this)
                .addOnSuccessListener(this);

        return currentUploadTask;
    }


    public void downloadImage(String fileName, String user, ImageView imageView, ProgressBar progressBar, TextView progress) {
        downloadImageView = imageView;
        downloadProgressBar = progressBar;
        downloadProgress = progress;
        if(downloadProgress == null) {
            if(downloadProgressBar != null) {
                downloadProgressBar.setVisibility(View.VISIBLE);
                downloadImageView.setVisibility(View.INVISIBLE);
            } else if(downloadImageView != null) {
                downloadImageView.setImageResource(R.mipmap.ic_launcher);
            }
        }

        try {
            localFile = File.createTempFile("temp", "jpg");
            currentDownloadTask = getUserImagesReference(user).child(fileName).getFile(localFile);
            currentDownloadTask.addOnFailureListener(this)
                    .addOnPausedListener(this)
                    .addOnProgressListener(this)
                    .addOnSuccessListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pauseUploadTask() {
        currentUploadTask.pause();
    }

    public void cancelUploadTask() {
        currentUploadTask.cancel();
    }

    public void resumeUploadTask() {
        currentUploadTask.resume();
    }

    public boolean checkUpload() {
        return uploadProgressBar != null && uploadProgress != null;
    }

    public boolean checkDownload() {
        return downloadProgressBar != null;
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        // Handle unsuccessful uploads
        e.printStackTrace();
        if(checkDownload()) {
            downloadImageView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public void onPaused(Object o) {
        // Handle Pause
        if(checkUpload() && uploadProgress != null) {
            uploadProgress.setText(R.string.paused);
        } else if(checkDownload() && downloadProgress != null) {
            downloadProgress.setText(R.string.paused);
        }
    }

    @Override
    public void onProgress(Object o) {
        if(checkUpload() && uploadProgress != null && uploadProgressBar != null) {
            UploadTask.TaskSnapshot taskSnapshot = (UploadTask.TaskSnapshot) o;
            int progress = (int) ((((double) taskSnapshot.getBytesTransferred()) / ((double) taskSnapshot.getTotalByteCount())) * 100);
            uploadProgressBar.setProgress(progress);
            uploadProgress.setText(progress + "%");
        } else if(checkDownload() && downloadProgress != null && downloadProgressBar != null) {
            FileDownloadTask.TaskSnapshot taskSnapshot = (FileDownloadTask.TaskSnapshot) o;
            int progress = (int) ((((double) taskSnapshot.getBytesTransferred()) / ((double) taskSnapshot.getTotalByteCount())) * 100);
            downloadProgressBar.setProgress(progress);
            downloadProgress.setText(progress + "%");
        }
    }

    public void onSuccess(Object o) {
        if(checkUpload()) {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            UploadTask.TaskSnapshot taskSnapshot = (UploadTask.TaskSnapshot) o;
            Uri downloadUrl = taskSnapshot.getDownloadUrl();
            if(downloadUrl != null) {
                //NFCReadFragment.downloadFilePath = downloadUrl.getLastPathSegment().substring(downloadUrl.getLastPathSegment().lastIndexOf("/"));
                uploadProgress.setText(R.string.finished);
            } else {
                uploadProgress.setText(R.string.fail);
            }
        } else if(checkDownload()) {
            //FileDownloadTask.TaskSnapshot taskSnapshot = (FileDownloadTask.TaskSnapshot) o;
            if(downloadProgress != null) {
                downloadProgress.setText(R.string.finished);
            } else if(downloadProgressBar != null){
                downloadProgressBar.setVisibility(View.INVISIBLE);
                downloadImageView.setVisibility(View.VISIBLE);
            }
            Bitmap image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
            downloadImageView.setImageBitmap(image);
        }
    }
}
