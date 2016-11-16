package com.felipeacerbi.nfctest.models.tags;

import com.felipeacerbi.nfctest.firebasemodels.BaseTagDB;

public class QRCodeTag extends BaseTag {

    public QRCodeTag() {
    }

    public QRCodeTag(String id, BaseTagDB baseTagDB) {
        super(id, baseTagDB);
    }
}
