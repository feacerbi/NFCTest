package com.felipeacerbi.nfctest.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.felipeacerbi.nfctest.models.Request;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseHelper;

public class NotificationHandler extends BroadcastReceiver {
    public NotificationHandler() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {
            case Constants.ACTION_PLAYER_REFUSE_REQUEST:
                String gameId = intent.getStringExtra("gameId");
                if(!gameId.equals("")) {
                    NotificationManager notificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(Constants.GAME_REQUEST_NOTIFICATION);

                    FirebaseHelper firebaseHelper = new FirebaseHelper(context);
                    firebaseHelper.getGameReference(gameId).child("ready").setValue("refused");

                    Toast.makeText(context, "Game request refused", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
