package com.felipeacerbi.nfctest.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.activities.WaitTagActivity;
import com.felipeacerbi.nfctest.models.tags.NFCTag;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.felipeacerbi.nfctest.utils.FirebaseStoreHelper;
import com.felipeacerbi.nfctest.utils.NFCUtils;

import java.io.File;
import java.util.Locale;

public class NFCWriteFragment extends Fragment implements View.OnClickListener {

    // Firebase Helper instance
    private FirebaseDBHelper firebaseDBHelper;

    private EditText tagMessages;
    private ProgressBar uploadProgressBar;
    private TextView uploadProgress;

    public NFCWriteFragment() {
    }

    public static NFCWriteFragment newInstance(int sectionNumber) {
        NFCWriteFragment fragment = new NFCWriteFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseStoreHelper firebaseStoreHelper = new FirebaseStoreHelper();

        // Handle response from WaitTagActivity
        if(requestCode == Constants.START_WAIT_WRITE_TAG_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                Snackbar.make(
                        getActivity().findViewById(R.id.nfc_write_layout),
                        R.string.nfc_write_success,
                        Snackbar.LENGTH_LONG).show();
                NFCTag nfcTag = data.getExtras().getParcelable(Constants.NFC_TAG_EXTRA);
                // Set the content message to send to the Tag
                writeNFCTag(nfcTag, (tagMessages.getText().toString().equals("")) ? "Test" : tagMessages.getText().toString());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(
                        getActivity().findViewById(R.id.nfc_write_layout),
                        R.string.nfc_write_canceled,
                        Snackbar.LENGTH_LONG).show();
            }
        } else if(requestCode == Constants.GET_IMAGE_FROM_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                String path = getBitmapPath(data);
                firebaseStoreHelper.uploadImage(new File(path), "-KXG849lebtS6ZNbWmOK", uploadProgressBar, uploadProgress);
            }
        }
    }

    private void writeNFCTag(NFCTag nfcTag, String payload) {
        // Create Ndef message
        NdefMessage ndefMessage = new NdefMessage(
                NFCUtils.createTextRecord(payload, Locale.getDefault(), true),   // Create a TNF_WELL_KNOWN NDEF Record
                NdefRecord.createApplicationRecord(getActivity().getPackageName())); // Include Android Application Record (AAR)

        if (nfcTag.getTag() != null) {
            try {
                Ndef ndefTag = Ndef.get(nfcTag.getTag());
                if (ndefTag == null) {
                    // Let's try to format the Tag in NDEF
                    NdefFormatable nForm = NdefFormatable.get(nfcTag.getTag());
                    if (nForm != null) {
                        nForm.connect();
                        nForm.format(ndefMessage);
                        nForm.close();
                    }
                } else {
                    ndefTag.connect();
                    ndefTag.writeNdefMessage(ndefMessage);
                    ndefTag.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseDBHelper = new FirebaseDBHelper(getActivity());
        firebaseDBHelper.registerUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_write_nfc, container, false);

        tagMessages = (EditText) rootView.findViewById(R.id.tag_write_messages_value);
        uploadProgressBar = (ProgressBar) rootView.findViewById(R.id.upload_progress_bar);
        uploadProgress = (TextView) rootView.findViewById(R.id.upload_message);
        Button uploadButton = (Button) rootView.findViewById(R.id.upload_image_button);
        uploadButton.setOnClickListener(this);

        return rootView;
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
            ActivityCompat.requestPermissions(getActivity(), permissions, Constants.WRITE_EXTERNAL_STORAGE_PERMISSION);
        } else {
            Toast.makeText(getActivity(), R.string.allow_write_storage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == Constants.WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPickImageActivity();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                // Write NFC Tag
                Intent startReadIntent = new Intent(getActivity(), WaitTagActivity.class);
                startActivityForResult(startReadIntent, Constants.START_WAIT_WRITE_TAG_INTENT);
                break;
            case R.id.upload_image_button:
                int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (rc == PackageManager.PERMISSION_GRANTED) {
                    startPickImageActivity();
                } else {
                    requestStoragePermission();
                }
                break;
        }
    }
}
