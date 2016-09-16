package com.felipeacerbi.nfctest.activities;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.models.NFCTag;

public class WaitTagActivity extends AppCompatActivity {

    private ImageView nfcLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_tag);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        nfcLogo = (ImageView) findViewById(R.id.nfc_logo);
        nfcLogo.setImageResource(R.drawable.nfc_logo_fail);

        final TextView cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAction();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelAction();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Create NFC Tag from Intent
        NFCTag nfcTag = getNFCTag(intent);

        if(nfcTag != null) {

            nfcLogo.setImageResource(R.drawable.nfc_logo_ok);

            // Prepare response Intent
            Intent returnIntent = new Intent();
            returnIntent.putExtra("nfc_tag", nfcTag);

            // NFC Tag read and created
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            // Not able to create NFC Tag
            Toast.makeText(this, "NFC Tag not recognized", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelAction() {
        // NFC Tag read canceled
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
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
                nfcTag.setTag((Tag) nfcIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG));

                // Retrieve Tag id.
                nfcTag.setId(Integer.valueOf(nfcIntent.getParcelableExtra(NfcAdapter.EXTRA_ID).toString()));

            } else if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(nfcIntent.getAction())) {
                Toast.makeText(this, "NFC Tag technology not supported", Toast.LENGTH_SHORT).show();
            } else if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(nfcIntent.getAction())) {
                Toast.makeText(this, "NFC Tag not supported", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show();
            }
        }

        return nfcTag;
    }
}
