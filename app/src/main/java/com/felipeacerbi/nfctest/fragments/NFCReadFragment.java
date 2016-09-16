package com.felipeacerbi.nfctest.fragments;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.activities.WaitTagActivity;
import com.felipeacerbi.nfctest.models.NFCTag;
import com.felipeacerbi.nfctest.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class NFCReadFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int START_WAIT_READ_TAG_INTENT = 2;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == START_WAIT_READ_TAG_INTENT && resultCode == Activity.RESULT_OK) {
            Snackbar.make(
                    getView().findViewById(R.id.nfc_read_layout),
                    "TAG read successfully",
                    Snackbar.LENGTH_LONG).show();
            setNFCFields((NFCTag) data.getExtras().getSerializable("nfc_tag"));
        }
    }

    public void setNFCFields(NFCTag nfcTag) {
        tagValue.setText(nfcTag.getTag().toString());
        tagMessages.setText(nfcTag.getNdefMessagesString());
        tagId.setText(String.valueOf(nfcTag.getId()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_read_nfc, container, false);

        tagValue = (TextView) rootView.findViewById(R.id.tag_value);
        tagMessages = (TextView) rootView.findViewById(R.id.tag_messages_value);
        tagId = (TextView) rootView.findViewById(R.id.tag_id_value);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startReadIntent = new Intent(getActivity(), WaitTagActivity.class);
                startActivityForResult(startReadIntent, START_WAIT_READ_TAG_INTENT);
            }
        });

        return rootView;
    }

}
