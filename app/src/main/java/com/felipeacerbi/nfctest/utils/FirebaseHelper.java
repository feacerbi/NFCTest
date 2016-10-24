package com.felipeacerbi.nfctest.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.adapters.UsersAdapter;
import com.felipeacerbi.nfctest.firebasemodels.NFCTagDB;
import com.felipeacerbi.nfctest.firebasemodels.RequestDB;
import com.felipeacerbi.nfctest.models.User;
import com.felipeacerbi.nfctest.firebasemodels.UserDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper extends FirebaseInstanceIdService {

    private static final String GAME_TOPIC = "game_topic";
    private String loginName;
    private Context context;

    public FirebaseHelper() {
    }

    public FirebaseHelper(Context context) {
        this.context = context;
    }

    public String getAppIDToken() {
        String token = FirebaseInstanceId.getInstance().getToken();
        // Once a token is generated, we subscribe to topic.
        FirebaseMessaging.getInstance().subscribeToTopic(GAME_TOPIC);
        return token;
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        // Update id token on DB
        DatabaseReference userRef = getCurrentUserReference();
        userRef.child("idToken").setValue(getAppIDToken());
    }

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

    public DatabaseReference getCurrentUserReference() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference(Constants.DATABASE_USERS_PATH + getLoginName());
    }

    public DatabaseReference getUsersReference() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference(Constants.DATABASE_USERS_PATH);
    }

    public DatabaseReference getUserReference(String username) {
        return getUsersReference().child(username);
    }

    public void showResultUsers(final String search, final RecyclerView recyclerView) {
        DatabaseReference users = getUsersReference();
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> usersResultList = new ArrayList<>();

                for(DataSnapshot userReference : dataSnapshot.getChildren()) {
                    UserDB userDB = userReference.getValue(UserDB.class);
                    if(!userReference.getKey().equals(getCurrentUserReference().getKey()) &&
                            (userReference.getKey().toLowerCase().contains(search.toLowerCase()) ||
                            userDB.getName().toLowerCase().contains(search.toLowerCase())))
                        usersResultList.add(new User(userReference.getKey(), userDB));
                }

                if(usersResultList.size() == 0) {
                    Toast.makeText(context, "No users found", Toast.LENGTH_SHORT).show();
                }
                recyclerView.setAdapter(new UsersAdapter(context, usersResultList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public DatabaseReference getGamesReference() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference(Constants.DATABASE_GAMES_PATH);
    }

    public DatabaseReference getGameReference(String gameId) {
        return getGamesReference().child(gameId);
    }

    public void setTurn(String gameId, String player) {
        DatabaseReference turnReference = getGameReference(gameId).child("currentTurn");
        turnReference.setValue(player);
    }

    public void deleteGame(String gameId) {
        getGameReference(gameId).removeValue();
    }

    public void updateGamePlace(String gameId, int place, int marker) {
        getGameReference(gameId).child("places").child(String.valueOf(place)).setValue(marker);
    }

    public DatabaseReference getRequestsReference() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference(Constants.DATABASE_REQUESTS_PATH);
    }

    public DatabaseReference getRequestReference(String requestId) {
        return getRequestsReference().child(requestId);
    }

    public void deleteRequest(String requestId) {
        getRequestReference(requestId).removeValue();
    }

    public void signOut() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
    }
}
