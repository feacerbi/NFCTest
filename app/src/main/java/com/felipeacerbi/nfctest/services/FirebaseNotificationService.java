package com.felipeacerbi.nfctest.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.activities.TicTacToeActivity;
import com.felipeacerbi.nfctest.activities.TicTacToePlayActivity;
import com.felipeacerbi.nfctest.firebasemodels.RequestDB;
import com.felipeacerbi.nfctest.firebasemodels.TicTacToeGameDB;
import com.felipeacerbi.nfctest.firebasemodels.UserDB;
import com.felipeacerbi.nfctest.models.Request;
import com.felipeacerbi.nfctest.models.TicTacToeGame;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseNotificationService extends com.google.firebase.messaging.FirebaseMessagingService {

    private FirebaseHelper firebaseHelper;
    private ValueEventListener requestsListener;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseMessaging.getInstance().subscribeToTopic("test");

        firebaseHelper = new FirebaseHelper(this);

        requestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final Request request = getCurrentUserRequest(dataSnapshot);
                if(request != null) {
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(FirebaseNotificationService.this)
                            .setContentTitle("New request from " + firebaseHelper.getUserReference(request.getRequestDB().getRequester()).child("name").getKey())
                            .setContentText("Touch to start a new game")
                            .setSmallIcon(android.R.drawable.stat_sys_warning)
                            .setAutoCancel(true)
                            .setContentIntent(getNewGameIntent(request))
                            .setDeleteIntent(null)
                            .addAction(R.drawable.nfc_logo_ok, "ACCEPT", getNewGameIntent(request))
                            .addAction(R.drawable.nfc_logo_fail, "REFUSE", null);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify((int) System.currentTimeMillis(), notification.build());

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebaseHelper.getCurrentUserReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserDB dbUser = dataSnapshot.getValue(UserDB.class);
                if(dbUser.isOnline()) {
                    firebaseHelper.getRequestsReference().removeEventListener(requestsListener);
                } else {
                    firebaseHelper.getRequestsReference().addValueEventListener(requestsListener);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Request getCurrentUserRequest(DataSnapshot requestsSnapshot) {
        for (DataSnapshot requestSnapshot : requestsSnapshot.getChildren()) {
            RequestDB requestDB = requestSnapshot.getValue(RequestDB.class);

            if (requestDB.getReceiver().equals(firebaseHelper.getLoginName())) {
                return new Request(requestSnapshot.getKey(), requestDB);
            }
        }
        return null;
    }

    public PendingIntent getNewGameIntent(Request request) {

        String currentUser = firebaseHelper.getLoginName();
        TicTacToeGame ticTacToeGame = new TicTacToeGame(new TicTacToeGameDB(request.getRequestDB().getRequester(), currentUser));

        Intent resultIntent = new Intent(this, TicTacToePlayActivity.class)
                .putExtra("game", ticTacToeGame)
                .putExtra("player", Constants.PLAYER_TWO)
                .putExtra("requestId", request.getId());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(TicTacToePlayActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        // Gets a PendingIntent containing the entire back stack
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public PendingIntent getCancelIntent(Request request) {

        Intent resultIntent = new Intent(this, FirebaseNotificationService.class)
                .putExtra("requestId", request.getId());

        return PendingIntent.getService(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            //Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
