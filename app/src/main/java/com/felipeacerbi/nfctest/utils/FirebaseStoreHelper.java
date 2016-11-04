package com.felipeacerbi.nfctest.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class FirebaseStoreHelper implements OnFailureListener, OnSuccessListener<UploadTask.TaskSnapshot>,
        OnPausedListener<UploadTask.TaskSnapshot>, OnProgressListener<UploadTask.TaskSnapshot> {

    private UploadTask currentUploadTask;
    private FileDownloadTask currentDownloadTask;

    public StorageReference getImageReference(String name) {
        return getImagesReference().child(name);
    }

    public StorageReference getImagesReference() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        return storage.getReference(Constants.STORAGE_IMAGES_PATH);
    }

    public void uploadImage(File file) {
        Uri uri = Uri.fromFile(file);

        currentUploadTask = getImageReference(file.getName()).putFile(uri);
        currentUploadTask.addOnFailureListener(this);
        currentUploadTask.addOnPausedListener(this);
        currentUploadTask.addOnProgressListener(this);
        currentUploadTask.addOnSuccessListener(this);
    }

    public void downloadImage(final File file, final ImageView imageView) {
        currentDownloadTask = getImageReference(file.getName()).getFile(file);
        currentDownloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageView.setImageResource(R.mipmap.ic_launcher);
            }
        });
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

    @Override
    public void onFailure(@NonNull Exception e) {
        // Handle unsuccessful uploads
        e.printStackTrace();
    }

    @Override
    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
        // Handle Pause
    }

    @Override
    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
        long progress = taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount() * 100;
    }

    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
        Uri downloadUrl = taskSnapshot.getDownloadUrl();
    }
}
