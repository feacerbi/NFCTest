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
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.activities.WaitTagActivity;
import com.felipeacerbi.nfctest.models.NFCTag;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.felipeacerbi.nfctest.utils.FirebaseStoreHelper;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Locale;

public class NFCWriteFragment extends Fragment implements View.OnClickListener {

    // Firebase Helper instance
    private FirebaseDBHelper firebaseDBHelper;

    private EditText tagMessages;
    private Button uploadButton;
    private FirebaseStoreHelper firebaseStoreHelper;

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
        } else if(requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                String path = getBitmapPath(data);
                firebaseStoreHelper.uploadImage(new File(path));
            }
        } else if(requestCode == 1) {
            startPickImageActivity();
        }
    }

    private void writeNFCTag(NFCTag nfcTag, String payload) {
        // Create Ndef message
        NdefMessage ndefMessage = new NdefMessage(
                createTextRecord(payload, Locale.getDefault(), true),   // Create a TNF_WELL_KNOWN NDEF Record
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

    public NdefRecord createTextRecord(String payload, Locale locale, boolean encodeInUtf8) {
        // Handle Locale.
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        // Handle Encoding.
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");

        // Payload to bytes.
        byte[] textBytes = payload.getBytes(utfEncoding);

        // Create status.
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        // Create message.
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        // Create new Ndef Record.
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
    }

    public String getBitmapPath(Intent data){
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        return picturePath;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseDBHelper = new FirebaseDBHelper(getActivity());
        firebaseStoreHelper = new FirebaseStoreHelper();
        firebaseDBHelper.registerUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_write_nfc, container, false);

        tagMessages = (EditText) rootView.findViewById(R.id.tag_write_messages_value);
        uploadButton = (Button) rootView.findViewById(R.id.upload_image_button);
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

        return rootView;
    }

    private void startPickImageActivity() {
        Intent gal = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(gal.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(gal, 0);

        } else {
            Toast.makeText(getActivity(), "No Gallery app", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestStoragePermission() {
        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
            return;
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(getActivity(), permissions,
                        1);
            }
        };
    }

    @Override
    public void onClick(View view) {
        // Write NFC Tag
        Intent startReadIntent = new Intent(getActivity(), WaitTagActivity.class);
        startActivityForResult(startReadIntent, Constants.START_WAIT_WRITE_TAG_INTENT);
    }
}
