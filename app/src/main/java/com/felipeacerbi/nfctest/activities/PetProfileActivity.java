package com.felipeacerbi.nfctest.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.adapters.PostsAdapter;
import com.felipeacerbi.nfctest.dialogs.PetDialog;
import com.felipeacerbi.nfctest.models.Pet;
import com.felipeacerbi.nfctest.models.posts.FeedPost;
import com.felipeacerbi.nfctest.models.posts.FeedPostFullViewHolder;
import com.felipeacerbi.nfctest.models.tags.BaseTag;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.felipeacerbi.nfctest.utils.FirebaseStoreHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PetProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private RecyclerView postsList;
    private FirebaseDBHelper firebaseDBHelper;
    private PostsAdapter postsAdapter;
    private Pet pet;
    private ValueEventListener profileListener;
    private ImageView editPetButton;
    private ImageView petPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_profile);
        firebaseDBHelper = new FirebaseDBHelper(this);

        Intent petProfileIntent = getIntent();
        if(petProfileIntent != null) {
            pet = (Pet) petProfileIntent.getSerializableExtra(Constants.PET_EXTRA);
        }

        profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pet pet = new Pet(dataSnapshot);

                toolbar.setTitle(pet.getName());
                petPhoto.setImageResource(R.drawable.nfc_logo_ok);

                FirebaseStoreHelper firebaseStoreHelper = new FirebaseStoreHelper();
                firebaseStoreHelper.downloadImage(pet.getProfileImage(),
                        pet.getId(),
                        petPhoto,
                        null, null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        postsAdapter = new PostsAdapter(
                this,
                FeedPost.class,
                R.layout.feed_card_text,
                FeedPostFullViewHolder.class,
                getQuery(firebaseDBHelper));

        postsList.setAdapter(postsAdapter);
    }

    private Query getQuery(FirebaseDBHelper firebaseDBHelper) {
        return firebaseDBHelper.getPetReference(pet.getId()).child(Constants.DATABASE_POSTS_CHILD).orderByKey();
    }

    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseDBHelper.getPetReference(pet.getId()).addValueEventListener(profileListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setToolbar();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseDBHelper.getPetReference(pet.getId()).removeEventListener(profileListener);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        editPetButton = (ImageView) findViewById(R.id.edit_photo);
        editPetButton.setOnClickListener(this);
        petPhoto = (ImageView) findViewById(R.id.pet_photo);

        postsList = (RecyclerView) findViewById(R.id.pet_posts_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_photo:
                PetDialog petDialog = PetDialog.newInstance(
                        new BaseTag(pet.getTag(), pet.getId(), pet.getName()),
                        true);
                petDialog.show(getSupportFragmentManager(), "editPet");
                break;
        }
    }
}
