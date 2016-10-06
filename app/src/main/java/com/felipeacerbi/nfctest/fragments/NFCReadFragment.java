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
import android.widget.TextView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.models.NFCTag;
import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.models.NFCTagDB;
import com.felipeacerbi.nfctest.models.UserDB;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

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
                setNFCFields((NFCTag) data.getExtras().getSerializable("nfc_tag"));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(
                        getView().findViewById(R.id.nfc_read_layout),
                        "TAG read canceled",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void setNFCFields(NFCTag nfcTag) {
        tagValue.setText(nfcTag.getTag().toString());
        tagMessages.setText(nfcTag.getNdefMessagesString());
        tagId.setText(String.valueOf(nfcTag.getId()));
    }

    public void setUserInfo() {
        userNameField.setText(firebaseHelper.getUserName());
        userEmailField.setText(firebaseHelper.getEmail());
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

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(getActivity(), "Data read", Toast.LENGTH_SHORT).show();
        /* // Start the activity to wait for Tag interactivity
                Intent startReadIntent = new Intent(getActivity(), WaitTagActivity.class);
                startActivityForResult(startReadIntent, Constants.START_WAIT_READ_TAG_INTENT); */

        // Read from the database
        DatabaseReference userRef = firebaseHelper.getCurrentUserReference();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                UserDB userDB = dataSnapshot.getValue(UserDB.class);
                List<NFCTagDB> dbTags = userDB.getNfcTagDBs();
                if(dbTags != null) {
                    NFCTagDB nfcTagDB = (NFCTagDB) dbTags.get(0);
                    tagValue.setText(nfcTagDB.getTag());
                    tagMessages.setText(nfcTagDB.getNdefMessages().get(0));
                    tagId.setText(nfcTagDB.getId());
                } else {
                    Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                tagValue.setText("Fail");
            }
        });
    }
}
