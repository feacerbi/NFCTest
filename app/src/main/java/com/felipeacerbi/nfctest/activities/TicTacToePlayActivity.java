package com.felipeacerbi.nfctest.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.firebasemodels.TicTacToeGameDB;
import com.felipeacerbi.nfctest.firebasemodels.UserDB;
import com.felipeacerbi.nfctest.models.TicTacToeGame;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TicTacToePlayActivity extends AppCompatActivity implements View.OnClickListener {

    TicTacToeGame ticTacToeGame;
    private FirebaseHelper firebaseHelper;
    private TicTacToeGameDB ticTacToeGameDB;
    private List<ImageView> placeFields;
    private String gameId;
    private String currentPlayer;
    private TextView turnField;
    private ProgressBar progressBar;
    private ArrayList<ImageView> bars;
    private String currentTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe_play);

        firebaseHelper = new FirebaseHelper(this);
        connect();

        final Intent gameIntent = getIntent();
        if(gameIntent != null) {
            ticTacToeGame = (TicTacToeGame) gameIntent.getExtras().get("game");
            currentPlayer = gameIntent.getStringExtra("player");
        } else {
            finish();
            Toast.makeText(this, "Game not started", Toast.LENGTH_SHORT).show();
        }

        ticTacToeGameDB = ticTacToeGame.getTicTacToeGameDB();
        gameId = ticTacToeGameDB.getPlayerOne() + ticTacToeGameDB.getPlayerTwo();

        ValueEventListener onlineMonitor = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserDB userDB = dataSnapshot.getValue(UserDB.class);
                if(!userDB.isOnline()) {
                    firebaseHelper.deleteGame(gameId);
                    finish();
                    Toast.makeText(TicTacToePlayActivity.this, "Opponent left", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        ValueEventListener gameScoreMonitor = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TicTacToeGameDB dataTicTacToeGameDB = dataSnapshot.getValue(TicTacToeGameDB.class);
                ticTacToeGameDB.setPlaces(dataTicTacToeGameDB.getPlaces());
                updatePlaces(isCurrentTurn());
                changeTurn();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        DatabaseReference gameReference = firebaseHelper.getGameReference(gameId);

        if(currentPlayer.equals(Constants.PLAYER_ONE)) {
            firebaseHelper.getUserReference(ticTacToeGameDB.getPlayerTwo()).addValueEventListener(onlineMonitor);
            gameReference.setValue(ticTacToeGameDB);
            setLoading(true);
        } else {
            firebaseHelper.getUserReference(ticTacToeGameDB.getPlayerOne()).addValueEventListener(onlineMonitor);
            gameReference.child("ready").setValue("true");
        }

        gameReference.child("ready").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isReady = Boolean.valueOf(dataSnapshot.getValue(String.class));
                if(isReady) {
                    setLoading(false);
                } else {
                    setLoading(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firebaseHelper.getGameReference(gameId).addValueEventListener(gameScoreMonitor);

        currentTurn = ticTacToeGameDB.getPlayerOne();
        turnField.setText(currentTurn);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        placeFields = new ArrayList<>();
        bars = new ArrayList<>();

        ImageView place1 = (ImageView) findViewById(R.id.place1);
        ImageView place2 = (ImageView) findViewById(R.id.place2);
        ImageView place3 = (ImageView) findViewById(R.id.place3);
        ImageView place4 = (ImageView) findViewById(R.id.place4);
        ImageView place5 = (ImageView) findViewById(R.id.place5);
        ImageView place6 = (ImageView) findViewById(R.id.place6);
        ImageView place7 = (ImageView) findViewById(R.id.place7);
        ImageView place8 = (ImageView) findViewById(R.id.place8);
        ImageView place9 = (ImageView) findViewById(R.id.place9);

        ImageView firstRow = (ImageView) findViewById(R.id.first_row);
        ImageView secondRow = (ImageView) findViewById(R.id.second_row);
        ImageView firstCol = (ImageView) findViewById(R.id.first_column);
        ImageView secondCol = (ImageView) findViewById(R.id.second_column);

        placeFields.add(0, place1);
        placeFields.add(1, place2);
        placeFields.add(2, place3);
        placeFields.add(3, place4);
        placeFields.add(4, place5);
        placeFields.add(5, place6);
        placeFields.add(6, place7);
        placeFields.add(7, place8);
        placeFields.add(8, place9);

        bars.add(0, firstRow);
        bars.add(0, secondRow);
        bars.add(0, firstCol);
        bars.add(0, secondCol);

        turnField = (TextView) findViewById(R.id.turn_name);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    public void updatePlaces(boolean isCurrentTurn) {
        List<Integer> places = ticTacToeGameDB.getPlaces();
        for(int i = 0; i < places.size(); i++) {
            switch (places.get(i)) {
                case TicTacToeGame.PLACE_AVAILABLE:
                    placeFields.get(i).setImageAlpha(0);
                    if(isCurrentTurn) placeFields.get(i).setOnClickListener(this);
                    break;
                case TicTacToeGame.X_MARKER:
                    placeFields.get(i).setImageResource(R.drawable.x);
                    placeFields.get(i).setOnClickListener(null);
                    break;
                case TicTacToeGame.O_MARKER:
                    placeFields.get(i).setImageResource(R.drawable.o);
                    placeFields.get(i).setOnClickListener(null);
                    break;
            }
        }
    }

    public void setLoading(boolean loading) {
        if(loading) {
            for(ImageView placeField : placeFields) {
                placeField.setVisibility(View.GONE);
            }
            for(ImageView bar : bars) {
                bar.setVisibility(View.GONE);
            }
            progressBar.setVisibility(View.VISIBLE);
        } else {
            for(ImageView placeField : placeFields) {
                placeField.setVisibility(View.VISIBLE);
            }
            for(ImageView bar : bars) {
                bar.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
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
        firebaseHelper.deleteGame(gameId);
    }

    public void changeTurn() {
        DatabaseReference turnReference = firebaseHelper.getGameReference(gameId).child("currentTurn");
        String player1 = ticTacToeGameDB.getPlayerOne();
        String player2 = ticTacToeGameDB.getPlayerTwo();

        if(currentTurn.equals(player1)) {
            turnReference.setValue(player2);
            currentTurn = player2;
            updatePlaces(currentPlayer.equals(Constants.PLAYER_TWO));
        } else {
            turnReference.setValue(player1);
            currentTurn = player1;
            updatePlaces(currentPlayer.equals(Constants.PLAYER_ONE));
        }

        turnField.setText(currentTurn);
    }

    public boolean isCurrentTurn() {
        return (currentTurn.equals(ticTacToeGameDB.getPlayerOne()) && currentPlayer.equals(Constants.PLAYER_ONE)) ||
                (currentTurn.equals(ticTacToeGameDB.getPlayerTwo()) && currentPlayer.equals(Constants.PLAYER_TWO));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    @Override
    public void onClick(View view) {
        int marker = (currentPlayer.equals(Constants.PLAYER_ONE) ?
                TicTacToeGame.X_MARKER :
                TicTacToeGame.O_MARKER);

        switch (view.getId()) {
            case R.id.place1:
                firebaseHelper.updateGamePlace(gameId, 0, marker);
                break;
            case R.id.place2:
                firebaseHelper.updateGamePlace(gameId, 1, marker);
                break;
            case R.id.place3:
                firebaseHelper.updateGamePlace(gameId, 2, marker);
                break;
            case R.id.place4:
                firebaseHelper.updateGamePlace(gameId, 3, marker);
                break;
            case R.id.place5:
                firebaseHelper.updateGamePlace(gameId, 4, marker);
                break;
            case R.id.place6:
                firebaseHelper.updateGamePlace(gameId, 5, marker);
                break;
            case R.id.place7:
                firebaseHelper.updateGamePlace(gameId, 6, marker);
                break;
            case R.id.place8:
                firebaseHelper.updateGamePlace(gameId, 7, marker);
                break;
            case R.id.place9:
                firebaseHelper.updateGamePlace(gameId, 8, marker);
                break;
        }

        view.setOnClickListener(null);

        Toast.makeText(this, ticTacToeGame.checkResult(), Toast.LENGTH_SHORT).show();
    }
}
