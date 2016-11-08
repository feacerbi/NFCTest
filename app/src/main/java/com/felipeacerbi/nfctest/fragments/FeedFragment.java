package com.felipeacerbi.nfctest.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.activities.BarcodeCaptureActivity;
import com.felipeacerbi.nfctest.activities.WaitTagActivity;
import com.felipeacerbi.nfctest.models.BaseTag;
import com.felipeacerbi.nfctest.models.NFCTag;
import com.felipeacerbi.nfctest.models.QRCodeTag;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.felipeacerbi.nfctest.utils.FirebaseStoreHelper;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.File;

public class FeedFragment extends Fragment implements View.OnClickListener {

    // Firebase Helper instance
    private FirebaseDBHelper firebaseDBHelper;
    private FirebaseStoreHelper firebaseStoreHelper;
    private FloatingActionButton fab;
    private RecyclerView cardsList;

    public FeedFragment() {
    }

    public static FeedFragment newInstance(int sectionNumber) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseDBHelper = new FirebaseDBHelper(getActivity());
        firebaseStoreHelper = new FirebaseStoreHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_read_nfc, container, false);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        cardsList = (RecyclerView) rootView.findViewById(R.id.cards_list);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                break;
            default:
        }
    }
}
