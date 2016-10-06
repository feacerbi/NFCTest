package com.felipeacerbi.nfctest.models;

import android.nfc.NdefMessage;
import android.nfc.Tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NFCTagDB implements Serializable {

    private String tag;
    private List<String> ndefMessages;
    private String id;

    public NFCTagDB() {

    }

    private NFCTagDB(String tag, List<String> ndefMessages, String id) {
        this.tag = tag;
        this.ndefMessages = ndefMessages;
        this.id = id;
    }

    public static NFCTagDB createDBTag(NFCTag nfcTag) {
        return new NFCTagDB(
                nfcTag.getTag().toString(),
                nfcTag.getNdefMessagesArray(),
                String.valueOf(nfcTag.getId()));
    }

    public static NFCTagDB createTestDBTag(String test) {
        List<String> temp = new ArrayList<>();
        temp.add("test");
        temp.add("test");
        return new NFCTagDB(
                test,
                temp,
                test);
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
}
