package com.felipeacerbi.nfctest.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.activities.TicTacToePlayActivity;
import com.felipeacerbi.nfctest.firebasemodels.RequestDB;
import com.felipeacerbi.nfctest.firebasemodels.TicTacToeGameDB;
import com.felipeacerbi.nfctest.firebasemodels.UserDB;
import com.felipeacerbi.nfctest.models.FeedPost;
import com.felipeacerbi.nfctest.models.FeedPostText;
import com.felipeacerbi.nfctest.models.TicTacToeGame;
import com.felipeacerbi.nfctest.models.User;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FeedPostsAdapter extends RecyclerView.Adapter<FeedPostsAdapter.ViewHolder> {

    private List<FeedPost> posts;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView userField;
        private final TextView timeField;
        private final TextView likeButton;
        private final TextView commentButton;
        private final ImageView profilePicture;


        public ViewHolder(View itemView) {
            super(itemView);

            profilePicture = (ImageView) itemView.findViewById(R.id.profile_picture);
            userField = (TextView) itemView.findViewById(R.id.post_title);
            timeField = (TextView) itemView.findViewById(R.id.post_date_time);
            likeButton = (TextView) itemView.findViewById(R.id.like_button);
            commentButton = (TextView) itemView.findViewById(R.id.comment_button);

        }

        public TextView getUserField() {
            return userField;
        }

        public TextView getTimeField() {
            return timeField;
        }

        public TextView getLikeButton() {
            return likeButton;
        }

        public TextView getCommentButton() {
            return commentButton;
        }

        public ImageView getProfilePicture() {
            return profilePicture;
        }
    }

    public static class ViewHolderText extends ViewHolder {

        private final TextView contentField;

        public ViewHolderText(View itemView) {
            super(itemView);

            contentField = (TextView) itemView.findViewById(R.id.post_content);

        }

        public TextView getContentField() {
            return contentField;
        }
    }

    public static class ViewHolderPhoto extends ViewHolder {

        private final ImageView photoField;
        private final TextView photoCommentField;

        public ViewHolderPhoto(View itemView) {
            super(itemView);

            photoField = (ImageView) itemView.findViewById(R.id.post_photo);
            photoCommentField = (TextView) itemView.findViewById(R.id.post_photo_comment);

        }

        public ImageView getPhotoField() {
            return photoField;
        }

        public TextView getPhotoCommentField() {
            return photoCommentField;
        }
    }

    public FeedPostsAdapter(List<FeedPost> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case Constants.POST_TYPE_TEXT:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_card_text, parent, false);
                itemView.setTag(0, parent);
                return new ViewHolderText(itemView);
            case Constants.POST_TYPE_PHOTO:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_card_photo, parent, false);
                itemView.setTag(0, parent);
                return new ViewHolderPhoto(itemView);
            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_card_text, parent, false);
                itemView.setTag(0, parent);
                return new ViewHolderText(itemView);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            FeedPost post = getPosts().get(position);
            FirebaseDBHelper firebaseDBHelper = new FirebaseDBHelper(context);

            if(post instanceof FeedPostText) {
                ViewHolderText viewHolderText;
                FeedPostText feedPostText = (FeedPostText) post;
                if(holder instanceof ViewHolderText) {
                    viewHolderText = (ViewHolderText) holder;
                } else {
                    ViewGroup viewGroup = (ViewGroup) holder.itemView.getTag(0);
                    viewHolderText = new ViewHolderText(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_card_text, viewGroup, false));
                }
                viewHolderText.getUserField().setText(feedPostText.getUser());
                viewHolderText.getTimeField().setText(FeedPost.formatTime(feedPostText.getTime()));
                viewHolderText.getContentField().setText(feedPostText.getContent());
                // TODO Load image from Firebase Storage


            } else if(holder instanceof ViewHolderPhoto) {

            }
//            holder.getNameField().setText(user.getUserDB().getName());
//
//            final DatabaseReference userReference = firebaseDBHelper.getUserReference(user.getUsername());
//            userReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(final DataSnapshot dataSnapshot) {
//                    final UserDB dbUser = dataSnapshot.getValue(UserDB.class);
//                    holder.getOnlineField().setImageResource((dbUser.isOnline()) ?
//                            R.drawable.ic_signal_wifi_4_bar_black_24dp :
//                            R.drawable.ic_signal_wifi_off_black_24dp);
//                    holder.getPlayingField().setImageAlpha((dbUser.isPlaying()) ?
//                            255 :
//                            40);
//                    if(dbUser.isPlaying()) {
//                        holder.itemView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Toast.makeText(context, R.string.user_playing, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    } else {
//                        holder.itemView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                String message = "Invite " + dbUser.getName() + " to play a game?";
//                                if(!dbUser.isOnline()) {
//                                    message = dbUser.getName() + " is offline, invite anyway?";
//                                }
//                                AlertDialog.Builder playAlert = new AlertDialog.Builder(context);
//                                playAlert
//                                        .setTitle(R.string.send_request)
//                                        .setMessage(message)
//                                        .setCancelable(true)
//                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                DatabaseReference newRequest = firebaseDBHelper.getRequestsReference().push();
//                                                newRequest.setValue(
//                                                        new RequestDB(
//                                                                firebaseDBHelper.getLoginName(),
//                                                                user.getUsername()));
//
//                                                context.startActivity(
//                                                        new Intent(context, TicTacToePlayActivity.class)
//                                                                .putExtra("game", new TicTacToeGame(
//                                                                        new TicTacToeGameDB(
//                                                                                firebaseDBHelper.getLoginName(),
//                                                                                user.getUsername())))
//                                                                .putExtra("player", Constants.PLAYER_ONE)
//                                                                .putExtra("requestId", newRequest.getKey()));
//                                            }
//                                        })
//                                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                dialogInterface.cancel();
//                                            }
//                                        })
//                                        .show();
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
    }

    @Override
    public int getItemViewType(int position) {
        FeedPost feedPost = getPosts().get(position);
        return feedPost.getType();
    }

    public List<FeedPost> getPosts() {
        return posts;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
