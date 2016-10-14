package com.felipeacerbi.nfctest.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.IntegerRes;
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
import com.google.firebase.database.GenericTypeIndicator;
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
    private ValueEventListener gameScoreMonitor;
    private ValueEventListener onlineMonitor;

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

        onlineMonitor = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserDB userDB = dataSnapshot.getValue(UserDB.class);
                if(!userDB.isPlaying()) {
                    disconnect();
                    finish();
                    Toast.makeText(TicTacToePlayActivity.this, "Opponent left", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        gameScoreMonitor = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<Integer>> t = new GenericTypeIndicator<List<Integer>>() {};
                List<Integer> places = dataSnapshot.getValue(t);
                if(places != null) {
                    ticTacToeGameDB.setPlaces(places);
                    updatePlaces();
                }

                if(!ticTacToeGame.checkResult().equals("")) {
                    Toast.makeText(TicTacToePlayActivity.this, "Winner: " + ticTacToeGame.checkResult() + "!!!", Toast.LENGTH_SHORT).show();
                    disconnect();
                    finish();
                } else {
                    changeTurn();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        DatabaseReference gameReference = firebaseHelper.getGameReference(gameId);
        setUpTurn(ticTacToeGameDB.getPlayerTwo());

        if(currentPlayer.equals(Constants.PLAYER_ONE)) {
            gameReference.setValue(ticTacToeGameDB);
            setLoading(true);

            gameReference.child("ready").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean isReady = Boolean.valueOf(dataSnapshot.getValue(String.class));
                    setLoading(!isReady);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            firebaseHelper.getUserReference(ticTacToeGameDB.getPlayerOne()).addValueEventListener(onlineMonitor);
            gameReference.child("ready").setValue("true");
        }

        gameReference.child("places").addValueEventListener(gameScoreMonitor);
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

        bars.add(firstRow);
        bars.add(secondRow);
        bars.add(firstCol);
        bars.add(secondCol);

        turnField = (TextView) findViewById(R.id.turn_name);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    public void updatePlaces() {
        List<Integer> places = ticTacToeGameDB.getPlaces();
        Bitmap xMarker = BitmapFactory.decodeResource(getResources(), R.drawable.x);
        Bitmap oMarker = BitmapFactory.decodeResource(getResources(), R.drawable.o);
        boolean isCurrentTurn = isCurrentTurn();
        for(int i = 0; i < places.size(); i++) {
            switch (places.get(i)) {
                case TicTacToeGame.PLACE_AVAILABLE:
                    placeFields.get(i).setImageAlpha(0);
                    if(isCurrentTurn) {
                        placeFields.get(i).setOnClickListener(this);
                    } else {
                        placeFields.get(i).setOnClickListener(null);
                    }
                    break;
                case TicTacToeGame.X_MARKER:
                    placeFields.get(i).setImageAlpha(255);
                    placeFields.get(i).setImageBitmap(xMarker);
                    placeFields.get(i).setOnClickListener(null);
                    break;
                case TicTacToeGame.O_MARKER:
                    placeFields.get(i).setImageAlpha(255);
                    placeFields.get(i).setImageBitmap(oMarker);
                    placeFields.get(i).setOnClickListener(null);
                    break;
            }
        }
    }

    public void setLoading(boolean loading) {
        int notVisibleLoading = loading ? View.INVISIBLE : View.VISIBLE;
        int visibleLoading = loading ? View.VISIBLE : View.GONE;

        for(ImageView placeField : placeFields) {
            placeField.setVisibility(notVisibleLoading);
        }
        for(ImageView bar : bars) {
            bar.setVisibility(notVisibleLoading);
        }
        progressBar.setVisibility(visibleLoading);

        if(!loading) {
            firebaseHelper.getUserReference(ticTacToeGameDB.getPlayerTwo()).addValueEventListener(onlineMonitor);
            updatePlaces();
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

        firebaseHelper.getGameReference(gameId).child("places").removeEventListener(gameScoreMonitor);

        firebaseHelper.getUserReference(ticTacToeGameDB.getPlayerOne()).removeEventListener(onlineMonitor);
        firebaseHelper.getUserReference(ticTacToeGameDB.getPlayerTwo()).removeEventListener(onlineMonitor);
    }

    public void changeTurn() {
        String player1 = ticTacToeGameDB.getPlayerOne();
        String player2 = ticTacToeGameDB.getPlayerTwo();

        if(currentTurn.equals(player1)) {
            setUpTurn(player2);
        } else {
            setUpTurn(player1);
        }

        updatePlaces();
    }

    public void setUpTurn(String player) {
        firebaseHelper.setTurn(gameId, player);
        currentTurn = player;
        turnField.setText(player);
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
    }
}
