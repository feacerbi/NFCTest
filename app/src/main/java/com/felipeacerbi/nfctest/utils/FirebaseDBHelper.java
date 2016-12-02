package com.felipeacerbi.nfctest.utils;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.dialogs.PetDialog;
import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.game.UsersAdapter;
import com.felipeacerbi.nfctest.models.Pet;
import com.felipeacerbi.nfctest.models.User;
import com.felipeacerbi.nfctest.models.tags.BaseTag;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDBHelper extends FirebaseInstanceIdService {

    //private static final String GAME_TOPIC = "game_topic";
    private String loginName;
    private Context context;

    public FirebaseDBHelper() {
    }

    public FirebaseDBHelper(Context context) {
        this.context = context;
    }

    public String getAppIDToken() {
        // Once a token is generated, we subscribe to topic.
        //FirebaseMessaging.getInstance().subscribeToTopic(GAME_TOPIC);
        return FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        // Update id token on DB
        DatabaseReference userRef = getCurrentUserReference();
        userRef.child(Constants.DATABASE_IDTOKEN_CHILD).setValue(getAppIDToken());
    }

    public DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance().getReference();
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

    public Uri getProfilePicture() {
        FirebaseUser firebaseUser = getFirebaseUser();
        if(firebaseUser != null) return getFirebaseUser().getPhotoUrl();
        return null;
    }

    public String getLoginName() {
        setLoginName(getEmail());
        return loginName;
    }

    private void setLoginName(String email) {
        if(email != null) {
            String[] parts = email.split("@");
            loginName = parts[0] + parts[1].replace(".", "");
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

    public void registerUser() {
        // Insert user on DB

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(Constants.DATABASE_USERS_PATH + "/"
                + getLoginName() + "/"
                + Constants.DATABASE_IDTOKEN_CHILD,
                getAppIDToken());
        childUpdates.put(Constants.DATABASE_USERS_PATH + "/"
                + getLoginName() + "/"
                + Constants.DATABASE_NAME_CHILD,
                getUserName());
        childUpdates.put(Constants.DATABASE_USERS_PATH + "/"
                + getLoginName() + "/"
                + Constants.DATABASE_EMAIL_CHILD,
                getEmail());

        getDatabase().updateChildren(childUpdates);
    }

    public void updateUser(User user) {
        // Insert user on DB

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(Constants.DATABASE_USERS_PATH + "/"
                        + getLoginName() + "/"
                        + Constants.DATABASE_AGE_CHILD,
                        user.getAge());
        childUpdates.put(Constants.DATABASE_USERS_PATH + "/"
                        + getLoginName() + "/"
                        + Constants.DATABASE_GENDER_CHILD,
                        user.getGender());
        childUpdates.put(Constants.DATABASE_USERS_PATH + "/"
                        + getLoginName() + "/"
                        + Constants.DATABASE_DESCRIPTION_CHILD,
                        user.getDescription());
        childUpdates.put(Constants.DATABASE_USERS_PATH + "/"
                        + getLoginName() + "/"
                        + Constants.DATABASE_PROFILE_PICTURE_CHILD,
                        user.getProfilePicture());

        getDatabase().updateChildren(childUpdates);
    }

    public void showResultUsers(final String search, final RecyclerView recyclerView) {
        DatabaseReference users = getUsersReference();
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> usersResultList = new ArrayList<>();

                for(DataSnapshot userReference : dataSnapshot.getChildren()) {
                    User user = new User(userReference);
                    if(!userReference.getKey().equals(getCurrentUserReference().getKey()) &&
                            (userReference.getKey().toLowerCase().contains(search.toLowerCase()) ||
                            user.getName().toLowerCase().contains(search.toLowerCase())))
                        usersResultList.add(user);
                }

                if(usersResultList.size() == 0) {
                    Toast.makeText(context, R.string.no_users, Toast.LENGTH_SHORT).show();
                }
                recyclerView.setAdapter(new UsersAdapter(context, usersResultList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public DatabaseReference getPetsReference() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference(Constants.DATABASE_PETS_PATH);
    }

    public DatabaseReference getPetReference(String petId) {
        return getPetsReference().child(petId);
    }

    public void addPet(final BaseTag tag, final boolean isNew) {
        getTagsReference().orderByKey().equalTo(tag.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean found = false;
                BaseTag foundTag = tag;

                for (DataSnapshot tagSnapshot: dataSnapshot.getChildren()) {
                    found = true;
                    foundTag = new BaseTag(tagSnapshot);
                }

                if(isNew) {
                    if(found) {
                        addNewPet(foundTag, null, true);
                    } else {
                        PetDialog petDialog = PetDialog.newInstance(foundTag, false);
                        petDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "newPet");
                    }
                    Toast.makeText(context, "Buddy added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    if(found) {
                        addFollowPet(foundTag);
                        Toast.makeText(context, "New following added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Pet not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addNewPet(BaseTag tag, Pet pet, Boolean exists) {
        if(!exists && pet != null) {
            tag.setPet(pet.getId());
            tag.setPetName(pet.getName());
            pet.setTag(tag.getId());
        }

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(Constants.DATABASE_USERS_CHILD + "/" // Add TAG to current user
                        + getLoginName() + "/"
                        + Constants.DATABASE_BUDDIES_CHILD + "/"
                        + tag.getPet(),
                        tag.getPetName());
        if(exists) {
            childUpdates.put(Constants.DATABASE_PETS_PATH + "/"
                            + tag.getPet() + "/"
                            + Constants.DATABASE_OWNERS_CHILD + "/"
                            + getLoginName(),
                            true);
        } else if(pet != null) {
            pet.getOwners().put(getLoginName(), true);
            childUpdates.put(Constants.DATABASE_TAGS_CHILD + "/" // Add current user to TAG
                            + tag.getId(),
                            tag.toMap());

            childUpdates.put(Constants.DATABASE_PETS_PATH + "/"
                            + tag.getPet(),
                            pet.toMap());
        }

        getDatabase().updateChildren(childUpdates);
    }

    private void addFollowPet(final BaseTag tag) {

        getPetReference(tag.getPet()).child(Constants.DATABASE_POSTS_CHILD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> childUpdates = new HashMap<>();

                childUpdates.put(Constants.DATABASE_USERS_PATH + "/" // Add TAG to current user
                                + getLoginName() + "/"
                                + Constants.DATABASE_FOLLOWING_CHILD + "/"
                                + tag.getPet(),
                                 true);

                childUpdates.put(Constants.DATABASE_PETS_PATH + "/" // Add TAG to current user
                                + tag.getPet() + "/"
                                + Constants.DATABASE_FOLLOWERS_CHILD + "/"
                                + getLoginName(),
                                true);

                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    childUpdates.put(Constants.DATABASE_USERS_PATH + "/"
                                    + getLoginName() + "/"
                                    + Constants.DATABASE_FEED_CHILD + "/"
                                    + post.getKey(),
                                     post.getValue());
                }

                getDatabase().updateChildren(childUpdates);
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
        DatabaseReference turnReference = getGameReference(gameId).child(Constants.DATABASE_CURRENT_TURN_CHILD);
        turnReference.setValue(player);
    }

    public void deleteGame(String gameId) {
        getGameReference(gameId).removeValue();
    }

    public void updateGamePlace(String gameId, int place, int marker) {
        getGameReference(gameId).child(Constants.DATABASE_PLACES_CHILD).child(String.valueOf(place)).setValue(marker);
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

    public DatabaseReference getTagsReference() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference(Constants.DATABASE_TAGS_PATH);
    }

    public DatabaseReference getTagReference(String tagId) {
        return getTagsReference().child(tagId);
    }

    public DatabaseReference getPostsReference() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference(Constants.DATABASE_POSTS_PATH);
    }

    public DatabaseReference getPostReference(String id) {
        return getPostsReference().child(id);
    }

    public DatabaseReference getCommentsReference() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference(Constants.DATABASE_COMMENTS_PATH);
    }

    public DatabaseReference getCommentReference(String commentTimestamp) {
        return getTagsReference().child(commentTimestamp);
    }

    public void signOut() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
    }
}
