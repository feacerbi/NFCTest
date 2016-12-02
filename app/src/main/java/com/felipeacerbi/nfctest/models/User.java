package com.felipeacerbi.nfctest.models;

import com.google.android.gms.common.api.BooleanResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

    private String username;
    private String idToken;
    private String name;
    private String email;
    private String profilePicture;
    private String age;
    private String description;
    private int gender;
    private boolean online = false;
    private boolean playing = false;
    private Map<String, String> pets = new HashMap<>();
    private Map<String, Boolean> following = new HashMap<>();

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public User(DataSnapshot dataSnapshot) {
        fromMap(dataSnapshot);
    }

    public User(String username, String idToken, String name, String email, String profilePicture, String age, String description, int gender) {
        this.username = username;
        this.idToken = idToken;
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
        this.age = age;
        this.description = description;
        this.gender = gender;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("idToken", idToken);
        result.put("name", name);
        result.put("email", email);
        result.put("profilePicture", profilePicture);
        result.put("age", age);
        result.put("description", description);
        result.put("gender", gender);
        result.put("online", online);
        result.put("playing", playing);

        return result;
    }

    public void fromMap(DataSnapshot dataSnapshot) {
        username = dataSnapshot.getKey();
        idToken = dataSnapshot.child("idToken").getValue(String.class);
        name = dataSnapshot.child("name").getValue(String.class);
        email = dataSnapshot.child("email").getValue(String.class);
        profilePicture = dataSnapshot.child("profilePicture").getValue(String.class);
        age = dataSnapshot.child("age").getValue(String.class);
        description = dataSnapshot.child("description").getValue(String.class);
        if(dataSnapshot.child("gender").getValue() != null) {
            gender = dataSnapshot.child("gender").getValue(Integer.class);
        }
        //online = dataSnapshot.child("online").getValue(Boolean.class);
        //playing = dataSnapshot.child("playing").getValue(Boolean.class);
        GenericTypeIndicator<Map<String, String>> t = new GenericTypeIndicator<Map<String, String>>() {};
        pets = dataSnapshot.child("pets").getValue(t);
        GenericTypeIndicator<Map<String, Boolean>> t2 = new GenericTypeIndicator<Map<String, Boolean>>() {};
        following = dataSnapshot.child("following").getValue(t2);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public Map<String, String> getPets() {
        return pets;
    }

    public void setPets(Map<String, String> pets) {
        this.pets = pets;
    }

    public Map<String, Boolean> getFollowing() {
        return following;
    }

    public void setFollowing(Map<String, Boolean> following) {
        this.following = following;
    }
}
