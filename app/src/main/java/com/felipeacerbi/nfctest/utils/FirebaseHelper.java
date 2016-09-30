package com.felipeacerbi.nfctest.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseHelper {

    private String loginName;

    public FirebaseUser getFirebaseUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.getCurrentUser();
    }

    public String getUserName() {
        FirebaseUser firebaseUser = getFirebaseUser();
        if(firebaseUser != null) return getFirebaseUser().getDisplayName();
        return null;
    }

    public String getEmail() {
        FirebaseUser firebaseUser = getFirebaseUser();
        if(firebaseUser != null) return getFirebaseUser().getEmail();
        return null;
    }

    public String getLoginName() {
        setLoginName(getEmail());
        return loginName;
    }

    private void setLoginName(String email) {
        if(email != null) {
            loginName = email.substring(0, email.indexOf('@'));
        } else {
            loginName = Constants.LOGIN_ANONYMOUS;
        }
    }

    public void signOut() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
    }
}
