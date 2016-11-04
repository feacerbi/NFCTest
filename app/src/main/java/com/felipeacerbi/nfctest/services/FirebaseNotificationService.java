package com.felipeacerbi.nfctest.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.felipeacerbi.nfctest.receivers.NotificationHandler;
import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.activities.TicTacToePlayActivity;
import com.felipeacerbi.nfctest.firebasemodels.RequestDB;
import com.felipeacerbi.nfctest.firebasemodels.TicTacToeGameDB;
import com.felipeacerbi.nfctest.models.Request;
import com.felipeacerbi.nfctest.models.TicTacToeGame;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;
import com.google.firebase.database.*;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseNotificationService extends com.google.firebase.messaging.FirebaseMessagingService {

    private FirebaseDBHelper firebaseDBHelper;
    private ValueEventListener requestsListener;
    private ValueEventListener onlineListener;

    @Override
    public void onCreate() {
        super.onCreate();

        //FirebaseMessaging.getInstance().subscribeToTopic("test");

        firebaseDBHelper = new FirebaseDBHelper(this);

        requestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final Request request = getCurrentUserRequest(dataSnapshot);
                if(request != null) {
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(FirebaseNotificationService.this)
                            .setContentTitle("New request from " + request.getRequestDB().getRequester())
                            .setContentText(getString(R.string.touch_to_start))
                            .setSmallIcon(android.R.drawable.stat_sys_warning)
                            .setAutoCancel(true)
                            .setWhen(System.currentTimeMillis())
                            .setContentIntent(getNewGameIntent(request))
                            .setDeleteIntent(getCancelIntent(request))
                            .addAction(R.drawable.nfc_logo_ok, getString(R.string.accept), getNewGameIntent(request))
                            .addAction(R.drawable.nfc_logo_fail, getString(R.string.refuse), getCancelIntent(request));

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(Constants.GAME_REQUEST_NOTIFICATION, notification.build());

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        onlineListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isOnline = dataSnapshot.getValue(Boolean.class);
                if(isOnline) {
                    firebaseDBHelper.getRequestsReference().removeEventListener(requestsListener);
                } else {
                    firebaseDBHelper.getRequestsReference().addValueEventListener(requestsListener);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebaseDBHelper.getCurrentUserReference().child(Constants.DATABASE_ONLINE_CHILD).addValueEventListener(onlineListener);
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

    public PendingIntent getNewGameIntent(Request request) {

        String currentUser = firebaseDBHelper.getLoginName();
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

        String gameId = request.getRequestDB().getRequester() + request.getRequestDB().getReceiver();

        Intent resultIntent = new Intent(this, NotificationHandler.class)
                .setAction(Constants.ACTION_PLAYER_REFUSE_REQUEST)
                .putExtra("gameId", gameId);

        return PendingIntent.getBroadcast(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        firebaseDBHelper.getRequestsReference().removeEventListener(requestsListener);
        firebaseDBHelper.getCurrentUserReference().removeEventListener(onlineListener);
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
