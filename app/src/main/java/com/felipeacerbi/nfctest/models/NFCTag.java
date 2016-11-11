package com.felipeacerbi.nfctest.models;

import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.os.Parcel;
import android.os.Parcelable;

import com.felipeacerbi.nfctest.firebasemodels.BaseTagDB;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NFCTag extends BaseTag implements Parcelable {

    private Tag tag;
    private NdefMessage[] ndefMessages;

    public NFCTag() {
    }

    public NFCTag(Tag tag, NdefMessage[] ndefMessages) {
        this.tag = tag;
        this.ndefMessages = ndefMessages;
    }

    public NFCTag(String id, BaseTagDB baseTagDB) {
        super(id, baseTagDB);
    }

    public NFCTag(String id, BaseTagDB baseTagDB, Tag tag, NdefMessage[] ndefMessages) {
        super(id, baseTagDB);
        this.tag = tag;
        this.ndefMessages = ndefMessages;
    }

    private NFCTag(Parcel in) {
        id = in.readString();
        baseTagDB = (BaseTagDB) in.readSerializable();
        tag = in.readParcelable(Tag.class.getClassLoader());
        ndefMessages = in.createTypedArray(NdefMessage.CREATOR);
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

    public NdefMessage[] getNdefMessages() {
        return ndefMessages;
    }

    public void setNdefMessages(NdefMessage[] ndefMessages) {
        this.ndefMessages = ndefMessages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeSerializable(baseTagDB);
        dest.writeParcelable(getTag(), flags);
        dest.writeTypedArray(getNdefMessages(), flags);
    }
}
