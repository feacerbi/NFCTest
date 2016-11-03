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
import android.widget.TextView;

import com.felipeacerbi.nfctest.activities.BarcodeCaptureActivity;
import com.felipeacerbi.nfctest.activities.WaitTagActivity;
import com.felipeacerbi.nfctest.firebasemodels.TagDB;
import com.felipeacerbi.nfctest.models.BaseTag;
import com.felipeacerbi.nfctest.models.NFCTag;
import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.models.QRCodeTag;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseHelper;
import com.google.android.gms.vision.barcode.Barcode;

public class NFCReadFragment extends Fragment implements View.OnClickListener {

    // Firebase Helper instance
    private FirebaseHelper firebaseHelper;

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
                    setNFCFields((NFCTag) data.getExtras().getParcelable("nfc_tag"));
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
                            "QR Code read successfully",
                            Snackbar.LENGTH_LONG).show();
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    if(barcode != null) {
                        setQRFields(new QRCodeTag(barcode.displayValue, firebaseHelper.getLoginName()));
                    } else {
                        clearFields();
                    }
                } else {
                    Snackbar.make(
                            getActivity().findViewById(R.id.nfc_read_layout),
                            "QR Code read fail",
                            Snackbar.LENGTH_LONG).show();
                    tagValue.setText(R.string.barcode_failure);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(
                        getActivity().findViewById(R.id.nfc_read_layout),
                        "QR Code read canceled",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void addNewTag(BaseTag tag) {
        firebaseHelper.getTagReference(tag.getId()).setValue(tag.getTagDB());
    }

    public void setNFCFields(NFCTag nfcTag) {
        tagValue.setText(nfcTag.getTag().toString());
        tagMessages.setText(NFCTag.decodePayload(nfcTag.getNdefMessages()[0]));
        tagId.setText(nfcTag.getId());
        addNewTag(nfcTag.setUser(firebaseHelper.getLoginName()));
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
        userNameField.setText(firebaseHelper.getUserName());
        userEmailField.setText(firebaseHelper.getEmail());
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
        firebaseHelper = new FirebaseHelper(getActivity());
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
        fab.setOnClickListener(this);
        fabQR.setOnClickListener(this);
        fabNFC.setOnClickListener(this);

        openAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        closeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        rotateForwardAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward);
        rotateBackAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backwards);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        animateFab();
        switch (view.getId()) {
            case R.id.fab:
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
            default:
        }
    }
}
