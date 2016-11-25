package com.felipeacerbi.nfctest.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.activities.PetProfileActivity;
import com.felipeacerbi.nfctest.models.Pet;
import com.felipeacerbi.nfctest.models.PetViewHolder;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.felipeacerbi.nfctest.utils.FirebaseStoreHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PetsNameAdapter extends RecyclerView.Adapter {

    private final Context context;
    private final List<Pet> pets;
    private FirebaseDBHelper firebaseDBHelper;
    private FirebaseStoreHelper firebaseStoreHelper;

    public PetsNameAdapter(Context context, List<Pet> pets) {
        this.context = context;
        this.pets = pets;
        firebaseDBHelper = new FirebaseDBHelper(context);
        firebaseStoreHelper = new FirebaseStoreHelper();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet_item, parent, false);
        return new PetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Pet pet = pets.get(position);

        PetViewHolder petViewHolder = (PetViewHolder) holder;
        petViewHolder.getNameField().setText(pet.getName());
        petViewHolder.getInfosField().setText(pet.getInfos());

        firebaseStoreHelper.downloadImage(
                pet.getProfileImage(),
                pet.getId(),
                petViewHolder.getProfilePicture(),
                petViewHolder.getProfilePictureProgress(),
                null);

    }

    @Override
    public int getItemCount() {
        return pets.size();
    }


}
