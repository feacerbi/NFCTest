package com.felipeacerbi.nfctest.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.models.Pet;
import com.felipeacerbi.nfctest.models.tags.BaseTag;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseStoreHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class PetDialog extends EditProfileDialog implements View.OnClickListener {

    private BaseTag tag;
    private Pet pet;
    private ImageView petPhoto;
    private boolean isEdit;

    public static PetDialog newInstance(BaseTag tag, Boolean isEdit) {
        PetDialog fragment = new PetDialog();
        Bundle args = new Bundle();
        args.putSerializable(Constants.BASE_TAG_EXTRA, tag);
        args.putBoolean(Constants.IS_EDIT_EXTRA, isEdit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && !getArguments().isEmpty()) {
            tag = (BaseTag) getArguments().getSerializable(Constants.BASE_TAG_EXTRA);
            isEdit = getArguments().getBoolean(Constants.IS_EDIT_EXTRA);
        }

        if(!isEdit) {
            pet = new Pet(firebaseDBHelper.getPetsReference().push().getKey());
            pet.setProfileImage("none");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(com.felipeacerbi.nfctest.R.layout.pet_dialog, null);

        final AutoCompleteTextView petName = (AutoCompleteTextView) view.findViewById(R.id.pet_name);
        final TextView animal = (TextView) view.findViewById(R.id.animal);
        final TextView petBreed = (TextView) view.findViewById(R.id.pet_breed);
        final AutoCompleteTextView petAge = (AutoCompleteTextView) view.findViewById(R.id.pet_age);
        final TextView petGender = (TextView) view.findViewById(R.id.pet_gender);
        final AutoCompleteTextView petDescription = (AutoCompleteTextView) view.findViewById(R.id.pet_description);
        petPhoto = (ImageView) view.findViewById(R.id.pet_photo);
        progressBar = (ProgressBar) view.findViewById(R.id.upload_progress_bar);
        progress = (TextView) view.findViewById(R.id.upload_message);

        petPhoto.setOnClickListener(this);
        animal.setOnClickListener(this);
        petBreed.setOnClickListener(this);
        petGender.setOnClickListener(this);

        if(isEdit) {
            firebaseDBHelper.getPetReference(tag.getPet()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pet = new Pet(dataSnapshot);
                    FirebaseStoreHelper firebaseStoreHelper = new FirebaseStoreHelper();
                    firebaseStoreHelper.downloadImage(pet.getProfileImage(),
                            pet.getId(),
                            petPhoto,
                            progressBar,
                            null);

                    petName.setText(pet.getName());
                    animal.setText(pet.getAnimal());
                    petBreed.setText(pet.getBreed());
                    petAge.setText(pet.getAge());
                    petGender.setText((pet.getGender() == Constants.MALE) ?
                            "Male" :
                            "Female");
                    petDescription.setText(pet.getDescription());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        pet.setName(petName.getText().toString());
                        if(!pet.getName().equals("")) {
                            pet.setAnimal(animal.getText().toString());
                            pet.setBreed(petBreed.getText().toString());
                            pet.setAge(petAge.getText().toString());
                            pet.setGender(petGender.getText().toString().equals("Male") ?
                                    Constants.MALE :
                                    Constants.FEMALE);
                            pet.setDescription(petDescription.getText().toString());

                            firebaseDBHelper.addNewPet(tag, pet, false);
                        } else {
                            Toast.makeText(getActivity(), "Please write the pet name", Toast.LENGTH_SHORT).show();
                            PetDialog.this.show(getActivity().getSupportFragmentManager(), "newPet");
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PetDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onClick(final View view) {
        AlertDialog.Builder builder;
        switch (view.getId()) {
            case R.id.pet_photo:
                onClickPhoto();
                break;
            case R.id.animal:
                builder = new AlertDialog.Builder(getActivity());
                builder.setItems(R.array.animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String animal = getActivity().getResources().getStringArray(R.array.animals)[i];
                        pet.setAnimal(animal);
                        ((TextView) view).setText(animal);
                    }
                })
                        .create()
                        .show();
                break;
            case R.id.pet_breed:
                builder = new AlertDialog.Builder(getActivity());
                builder.setItems(R.array.dog_breed, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String breed = getActivity().getResources().getStringArray(R.array.dog_breed)[i];
                        pet.setBreed(breed);
                        ((TextView) view).setText(breed);
                    }
                })
                        .create()
                        .show();
                break;
            case R.id.pet_gender:
                builder = new AlertDialog.Builder(getActivity());
                builder.setItems(R.array.genders, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0) {
                            pet.setGender(Constants.MALE);
                        } else {
                            pet.setGender(Constants.FEMALE);
                        }
                        ((TextView) view).setText(getActivity().getResources().getStringArray(R.array.genders)[i]);
                    }
                })
                        .create()
                        .show();
                break;
        }
    }

    @Override
    public void resultOk(File path) {
        FirebaseStoreHelper firebaseStoreHelper = new FirebaseStoreHelper();
        UploadTask uploadTask = firebaseStoreHelper.uploadImage(
                path,
                pet.getId(),
                progressBar,
                progress);
        uploadTask.addOnSuccessListener(this);

        petPhoto.setImageBitmap(BitmapFactory.decodeFile(path.getAbsolutePath()));
    }

    @Override
    public void successUrl(Uri downloadUrl) {
        pet.setProfileImage(downloadUrl.getLastPathSegment().substring(downloadUrl.getLastPathSegment().lastIndexOf("/")));
    }
}
