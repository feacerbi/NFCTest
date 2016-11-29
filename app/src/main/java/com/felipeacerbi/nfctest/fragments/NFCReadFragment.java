package com.felipeacerbi.nfctest.fragments;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.activities.BarcodeCaptureActivity;
import com.felipeacerbi.nfctest.activities.WaitTagActivity;
import com.felipeacerbi.nfctest.adapters.PetsAdapter;
import com.felipeacerbi.nfctest.models.Pet;
import com.felipeacerbi.nfctest.models.PetViewHolder;
import com.felipeacerbi.nfctest.models.tags.BaseTag;
import com.felipeacerbi.nfctest.models.tags.NFCTag;
import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.models.tags.QRCodeTag;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.felipeacerbi.nfctest.utils.FirebaseStoreHelper;
import com.felipeacerbi.nfctest.utils.NFCUtils;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

public class NFCReadFragment extends Fragment implements View.OnClickListener {

    // Firebase Helper instance
    private FirebaseDBHelper firebaseDBHelper;

    private FloatingActionButton fab;
    private FloatingActionButton fabNFC;
    private FloatingActionButton fabQR;
    private Animation openAnimation;
    private Animation closeAnimation;
    private Animation rotateForwardAnimation;
    private Animation rotateBackAnimation;
    private boolean isFabOpen = false;

    public static String downloadFilePath = "fail";
    private RecyclerView buddiesList;
    private RecyclerView followingList;
    private PetsAdapter buddiesAdapter;
    private PetsAdapter followingAdapter;

    public NFCReadFragment() {
    }

    public static NFCReadFragment newInstance(int sectionNumber) {
        NFCReadFragment fragment = new NFCReadFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle response from WaitTagActivity
        if(requestCode == Constants.START_WAIT_READ_TAG_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                Snackbar.make(
                        getActivity().findViewById(R.id.nfc_read_layout),
                        R.string.tag_read_success,
                        Snackbar.LENGTH_LONG).show();
                // Set text fields with Tag information
                if(data.getExtras() != null) {
                    firebaseDBHelper.addPet((NFCTag) data.getExtras().getParcelable(Constants.NFC_TAG_EXTRA), false);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(
                        getActivity().findViewById(R.id.nfc_read_layout),
                        R.string.tag_read_cancel,
                        Snackbar.LENGTH_LONG).show();
            }
        } else if(requestCode == Constants.RC_BARCODE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Snackbar.make(
                            getActivity().findViewById(R.id.nfc_read_layout),
                            R.string.qrcode_read_success,
                            Snackbar.LENGTH_LONG).show();
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    if(barcode != null) {
                        firebaseDBHelper.addPet(new QRCodeTag(barcode.displayValue), false);
                    }
                } else {
                    Snackbar.make(
                            getActivity().findViewById(R.id.nfc_read_layout),
                            R.string.qrcode_read_fail,
                            Snackbar.LENGTH_LONG).show();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(
                        getActivity().findViewById(R.id.nfc_read_layout),
                        R.string.qrcode_read_canceled,
                        Snackbar.LENGTH_LONG).show();
            }
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseDBHelper = new FirebaseDBHelper(getActivity());

        buddiesAdapter = new PetsAdapter(
                getActivity(),
                String.class,
                R.layout.pet_item,
                PetViewHolder.class,
                getQuery(Constants.DATABASE_BUDDIES_CHILD));

        followingAdapter = new PetsAdapter(
                getActivity(),
                Boolean.class,
                R.layout.pet_item,
                PetViewHolder.class,
                getQuery(Constants.DATABASE_FOLLOWING_CHILD));

        buddiesList.setAdapter(buddiesAdapter);
        followingList.setAdapter(followingAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_read_nfc, container, false);

        fabNFC = (FloatingActionButton) getActivity().findViewById(R.id.fabNFC);
        fabQR = (FloatingActionButton) getActivity().findViewById(R.id.fabQR);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fabQR.setOnClickListener(this);
        fabNFC.setOnClickListener(this);

        openAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        closeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        rotateForwardAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward);
        rotateBackAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backwards);

        buddiesList = (RecyclerView) rootView.findViewById(R.id.buddies_list);
        followingList = (RecyclerView) rootView.findViewById(R.id.following_list);

        return rootView;
    }

    public Query getQuery(String child) {
        return firebaseDBHelper.getCurrentUserReference().child(child).orderByKey();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                animateFab();
                break;
            case R.id.fabNFC:
                animateFab();
                // Start the activity to wait for Tag interactivity
                Intent startReadIntent = new Intent(getActivity(), WaitTagActivity.class);
                startActivityForResult(startReadIntent, Constants.START_WAIT_READ_TAG_INTENT);
                break;
            case R.id.fabQR:
                animateFab();
                // Launch barcode activity.
                Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, Constants.RC_BARCODE_CAPTURE);
                break;
            default:
        }
    }

    @Override
    public void onDestroy() {
        if(buddiesAdapter != null) {
            buddiesAdapter.cleanup();
        }
        if(followingAdapter != null) {
            followingAdapter.cleanup();
        }
        super.onDestroy();
    }
}
