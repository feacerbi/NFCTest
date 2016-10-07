package com.felipeacerbi.nfctest.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.models.TicTacToeGame;
import com.felipeacerbi.nfctest.firebasemodels.TicTacToeGameDB;
import com.felipeacerbi.nfctest.firebasemodels.UserDB;
import com.felipeacerbi.nfctest.utils.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TicTacToeActivity extends AppCompatActivity {

    private static final int SPAN_COUNT = 2;
    private AutoCompleteTextView searchPlayersBox;
    private ImageView searchButton;
    private RecyclerView resultUsersList;
    private LayoutManagerType currentLayoutManagerType;
    private FirebaseHelper firebaseHelper;
    private ValueEventListener requestsListener;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        firebaseHelper = new FirebaseHelper(this);

        requestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final UserDB userDB = dataSnapshot.getValue(UserDB.class);
                final List<String> requests = userDB.getRequests();
                if(requests.size() != 0) {
                    AlertDialog.Builder playAlert = new AlertDialog.Builder(TicTacToeActivity.this);
                    playAlert
                            .setTitle("New game request")
                            .setMessage("Start a new game with " + userDB.getName() + "?")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // New game
                                    firebaseHelper.getCurrentUserReference()
                                            .child("playRequests")
                                            .child("0")
                                            .removeValue();
                                    setUpNewGame(dataSnapshot.getKey());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            })
                            .show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebaseHelper.getCurrentUserReference().addValueEventListener(requestsListener);
    }

    public void setUpNewGame(String opponent) {
        String currentUser = firebaseHelper.getLoginName();
        TicTacToeGame ticTacToeGame = new TicTacToeGame(new TicTacToeGameDB(currentUser, opponent, "1"));

        startActivity(new Intent(this, TicTacToePlayActivity.class).putExtra("game", ticTacToeGame));
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        resultUsersList = (RecyclerView) findViewById(R.id.users_result_list);
        searchPlayersBox = (AutoCompleteTextView) findViewById(R.id.search_players_box);
        searchButton = (ImageView) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = searchPlayersBox.getText().toString();
                firebaseHelper.showResultUsers(search, resultUsersList);
            }
        });

        currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(currentLayoutManagerType);
    }

    public void connect() {
        DatabaseReference userReference = firebaseHelper.getCurrentUserReference();
        userReference.addValueEventListener(requestsListener);
        userReference.child("online").setValue(true);
    }

    public void disconnect() {
        DatabaseReference userReference = firebaseHelper.getCurrentUserReference();
        userReference.removeEventListener(requestsListener);
        userReference.child("online").setValue(false);
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

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        if (resultUsersList.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) resultUsersList.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        RecyclerView.LayoutManager layoutManager = null;
        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                layoutManager = new GridLayoutManager(this, SPAN_COUNT);
                currentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                //if(layoutMenuItem != null) layoutMenuItem.setIcon(R.drawable.ic_view_stream_white_24dp);
                break;
            case LINEAR_LAYOUT_MANAGER:
                layoutManager = new LinearLayoutManager(this);
                currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                //if(layoutMenuItem != null) layoutMenuItem.setIcon(R.drawable.ic_view_module_white_24dp);
                break;
            default:
                layoutManager = new LinearLayoutManager(this);
                currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        resultUsersList.setLayoutManager(layoutManager);
        resultUsersList.scrollToPosition(scrollPosition);
    }
}
