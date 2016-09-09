package com.felipeacerbi.nfctest.fragments;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.models.NFCTag;
import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.interfaces.NFCLaunchableActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class NFCReadFragment extends Fragment {

    NFCLaunchableActivity nfcLaunchableActivity;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView tagValue;
    private TextView tagId;
    private TextView tagMessages;
    private FloatingActionButton fab;

    public NFCReadFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NFCReadFragment newInstance(int sectionNumber) {
        NFCReadFragment fragment = new NFCReadFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private NFCTag getNFCTag(Intent nfcIntent) {
        NFCTag nfcTag = null;
        if (nfcIntent != null) {
            if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(nfcIntent.getAction())) {

                nfcTag = new NFCTag();

                // Retrieve Ndef Messages.
                Parcelable[] rawMsgs = nfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (rawMsgs != null) {
                    NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
                    for (int i = 0; i < rawMsgs.length; i++) {
                        msgs[i] = (NdefMessage) rawMsgs[i];
                    }
                    nfcTag.setNdefMessages(msgs);
                }

                // Retrieve Tag object.
                //nfcTag.setTag((Tag) nfcIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG));

                // Retrieve Tag id.
                //nfcTag.setId(Integer.valueOf(nfcIntent.getParcelableExtra(NfcAdapter.EXTRA_ID).toString()));

            } else if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(nfcIntent.getAction())) {
                Toast.makeText(getActivity(), "NFC Tag technology not supported", Toast.LENGTH_SHORT).show();
            } else if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(nfcIntent.getAction())) {
                Toast.makeText(getActivity(), "NFC Tag not supported", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Unknown error", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "NFC Tag not recognized", Toast.LENGTH_SHORT).show();
        }

        return nfcTag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_read_nfc, container, false);

        tagValue = (TextView) rootView.findViewById(R.id.tag_value);
        tagMessages = (TextView) rootView.findViewById(R.id.tag_messages_value);
        tagId = (TextView) rootView.findViewById(R.id.tag_id_value); // Get NFC Tag from Intent (nullable).

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Place device near an NFC Tag", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                NFCTag nfcTag = getNFCTag(((NFCLaunchableActivity) getActivity()).getNFCIntent());

                // Print Tag info.
                if(nfcTag != null) {
                    Toast.makeText(getActivity(), "NFC read successfully", Toast.LENGTH_SHORT).show();
                    tagValue.setText(nfcTag.getTag().toString());
                    tagMessages.setText(nfcTag.getNdefMessagesString());
                    tagId.setText(String.valueOf(nfcTag.getId()));
                }
            }
        });

        return rootView;
    }

}
