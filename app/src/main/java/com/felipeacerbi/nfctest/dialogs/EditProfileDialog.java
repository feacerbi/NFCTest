package com.felipeacerbi.nfctest.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public abstract class EditProfileDialog extends DialogFragment implements OnSuccessListener<UploadTask.TaskSnapshot> {

    FirebaseDBHelper firebaseDBHelper;
    ProgressBar progressBar;
    TextView progress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDBHelper = new FirebaseDBHelper();
    }

    public void onClickPhoto() {
        int rc = ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            startPickImageActivity();
        } else {
            requestStoragePermission();
        }
    }

    private void startPickImageActivity() {
        Intent gal = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(gal.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(gal, Constants.GET_IMAGE_FROM_PICKER);
        } else {
            Toast.makeText(getActivity(), R.string.no_gallery, Toast.LENGTH_SHORT).show();
        }
    }

    private void requestStoragePermission() {
        final String[] permissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), "Permission Should", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), permissions, Constants.WRITE_EXTERNAL_STORAGE_PERMISSION);
        } else {
            Toast.makeText(getActivity(), R.string.allow_write_storage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.GET_IMAGE_FROM_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                File path = new File(getBitmapPath(data));
                resultOk(path);
            }
        }
    }

    public abstract void resultOk(File path);

    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
        Uri downloadUrl = taskSnapshot.getDownloadUrl();
        if(downloadUrl != null) {
            successUrl(downloadUrl);
        }
    }

    public abstract void successUrl(Uri downloadUrl);

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
}
