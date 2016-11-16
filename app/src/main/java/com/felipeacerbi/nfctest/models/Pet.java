package com.felipeacerbi.nfctest.models;

import com.felipeacerbi.nfctest.models.tags.BaseTag;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Pet implements Serializable {

    private String tag;
    private String name;
    private int type;
    Map<String, Boolean> users;

    public Pet() {
    }

    public Pet(String tag, String name, int type, Map<String, Boolean> users) {
        this.tag = tag;
        this.name = name;
        this.type = type;
        this.users = users;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("tag", tag);
        result.put("name", name);
        result.put("type", type);
        result.put("users", users);

        return result;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Map<String, Boolean> getUsers() {
        return users;
    }
}
