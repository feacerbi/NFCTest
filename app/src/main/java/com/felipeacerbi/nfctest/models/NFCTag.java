package com.felipeacerbi.nfctest.models;

import android.nfc.NdefMessage;
import android.nfc.Tag;

import java.io.Serializable;

public class NFCTag implements Serializable {

    private Tag tag;
    private NdefMessage[] ndefMessages;
    private int id;

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public String getNdefMessagesString() {
        String messages = "";
        for(NdefMessage ndefMessage : getNdefMessages()) {
            messages += ndefMessage.toString() + "\n";
        }
        return messages;
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
