package com.felipeacerbi.nfctest.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.felipeacerbi.nfctest.models.Pet;
import com.felipeacerbi.nfctest.models.PetViewHolder;
import com.felipeacerbi.nfctest.models.posts.FeedPost;
import com.felipeacerbi.nfctest.models.posts.FeedPostFullViewHolder;
import com.felipeacerbi.nfctest.models.posts.FeedPostMedia;
import com.felipeacerbi.nfctest.models.posts.FeedPostText;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.felipeacerbi.nfctest.utils.FirebaseStoreHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PetsAdapter extends FirebaseRecyclerAdapter {

    private FirebaseDBHelper firebaseDBHelper;
    private FirebaseStoreHelper firebaseStoreHelper;

    public PetsAdapter(Context context, final Class modelClass, int modelLayout, Class viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        firebaseDBHelper = new FirebaseDBHelper(context);
        firebaseStoreHelper = new FirebaseStoreHelper();
    }

    @Override
    protected void populateViewHolder(final RecyclerView.ViewHolder viewHolder, Object model, int position) {
        DatabaseReference petRef = getRef(position);
        String petKey = petRef.getKey();

        firebaseDBHelper.getPetReference(petKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pet pet = new Pet(dataSnapshot);

                PetViewHolder petViewHolder = (PetViewHolder) viewHolder;
                petViewHolder.getNameField().setText(pet.getName());
                petViewHolder.getAgeField().setText(pet.getAge() + " years");

                firebaseStoreHelper.downloadImage(
                        pet.getProfileImage(),
                        pet.getId(),
                        petViewHolder.getProfilePicture(),
                        petViewHolder.getProfilePictureProgress(),
                        null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
