package com.felipeacerbi.nfctest.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.adapters.PetsAdapter;
import com.felipeacerbi.nfctest.dialogs.EditProfileDialog;
import com.felipeacerbi.nfctest.dialogs.UserDialog;
import com.felipeacerbi.nfctest.models.Pet;
import com.felipeacerbi.nfctest.models.PetViewHolder;
import com.felipeacerbi.nfctest.models.User;
import com.felipeacerbi.nfctest.models.tags.NFCTag;
import com.felipeacerbi.nfctest.models.tags.QRCodeTag;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.felipeacerbi.nfctest.utils.FirebaseStoreHelper;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private FirebaseDBHelper firebaseDBHelper;
    private FloatingActionButton fab;
    private FloatingActionButton fabNFC;
    private FloatingActionButton fabQR;
    private Animation openAnimation;
    private Animation closeAnimation;
    private Animation rotateForwardAnimation;
    private Animation rotateBackAnimation;
    private boolean isFabOpen = false;
    private RecyclerView petsList;
    private PetsAdapter petsAdapter;
    private ValueEventListener profileListener;
    private ImageView userPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseDBHelper = new FirebaseDBHelper(this);

        profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = new User(dataSnapshot);

                toolbar.setTitle(user.getName());

                FirebaseStoreHelper firebaseStoreHelper = new FirebaseStoreHelper();
                firebaseStoreHelper.downloadImage(user.getProfilePicture(),
                        firebaseDBHelper.getLoginName(),
                        userPhoto,
                        null, null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        petsAdapter = new PetsAdapter(
                this,
                String.class,
                R.layout.pet_item,
                PetViewHolder.class,
                getQuery(firebaseDBHelper));

        petsList.setAdapter(petsAdapter);
    }

    private Query getQuery(FirebaseDBHelper firebaseDBHelper) {
        return firebaseDBHelper.getCurrentUserReference().child(Constants.DATABASE_BUDDIES_CHILD).orderByKey();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setToolbar();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        fabNFC = (FloatingActionButton) findViewById(R.id.fabNFC);
        fabQR = (FloatingActionButton) findViewById(R.id.fabQR);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        fabQR.setOnClickListener(this);
        fabNFC.setOnClickListener(this);

        openAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        closeAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForwardAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotateBackAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_backwards);

        petsList = (RecyclerView) findViewById(R.id.buddies_list);

        ImageView editPhoto = (ImageView) findViewById(R.id.edit_photo);
        editPhoto.setOnClickListener(this);
        userPhoto = (ImageView) findViewById(R.id.user_photo);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle response from WaitTagActivity
        if(requestCode == Constants.START_WAIT_READ_TAG_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                firebaseDBHelper.addPet((NFCTag) data.getExtras().getParcelable(Constants.NFC_TAG_EXTRA), true);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(
                        findViewById(R.id.buddies_list),
                        R.string.tag_read_cancel,
                        Snackbar.LENGTH_LONG).show();
            }
        } else if(requestCode == Constants.RC_BARCODE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    firebaseDBHelper.addPet(new QRCodeTag(barcode.displayValue), true);
                } else {
                    Snackbar.make(
                            findViewById(R.id.buddies_list),
                            R.string.qrcode_read_fail,
                            Snackbar.LENGTH_LONG).show();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(
                        findViewById(R.id.buddies_list),
                        R.string.qrcode_read_canceled,
                        Snackbar.LENGTH_LONG).show();
            }
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                animateFab();
                break;
            case R.id.fabNFC:
                animateFab();
                // Start the activity to wait for Tag interactivity
                Intent startReadIntent = new Intent(this, WaitTagActivity.class);
                startActivityForResult(startReadIntent, Constants.START_WAIT_READ_TAG_INTENT);
                break;
            case R.id.fabQR:
                animateFab();
                // Launch barcode activity.
                Intent intent = new Intent(this, BarcodeCaptureActivity.class);
                startActivityForResult(intent, Constants.RC_BARCODE_CAPTURE);
                break;
            case R.id.edit_photo:
                UserDialog userDialog = UserDialog.newInstance(new User());
                userDialog.show(getSupportFragmentManager(), "editUser");
                break;
        }

    }

    public void animateFab() {
        if(isFabOpen) {
            fab.startAnimation(rotateBackAnimation);
            fabNFC.startAnimation(closeAnimation);
            fabNFC.setClickable(false);
            fabQR.startAnimation(closeAnimation);
            fabQR.setClickable(false);
            isFabOpen = false;
        } else {
            fab.startAnimation(rotateForwardAnimation);
            fabNFC.startAnimation(openAnimation);
            fabNFC.setClickable(true);
            fabQR.startAnimation(openAnimation);
            fabQR.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    protected void onDestroy() {
        if(petsAdapter != null) {
            petsAdapter.cleanup();
        }
        super.onDestroy();
    }
}
