package com.felipeacerbi.nfctest.firebasemodels;

import com.felipeacerbi.nfctest.models.Pet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BaseTagDB implements Serializable {

    Pet pet;
    Map<String, Boolean> users;

    public BaseTagDB() {
    }

    public BaseTagDB(Pet pet, Map<String, Boolean> users) {
        this.pet = pet;
        this.users = users;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Map<String, Boolean> getUsers() {
        return users;
    }

    public void setUsers(Map<String, Boolean> users) {
        this.users = users;
    }

}
