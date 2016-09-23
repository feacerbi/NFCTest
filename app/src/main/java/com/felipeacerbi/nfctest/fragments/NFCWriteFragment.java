package com.felipeacerbi.nfctest.fragments;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.activities.WaitTagActivity;
import com.felipeacerbi.nfctest.models.NFCTag;
import com.felipeacerbi.nfctest.utils.Constants;

import java.nio.charset.Charset;
import java.util.Locale;

public class NFCWriteFragment extends Fragment {

    private FloatingActionButton fab;
    private EditText tagMessages;

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
                        getView().findViewById(R.id.nfc_write_layout),
                        "TAG written successfully",
                        Snackbar.LENGTH_LONG).show();
                // Set the content message to send to the Tag
                registerPushNFCTag((tagMessages.getText().toString().equals("")) ? "Test" : tagMessages.getText().toString());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(
                        getView().findViewById(R.id.nfc_write_layout),
                        "TAG write canceled",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void registerPushNFCTag(String payload) {
        // Create Ndef message
        NdefMessage ndefMessage = new NdefMessage(
                createTextRecord(payload, Locale.getDefault(), true),   // Create a TNF_WELL_KNOWN NDEF Record
                NdefRecord.createApplicationRecord(getActivity().getPackageName())); // Include Android Application Record (AAR)

        // Register NFC push message.
        NfcAdapter.getDefaultAdapter(getActivity()).setNdefPushMessage(ndefMessage, getActivity());
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_write_nfc, container, false);

        tagMessages = (EditText) rootView.findViewById(R.id.tag_messages_value);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startReadIntent = new Intent(getActivity(), WaitTagActivity.class);
                startActivityForResult(startReadIntent, Constants.START_WAIT_WRITE_TAG_INTENT);
            }
        });

        return rootView;
    }
}
