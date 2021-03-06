package com.felipeacerbi.nfctest.game;

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
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class TicTacToeActivity extends AppCompatActivity {

    private static final int SPAN_COUNT = 2;
    private AutoCompleteTextView searchPlayersBox;
    private ImageView searchButton;
    private RecyclerView resultUsersList;
    private LayoutManagerType currentLayoutManagerType;
    private FirebaseDBHelper firebaseDBHelper;
    private ValueEventListener requestsListener;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        firebaseDBHelper = new FirebaseDBHelper(this);

        requestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final Request request = getCurrentUserRequest(dataSnapshot);
                if(request != null) {
                    final String gameId = request.getRequestDB().getRequester() + request.getRequestDB().getReceiver();
                    AlertDialog.Builder playAlert = new AlertDialog.Builder(TicTacToeActivity.this);
                    playAlert
                            .setTitle(R.string.new_game_request)
                            .setMessage("Start a new game with " + request.getRequestDB().getRequester() + "?")
                            .setCancelable(true)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // New game
                                    setUpNewGame(request);
                                    firebaseDBHelper.deleteRequest(request.getId());
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    firebaseDBHelper.getGameReference(gameId).child("ready").setValue("refused");
                                    Toast.makeText(TicTacToeActivity.this, R.string.request_refused, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    public Request getCurrentUserRequest(DataSnapshot requestsSnapshot) {
            for (DataSnapshot requestSnapshot : requestsSnapshot.getChildren()) {
                RequestDB requestDB = requestSnapshot.getValue(RequestDB.class);

                if (requestDB.getReceiver().equals(firebaseDBHelper.getLoginName())) {
                    return new Request(requestSnapshot.getKey(), requestDB);
                }
            }
        return null;
    }

    public void setUpNewGame(Request request) {
        String currentUser = firebaseDBHelper.getLoginName();
        TicTacToeGame ticTacToeGame = new TicTacToeGame(new TicTacToeGameDB(request.getRequestDB().getRequester(), currentUser));

        startActivity(
                new Intent(this, TicTacToePlayActivity.class)
                        .putExtra("game", ticTacToeGame)
                        .putExtra("player", Constants.PLAYER_TWO)
                        .putExtra("requestId", request.getId()));
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
                firebaseDBHelper.showResultUsers(search, resultUsersList);
            }
        });

        currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        setRecyclerViewLayoutManager(currentLayoutManagerType);
    }

    public void connect() {
        firebaseDBHelper.getRequestsReference().addValueEventListener(requestsListener);
        firebaseDBHelper.getCurrentUserReference().child(Constants.DATABASE_ONLINE_CHILD).setValue(true);
    }

    public void disconnect() {
        firebaseDBHelper.getRequestsReference().removeEventListener(requestsListener);
        firebaseDBHelper.getCurrentUserReference().child(Constants.DATABASE_ONLINE_CHILD).setValue(false);
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

        RecyclerView.LayoutManager layoutManager;
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
