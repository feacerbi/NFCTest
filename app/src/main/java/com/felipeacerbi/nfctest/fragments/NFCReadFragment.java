package com.felipeacerbi.nfctest.fragments;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.activities.BarcodeCaptureActivity;
import com.felipeacerbi.nfctest.activities.WaitTagActivity;
import com.felipeacerbi.nfctest.firebasemodels.BaseTagDB;
import com.felipeacerbi.nfctest.models.BaseTag;
import com.felipeacerbi.nfctest.models.NFCTag;
import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.models.QRCodeTag;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.felipeacerbi.nfctest.utils.FirebaseStoreHelper;
import com.felipeacerbi.nfctest.utils.NFCUtils;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class NFCReadFragment extends Fragment implements View.OnClickListener {

    // Firebase Helper instance
    private FirebaseDBHelper firebaseDBHelper;

    private TextView tagValue;
    private TextView tagId;
    private TextView tagMessages;
    private TextView userNameField;
    private TextView userEmailField;
    private FloatingActionButton fab;
    private FloatingActionButton fabNFC;
    private FloatingActionButton fabQR;
    private Animation openAnimation;
    private Animation closeAnimation;
    private Animation rotateForwardAnimation;
    private Animation rotateBackAnimation;
    private boolean isFabOpen = false;
    private ImageView downloadedImage;
    private FirebaseStoreHelper firebaseStoreHelper;
    private TextView downloadProgress;
    private ProgressBar downloadProgressBar;

    public static String downloadFilePath = "test.jpg";

    public NFCReadFragment() {
    }

    public static NFCReadFragment newInstance(int sectionNumber) {
        NFCReadFragment fragment = new NFCReadFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle response from WaitTagActivity
        if(requestCode == Constants.START_WAIT_READ_TAG_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                Snackbar.make(
                        getActivity().findViewById(R.id.nfc_read_layout),
                        R.string.tag_read_success,
                        Snackbar.LENGTH_LONG).show();
                // Set text fields with Tag information
                if(data.getExtras() != null) {
                    setNFCFields((NFCTag) data.getExtras().getParcelable(Constants.NFC_TAG_EXTRA));
                } else {
                    clearFields();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(
                        getActivity().findViewById(R.id.nfc_read_layout),
                        R.string.tag_read_cancel,
                        Snackbar.LENGTH_LONG).show();
            }
        } else if(requestCode == Constants.RC_BARCODE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Snackbar.make(
                            getActivity().findViewById(R.id.nfc_read_layout),
                            R.string.qrcode_read_success,
                            Snackbar.LENGTH_LONG).show();
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    if(barcode != null) {
                        setQRFields(new QRCodeTag(barcode.displayValue, new BaseTagDB()));
                    } else {
                        clearFields();
                    }
                } else {
                    Snackbar.make(
                            getActivity().findViewById(R.id.nfc_read_layout),
                            R.string.qrcode_read_fail,
                            Snackbar.LENGTH_LONG).show();
                    tagValue.setText(R.string.barcode_failure);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(
                        getActivity().findViewById(R.id.nfc_read_layout),
                        R.string.qrcode_read_canceled,
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void addNewTag(BaseTag tag) {
        // Map<String, Object> tagValues = tag.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        /* childUpdates.put(Constants.DATABASE_TAGS_CHILD // Set TAG Info
                + tag.getId(),
                tagValues); */
        childUpdates.put(Constants.DATABASE_TAGS_CHILD // Add current user to TAG
                + tag.getId()
                + Constants.DATABASE_USERS_CHILD
                + firebaseDBHelper.getLoginName(),
                true);
        childUpdates.put(Constants.DATABASE_USERS_CHILD // Add TAG to current user
                + firebaseDBHelper.getLoginName()
                + Constants.DATABASE_TAGS_CHILD
                + tagId,
                true);

        firebaseDBHelper.getDatabase().updateChildren(childUpdates);
    }

    public void setNFCFields(NFCTag nfcTag) {
        tagValue.setText(nfcTag.getTag().toString());
        tagMessages.setText(NFCUtils.decodePayload(nfcTag.getNdefMessages()[0]));
        tagId.setText(nfcTag.getId());

        addNewTag(nfcTag);
    }

    public void setQRFields(QRCodeTag qrCodeTag) {
        tagValue.setText(R.string.barcode_success);
        tagId.setText(qrCodeTag.getId());

        addNewTag(qrCodeTag);
    }

    public void clearFields() {
        tagValue.setText("");
        tagMessages.setText("");
        tagId.setText("");
    }

    public void setUserInfo() {
        userNameField.setText(firebaseDBHelper.getUserName());
        userEmailField.setText(firebaseDBHelper.getEmail());
    }

    public void animateFab() {
        if(isFabOpen) {
            fab.startAnimation(rotateBackAnimation);
            fabNFC.startAnimation(closeAnimation);
            fabNFC.setClickable(false);
            fabQR.startAnimation(closeAnimation);
            fabQR.setClickable(false);
            isFabOpen = false;
        } else {
            fab.startAnimation(rotateForwardAnimation);
            fabNFC.startAnimation(openAnimation);
            fabNFC.setClickable(true);
            fabQR.startAnimation(openAnimation);
            fabQR.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseDBHelper = new FirebaseDBHelper(getActivity());
        firebaseStoreHelper = new FirebaseStoreHelper();
        setUserInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_read_nfc, container, false);

        tagValue = (TextView) rootView.findViewById(R.id.tag_value);
        tagMessages = (TextView) rootView.findViewById(R.id.tag_messages_value);
        tagId = (TextView) rootView.findViewById(R.id.tag_id_value);

        // Get User info bar
        userNameField = (TextView) rootView.findViewById(R.id.user_name);
        userEmailField = (TextView) rootView.findViewById(R.id.user_email);

        fabNFC = (FloatingActionButton) getActivity().findViewById(R.id.fabNFC);
        fabQR = (FloatingActionButton) getActivity().findViewById(R.id.fabQR);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fabQR.setOnClickListener(this);
        fabNFC.setOnClickListener(this);

        openAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        closeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        rotateForwardAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward);
        rotateBackAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backwards);

        downloadProgressBar = (ProgressBar) rootView.findViewById(R.id.download_progress_bar);
        downloadProgress = (TextView) rootView.findViewById(R.id.download_message);
        downloadedImage = (ImageView) rootView.findViewById(R.id.downloaded_image);
        Button downloadButton = (Button) rootView.findViewById(R.id.download_image_button);
        downloadButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                animateFab();
                break;
            case R.id.fabNFC:
                // Start the activity to wait for Tag interactivity
                Intent startReadIntent = new Intent(getActivity(), WaitTagActivity.class);
                startActivityForResult(startReadIntent, Constants.START_WAIT_READ_TAG_INTENT);
                break;
            case R.id.fabQR:
                // Launch barcode activity.
                Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, Constants.RC_BARCODE_CAPTURE);
                break;
            case R.id.download_image_button:
                firebaseStoreHelper.downloadImage(new File(downloadFilePath), downloadedImage, downloadProgressBar, downloadProgress);
                break;
            default:
        }
    }
}
