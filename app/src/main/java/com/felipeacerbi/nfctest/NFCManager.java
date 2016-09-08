package com.felipeacerbi.nfctest;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.Locale;

public class NFCManager extends AppCompatActivity {

    private Intent nfcIntent;
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcmanager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Check for available NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        // Register fixed callback
        registerPushNFCTag("Test");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Retrieve NFC Intent
        nfcIntent = getIntent();

        // Get NFC Tag from Intent (nullable)
        NFCTag nfcTag = getNFCTag();

        // Print Tag info
        if(nfcTag != null) {
            Toast.makeText(this,
                    "Id: " + nfcTag.getId() +
                    "\nTag: " + nfcTag.getTag().toString() +
                    "\nMessage: " + nfcTag.getNdefMessages()[0], Toast.LENGTH_SHORT).show();
        }
    }

    private NFCTag getNFCTag() {
        NFCTag nfcTag = null;
        if (nfcIntent != null) {
            if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(nfcIntent.getAction())) {

                nfcTag = new NFCTag();

                // Retrieve Ndef Messages
                Parcelable[] rawMsgs = nfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (rawMsgs != null) {
                    NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
                    for (int i = 0; i < rawMsgs.length; i++) {
                        msgs[i] = (NdefMessage) rawMsgs[i];
                    }
                    nfcTag.setNdefMessages(msgs);
                }

                // Retrieve Tag object
                nfcTag.setTag((Tag) nfcIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG));

                // Retrieve Tag id
                nfcTag.setId(Integer.valueOf(nfcIntent.getParcelableExtra(NfcAdapter.EXTRA_ID).toString()));

            } else if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(nfcIntent.getAction())) {
                Toast.makeText(this, "NFC Tag technology not supported.", Toast.LENGTH_SHORT).show();
            } else if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(nfcIntent.getAction())) {
                Toast.makeText(this, "NFC Tag not supported.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unknown error.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "NFC Tag not recognized.", Toast.LENGTH_SHORT).show();
        }

        return nfcTag;
    }

    private void registerPushNFCTag(String payload) {

        NdefMessage ndefMessage = new NdefMessage(
                createTextRecord(payload, Locale.getDefault(), true),   // Create a TNF_WELL_KNOWN NDEF Record
                NdefRecord.createApplicationRecord(getPackageName())); // Android Application Record (AAR)

        // Register NFC push message
        nfcAdapter.setNdefPushMessage(ndefMessage, this);
    }

    public NdefRecord createTextRecord(String payload, Locale locale, boolean encodeInUtf8) {
        // Handle Locale
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        // Handle Encoding
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");

        // Payload to bytes
        byte[] textBytes = payload.getBytes(utfEncoding);

        // Create status
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        // Create message
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        // Create new Ndef Record
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nfcmanager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
