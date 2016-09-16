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

import java.nio.charset.Charset;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class NFCWriteFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int START_WAIT_WRITE_TAG_INTENT = 3;
    private FloatingActionButton fab;
    private EditText tagMessages;

    public NFCWriteFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NFCWriteFragment newInstance(int sectionNumber) {
        NFCWriteFragment fragment = new NFCWriteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == START_WAIT_WRITE_TAG_INTENT && resultCode == Activity.RESULT_OK) {
            Snackbar.make(
                    getView().findViewById(R.id.nfc_write_layout),
                    "TAG read successfully",
                    Snackbar.LENGTH_LONG).show();

            registerPushNFCTag((tagMessages.getText().toString().equals("")) ? "Test" : tagMessages.getText().toString());
        }
    }

    private void registerPushNFCTag(String payload) {

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

        //EditText tagValue = (EditText) rootView.findViewById(R.id.tag_value);
        //EditText tagId = (EditText) rootView.findViewById(R.id.tag_id_value);
        tagMessages = (EditText) rootView.findViewById(R.id.tag_messages_value);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startReadIntent = new Intent(getActivity(), WaitTagActivity.class);
                startActivityForResult(startReadIntent, START_WAIT_WRITE_TAG_INTENT);
            }
        });

        return rootView;
    }
}
