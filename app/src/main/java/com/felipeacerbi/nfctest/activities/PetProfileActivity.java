package com.felipeacerbi.nfctest.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.adapters.PetsAdapter;
import com.felipeacerbi.nfctest.adapters.PostsAdapter;
import com.felipeacerbi.nfctest.models.Pet;
import com.felipeacerbi.nfctest.models.PetViewHolder;
import com.felipeacerbi.nfctest.models.posts.FeedPost;
import com.felipeacerbi.nfctest.models.posts.FeedPostFullViewHolder;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.google.firebase.database.Query;

public class PetProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView postsList;
    private FirebaseDBHelper firebaseDBHelper;
    private PostsAdapter postsAdapter;
    private Pet pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_profile);
        firebaseDBHelper = new FirebaseDBHelper(this);

        Intent petProfileIntent = getIntent();
        if(petProfileIntent != null) {
            pet = (Pet) petProfileIntent.getSerializableExtra(Constants.PET_EXTRA);
        }

        setToolbar();

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

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        postsList = (RecyclerView) findViewById(R.id.pet_posts_list);
    }

    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(firebaseDBHelper.getUserName());
        setSupportActionBar(toolbar);
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
}
