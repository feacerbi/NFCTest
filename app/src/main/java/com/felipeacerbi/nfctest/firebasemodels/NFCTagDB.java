package com.felipeacerbi.nfctest.firebasemodels;

import android.nfc.NdefMessage;

import com.felipeacerbi.nfctest.models.NFCTag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NFCTagDB implements Serializable {

    private String tag;
    private List<String> ndefMessages;
    private String id;
    private String payload;

    public NFCTagDB() {

    }

    private NFCTagDB(String tag, List<String> ndefMessages, String id) {
        this.tag = tag;
        this.ndefMessages = ndefMessages;
        this.id = id;
    }

    private NFCTagDB(String tag, List<String> ndefMessages, String id, String payload) {
        this.tag = tag;
        this.ndefMessages = ndefMessages;
        this.id = id;
        this.payload = payload;
    }

    public static NFCTagDB createDBTag(NFCTag nfcTag) {
        return new NFCTagDB(
                (nfcTag.getTag() == null) ? "" : nfcTag.getTag().toString(),
                nfcTag.getNdefMessagesArray(),
                String.valueOf(nfcTag.getId()),
                NFCTag.decodePayload(nfcTag.getNdefMessages()[0]));
    }

    public String getTag() {
        return tag;
    }

    public List<String> getNdefMessages() {
        return ndefMessages;
    }

    public String getId() {
        return id;
    }

    public String getPayload() {
        return payload;
    }
}
