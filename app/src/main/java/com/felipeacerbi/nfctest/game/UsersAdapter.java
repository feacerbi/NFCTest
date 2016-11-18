package com.felipeacerbi.nfctest.game;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.models.User;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private List<User> users;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameField;
        private final ImageView onlineField;
        private final ImageView playingField;


        public ViewHolder(View itemView) {
            super(itemView);

            nameField = (TextView) itemView.findViewById(R.id.name_field);
            onlineField = (ImageView) itemView.findViewById(R.id.online_indicator);
            playingField = (ImageView) itemView.findViewById(R.id.playing_indicator);

        }

        public TextView getNameField() {
            return nameField;
        }

        public ImageView getOnlineField() {
            return onlineField;
        }

        public ImageView getPlayingField() {
            return playingField;
        }
    }

    public UsersAdapter(Context context, List<User> users) {
        this.users = users;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
            final User user = getUsers().get(position);
            final FirebaseDBHelper firebaseDBHelper = new FirebaseDBHelper(context);

            holder.getNameField().setText(user.getName());

            final DatabaseReference userReference = firebaseDBHelper.getUserReference(user.getUsername());
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    final User user = new User(dataSnapshot);
                    holder.getOnlineField().setImageResource((user.isOnline()) ?
                            R.drawable.ic_signal_wifi_4_bar_black_24dp :
                            R.drawable.ic_signal_wifi_off_black_24dp);
                    holder.getPlayingField().setImageAlpha((user.isPlaying()) ?
                            255 :
                            40);
                    if(user.isPlaying()) {
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(context, R.string.user_playing, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String message = "Invite " + user.getName() + " to play a game?";
                                if(!user.isOnline()) {
                                    message = user.getName() + " is offline, invite anyway?";
                                }
                                AlertDialog.Builder playAlert = new AlertDialog.Builder(context);
                                playAlert
                                        .setTitle(R.string.send_request)
                                        .setMessage(message)
                                        .setCancelable(true)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                DatabaseReference newRequest = firebaseDBHelper.getRequestsReference().push();
                                                newRequest.setValue(
                                                        new RequestDB(
                                                                firebaseDBHelper.getLoginName(),
                                                                user.getUsername()));

                                                context.startActivity(
                                                        new Intent(context, TicTacToePlayActivity.class)
                                                                .putExtra("game", new TicTacToeGame(
                                                                        new TicTacToeGameDB(
                                                                                firebaseDBHelper.getLoginName(),
                                                                                user.getUsername())))
                                                                .putExtra("player", Constants.PLAYER_ONE)
                                                                .putExtra("requestId", newRequest.getKey()));
                                            }
                                        })
                                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        })
                                        .show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    public List<User> getUsers() {
        return users;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
