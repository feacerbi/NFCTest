package com.felipeacerbi.nfctest.models;

import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NFCTag implements Parcelable {

    private Tag tag;
    private NdefMessage[] ndefMessages;
    private String id;

    public NFCTag() {
    }

    public NFCTag(Tag tag, NdefMessage[] ndefMessages, String id) {
        this.tag = tag;
        this.ndefMessages = ndefMessages;
        this.id = id;
    }

    private NFCTag(Parcel in) {
        tag = in.readParcelable(Tag.class.getClassLoader());
        ndefMessages = in.createTypedArray(NdefMessage.CREATOR);
        id = in.readString();
    }

    public static final Creator<NFCTag> CREATOR = new Creator<NFCTag>() {
        @Override
        public NFCTag createFromParcel(Parcel in) {
            return new NFCTag(in);
        }

        @Override
        public NFCTag[] newArray(int size) {
            return new NFCTag[size];
        }
    };

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public List<String> getNdefMessagesArray() {
        List<String> messages = new ArrayList<>();
        for(NdefMessage ndefMessage : getNdefMessages()) {
            messages.add(ndefMessage.toString());
        }
        return messages;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(getTag(), flags);
        dest.writeTypedArray(getNdefMessages(), flags);
        dest.writeString(getId());
    }
}
