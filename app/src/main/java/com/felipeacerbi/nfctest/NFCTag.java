package com.felipeacerbi.nfctest;

import android.nfc.NdefMessage;
import android.nfc.Tag;

public class NFCTag {

    private Tag tag;
    private NdefMessage[] ndefMessages;
    private int id;

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public NdefMessage[] getNdefMessages() {
        return ndefMessages;
    }

    public void setNdefMessages(NdefMessage[] ndefMessages) {
        this.ndefMessages = ndefMessages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
