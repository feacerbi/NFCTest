package com.felipeacerbi.nfctest.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.firebasemodels.TicTacToeGameDB;
import com.felipeacerbi.nfctest.firebasemodels.UserDB;
import com.felipeacerbi.nfctest.models.TicTacToeGame;
import com.felipeacerbi.nfctest.utils.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TicTacToePlayActivity extends AppCompatActivity {

    TicTacToeGame ticTacToeGame;
    private FirebaseHelper firebaseHelper;
    private TicTacToeGameDB ticTacToeGameDB;
    private List<ImageView> placeFields;
    private String gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe_play);

        firebaseHelper = new FirebaseHelper(this);

        Intent gameIntent = getIntent();
        if(gameIntent != null) {
            ticTacToeGame = (TicTacToeGame) gameIntent.getExtras().get("game");
        } else {
            finish();
            Toast.makeText(this, "Game not started", Toast.LENGTH_SHORT).show();
        }

        ticTacToeGameDB = ticTacToeGame.getTicTacToeGameDB();
        gameId = ticTacToeGameDB.getPlayerOne() + ticTacToeGameDB.getPlayerTwo();
        firebaseHelper.getUserReference(ticTacToeGameDB.getPlayerTwo()).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserDB userDB = dataSnapshot.getValue(UserDB.class);
                        if(!userDB.isOnline()) {
                            finish();
                            Toast.makeText(TicTacToePlayActivity.this, "Opponent left", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

        DatabaseReference game = firebaseHelper.getGameReference(gameId);
        game.setValue(ticTacToeGameDB);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        placeFields = new ArrayList<>();

        ImageView place1 = (ImageView) findViewById(R.id.place1);
        ImageView place2 = (ImageView) findViewById(R.id.place2);
        ImageView place3 = (ImageView) findViewById(R.id.place3);
        ImageView place4 = (ImageView) findViewById(R.id.place4);
        ImageView place5 = (ImageView) findViewById(R.id.place5);
        ImageView place6 = (ImageView) findViewById(R.id.place6);
        ImageView place7 = (ImageView) findViewById(R.id.place7);
        ImageView place8 = (ImageView) findViewById(R.id.place8);
        ImageView place9 = (ImageView) findViewById(R.id.place9);

        placeFields.add(0, place1);
        placeFields.add(1, place2);
        placeFields.add(2, place3);
        placeFields.add(3, place4);
        placeFields.add(4, place5);
        placeFields.add(5, place6);
        placeFields.add(6, place7);
        placeFields.add(7, place8);
        placeFields.add(8, place9);

        for(ImageView place : placeFields) {
            place.setImageAlpha(0);
        }
    }

    public void connect() {
        DatabaseReference userReference = firebaseHelper.getCurrentUserReference();
        userReference.child("online").setValue(true);
        userReference.child("playing").setValue(true);
    }

    public void disconnect() {
        DatabaseReference userReference = firebaseHelper.getCurrentUserReference();
        userReference.child("playing").setValue(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnect();
    }
}
