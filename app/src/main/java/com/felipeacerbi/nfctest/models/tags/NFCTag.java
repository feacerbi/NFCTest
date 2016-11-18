package com.felipeacerbi.nfctest.models.tags;

import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class NFCTag extends BaseTag implements Parcelable {

    private Tag tag;
    private NdefMessage[] ndefMessages;

    public NFCTag() {
    }

    public NFCTag(String id) {
        super(id);
    }

    private NFCTag(Parcel in) {
        Map<String, Object> temp = new HashMap<>();
        in.readMap(temp, Map.class.getClassLoader());
        fromMap(temp);

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(toMap());
        dest.writeParcelable(getTag(), flags);
        dest.writeTypedArray(getNdefMessages(), flags);
    }

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
}
