package com.felipeacerbi.nfctest.firebasemodels;

import com.felipeacerbi.nfctest.models.Pet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BaseTagDB implements Serializable {

    String pet;

    public BaseTagDB() {
    }

    public BaseTagDB(String pet) {
        this.pet = pet;
    }

    public String getPet() {
        return pet;
    }

    public void setPet(String pet) {
        this.pet = pet;
    }

}
