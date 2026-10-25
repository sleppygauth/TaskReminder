package com.example.taskreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int taskId = intent.getIntExtra("task_id", -1);

        String title = intent.getStringExtra("task_title");

        String description =
                intent.getStringExtra("task_description");

        if (taskId < 0 || title == null) {
            return;
        }

        NotificationHelper.showNotification(
                context,
                taskId,
                title,
                description
        );
    }
}