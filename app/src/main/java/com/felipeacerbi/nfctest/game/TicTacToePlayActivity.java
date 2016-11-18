package com.felipeacerbi.nfctest.game;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.models.User;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TicTacToePlayActivity extends AppCompatActivity implements View.OnClickListener {

    TicTacToeGame ticTacToeGame;
    private FirebaseDBHelper firebaseDBHelper;
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
    private String requestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe_play);

        firebaseDBHelper = new FirebaseDBHelper(this);
        connect();

        final Intent gameIntent = getIntent();
        if(gameIntent != null) {
            ticTacToeGame = (TicTacToeGame) gameIntent.getExtras().get("game");
            currentPlayer = gameIntent.getStringExtra("player");
            requestId = gameIntent.getStringExtra("requestId");
        } else {
            finish();
            Toast.makeText(this, R.string.game_not_started, Toast.LENGTH_SHORT).show();
        }

        ticTacToeGameDB = ticTacToeGame.getTicTacToeGameDB();
        gameId = ticTacToeGameDB.getPlayerOne() + ticTacToeGameDB.getPlayerTwo();

        setUpMonitors();

        DatabaseReference gameReference = firebaseDBHelper.getGameReference(gameId);
        setUpTurn(ticTacToeGameDB.getPlayerTwo());

        if(currentPlayer.equals(Constants.PLAYER_ONE)) {
            gameReference.setValue(ticTacToeGameDB);
            setLoading(true);

            gameReference.child("ready").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String isReady = dataSnapshot.getValue(String.class);
                    if(isReady != null && isReady.equals("refused")) {
                        disconnect();
                        finish();
                        Toast.makeText(TicTacToePlayActivity.this, R.string.request_refused, Toast.LENGTH_SHORT).show();
                    } else {
                        setLoading(!Boolean.valueOf(isReady));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            firebaseDBHelper.getUserReference(ticTacToeGameDB.getPlayerOne()).addValueEventListener(onlineMonitor);
            gameReference.child("ready").setValue("true");
        }

        gameReference.child(Constants.DATABASE_PLACES_CHILD).addValueEventListener(gameScoreMonitor);
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

    public void setUpMonitors() {
        onlineMonitor = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = new User(dataSnapshot);
                if(!user.isPlaying()) {
                    disconnect();
                    finish();
                    Toast.makeText(TicTacToePlayActivity.this, R.string.opponent_left, Toast.LENGTH_SHORT).show();
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

                String result = ticTacToeGame.checkResult();
                if(!result.equals("")) {
                    if(result.equals("tie")) {
                        Toast.makeText(TicTacToePlayActivity.this, R.string.tie, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TicTacToePlayActivity.this, "Winner: " + result + "!!!", Toast.LENGTH_SHORT).show();
                    }
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
    }

    public void updatePlaces() {
        List<Integer> places = ticTacToeGameDB.getPlaces();
        Bitmap xMarker = BitmapFactory.decodeResource(getResources(), R.drawable.x);
        Bitmap oMarker = BitmapFactory.decodeResource(getResources(), R.drawable.o);
        boolean isCurrentTurn = isCurrentTurn();
        for(int i = 0; i < places.size(); i++) {
            placeFields.get(i).setOnClickListener(this);
            switch (places.get(i)) {
                case TicTacToeGame.PLACE_AVAILABLE:
                    placeFields.get(i).setImageAlpha(0);
                    if(isCurrentTurn) {
                        placeFields.get(i).setClickable(true);
                    } else {
                        placeFields.get(i).setClickable(false);
                    }
                    break;
                case TicTacToeGame.X_MARKER:
                    placeFields.get(i).setImageAlpha(255);
                    placeFields.get(i).setImageBitmap(xMarker);
                    placeFields.get(i).setClickable(false);
                    break;
                case TicTacToeGame.O_MARKER:
                    placeFields.get(i).setImageAlpha(255);
                    placeFields.get(i).setImageBitmap(oMarker);
                    placeFields.get(i).setClickable(false);
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
            firebaseDBHelper.getUserReference(ticTacToeGameDB.getPlayerTwo()).addValueEventListener(onlineMonitor);
            updatePlaces();
        }
    }

    public void connect() {
        DatabaseReference userReference = firebaseDBHelper.getCurrentUserReference();
        userReference.child(Constants.DATABASE_ONLINE_CHILD).setValue(true);
        userReference.child(Constants.DATABASE_PLAYING_CHILD).setValue(true);
    }

    public void disconnect() {
        firebaseDBHelper.getCurrentUserReference().child(Constants.DATABASE_PLAYING_CHILD).setValue(false);

        firebaseDBHelper.getGameReference(gameId).child(Constants.DATABASE_PLACES_CHILD).removeEventListener(gameScoreMonitor);
        firebaseDBHelper.getUserReference(ticTacToeGameDB.getPlayerOne()).removeEventListener(onlineMonitor);
        firebaseDBHelper.getUserReference(ticTacToeGameDB.getPlayerTwo()).removeEventListener(onlineMonitor);

        firebaseDBHelper.deleteGame(gameId);
        firebaseDBHelper.deleteRequest(requestId);
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
        firebaseDBHelper.setTurn(gameId, player);
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
                firebaseDBHelper.updateGamePlace(gameId, 0, marker);
                break;
            case R.id.place2:
                firebaseDBHelper.updateGamePlace(gameId, 1, marker);
                break;
            case R.id.place3:
                firebaseDBHelper.updateGamePlace(gameId, 2, marker);
                break;
            case R.id.place4:
                firebaseDBHelper.updateGamePlace(gameId, 3, marker);
                break;
            case R.id.place5:
                firebaseDBHelper.updateGamePlace(gameId, 4, marker);
                break;
            case R.id.place6:
                firebaseDBHelper.updateGamePlace(gameId, 5, marker);
                break;
            case R.id.place7:
                firebaseDBHelper.updateGamePlace(gameId, 6, marker);
                break;
            case R.id.place8:
                firebaseDBHelper.updateGamePlace(gameId, 7, marker);
                break;
            case R.id.place9:
                firebaseDBHelper.updateGamePlace(gameId, 8, marker);
                break;
        }
    }
}
