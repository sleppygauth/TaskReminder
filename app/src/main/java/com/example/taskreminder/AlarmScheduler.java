package com.example.taskreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmScheduler {

    private AlarmScheduler() {
    }

    private static PendingIntent getAlarmPendingIntent(
            Context context, int taskId,
            String title, String description, long time) {

        Intent intent = new Intent(context, ReminderReceiver.class);

        intent.putExtra("task_id", taskId);
        intent.putExtra("task_title", title);
        intent.putExtra("task_description", description);
        intent.putExtra("reminder_time", time);

        return PendingIntent.getBroadcast(
                context,
                taskId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE
        );
    }

    public static void schedule(
            Context context, int taskId,
            String title, String description, long time) {

        if (time <= System.currentTimeMillis()) {
            return;
        }

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(
                        Context.ALARM_SERVICE);

        if (alarmManager == null) {
            return;
        }

        PendingIntent pendingIntent = getAlarmPendingIntent(
                context, taskId, title, description, time);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                !alarmManager.canScheduleExactAlarms()) {

            // Graceful fallback if exact-alarm access is not granted.
            alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    time,
                    pendingIntent
            );

            return;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        time,
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        time,
                        pendingIntent
                );
            }
        } catch (SecurityException e) {
            // The permission could have changed between checking
            // it and scheduling the alarm.
            alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    time,
                    pendingIntent
            );
        }
    }

    public static void cancel(Context context, int taskId) {
        Intent intent = new Intent(context, ReminderReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(
                        Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        pendingIntent.cancel();
    }
}