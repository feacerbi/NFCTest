package com.felipeacerbi.nfctest.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.utils.Constants;
import com.felipeacerbi.nfctest.utils.FirebaseDBHelper;

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

                    FirebaseDBHelper firebaseDBHelper = new FirebaseDBHelper(context);
                    firebaseDBHelper.getGameReference(gameId).child("ready").setValue("refused");

                    Toast.makeText(context, R.string.request_refused, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
