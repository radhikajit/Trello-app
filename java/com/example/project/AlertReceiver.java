package com.example.project;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        NotificationHelper notificationHelper = new NotificationHelper(context);
        String txt= bundle.getString("alarm_message");
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(txt);
        notificationHelper.getManager().notify(1, nb.build());
    }
}