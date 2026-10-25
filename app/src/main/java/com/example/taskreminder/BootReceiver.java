package com.example.taskreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == null ||
                !Intent.ACTION_BOOT_COMPLETED.equals(
                        intent.getAction())) {
            return;
        }

        DatabaseHelper databaseHelper =
                new DatabaseHelper(context);

        List<Task> tasks = databaseHelper.getAllTasks();

        long now = System.currentTimeMillis();

        for (Task task : tasks) {
            if (task.getReminderTime() > now) {
                AlarmScheduler.schedule(
                        context,
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getReminderTime()
                );
            }
        }

        databaseHelper.close();
    }
}