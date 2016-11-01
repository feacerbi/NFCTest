package com.felipeacerbi.nfctest.fragments;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.tech.Ndef;
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
import android.widget.Toast;

import com.felipeacerbi.nfctest.activities.BarcodeCaptureActivity;
import com.felipeacerbi.nfctest.activities.WaitTagActivity;
import com.felipeacerbi.nfctest.models.NFCTag;
import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.firebasemodels.NFCTagDB;
import com.felipeacerbi.nfctest.firebasemodels.UserDB;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseHelper;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.List;

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
                        getView().findViewById(R.id.nfc_read_layout),
                        "TAG read successfully",
                        Snackbar.LENGTH_LONG).show();
                // Set text fields with Tag information
                if(data.getExtras() != null) {
                    setNFCFields((NFCTag) data.getExtras().getParcelable("nfc_tag"));
                } else {
                    clearFields();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(
                        getView().findViewById(R.id.nfc_read_layout),
                        "TAG read canceled",
                        Snackbar.LENGTH_LONG).show();
            }
        } else if(requestCode == Constants.RC_BARCODE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Snackbar.make(
                            getView().findViewById(R.id.nfc_read_layout),
                            "QR Code read successfully",
                            Snackbar.LENGTH_LONG).show();
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    tagValue.setText(R.string.barcode_success);
                    tagMessages.setText(barcode.displayValue);
                } else {
                    Snackbar.make(
                            getView().findViewById(R.id.nfc_read_layout),
                            "QR Code read fail",
                            Snackbar.LENGTH_LONG).show();
                    tagValue.setText(R.string.barcode_failure);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(
                        getView().findViewById(R.id.nfc_read_layout),
                        "QR Code read canceled",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private String decodePayload(NdefMessage ndefMessage) {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

        byte[] payload = ndefMessage.getRecords()[0].getPayload();

        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        String resultPayload = "";
        try {
            resultPayload = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return resultPayload;
    }

    public void setNFCFields(NFCTag nfcTag) {
        tagValue.setText(nfcTag.getTag().toString());
        tagMessages.setText(decodePayload(nfcTag.getNdefMessages()[0]));
        tagId.setText(String.valueOf(nfcTag.getId()));
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

    public void animateFAB() {
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
        animateFAB();
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
