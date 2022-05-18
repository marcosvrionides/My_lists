package com.example.todolistapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Date;

public class ReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "task_reminders")
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                .setContentTitle("Reminder : ")
                .setContentText("Please check your tasks")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= 21) builder.setVibrate(new long[0]);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        int id;
        id = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE); //generate a random id everytime a notification is made so that it doesn't override the previous ones
        notificationManager.notify(id, builder.build());
    }
}
