package com.felipeacerbi.nfctest.models;

import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.os.Parcel;
import android.os.Parcelable;

import com.felipeacerbi.nfctest.firebasemodels.TagDB;
import com.felipeacerbi.nfctest.utils.Constants;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class NFCTag extends BaseTag implements Parcelable {

    private Tag tag;
    private NdefMessage[] ndefMessages;

    public NFCTag() {
    }

    public NFCTag(Tag tag, NdefMessage[] ndefMessages, String id) {
        super(id);
        this.tag = tag;
        this.ndefMessages = ndefMessages;
    }

    private NFCTag(Parcel in) {
        tag = in.readParcelable(Tag.class.getClassLoader());
        ndefMessages = in.createTypedArray(NdefMessage.CREATOR);
        id = in.readString();
        tagDB = (TagDB) in.readSerializable();
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

    public static String decodePayload(NdefMessage ndefMessage) {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

        byte[] payload = ndefMessage.getRecords()[0].getPayload();

        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 51;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        String resultPayload = "";
        try {
            resultPayload = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return resultPayload;
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
        dest.writeParcelable(getTag(), flags);
        dest.writeTypedArray(getNdefMessages(), flags);
        dest.writeString(getId());
        dest.writeSerializable(getTagDB());
    }
}
