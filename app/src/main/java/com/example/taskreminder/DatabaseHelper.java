package com.example.taskreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TASKS = "tasks";
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_TIME = "reminder_time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable =
                "CREATE TABLE " + TABLE_TASKS + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_TITLE + " TEXT NOT NULL, " +
                        COL_DESCRIPTION + " TEXT, " +
                        COL_TIME + " INTEGER NOT NULL)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        // Add database migration logic here when the schema changes.
    }

    public long insertTask(String title, String description,
                           long reminderTime) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_TIME, reminderTime);

        return db.insert(TABLE_TASKS, null, values);
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_TASKS,
                null,
                null,
                null,
                null,
                null,
                COL_TIME + " ASC")) {

            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(COL_ID));

                String title = cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_TITLE));

                String description = cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_DESCRIPTION));

                long reminderTime = cursor.getLong(
                        cursor.getColumnIndexOrThrow(COL_TIME));

                tasks.add(new Task(
                        id, title, description, reminderTime));
            }
        }

        return tasks;
    }

    public boolean deleteTask(int id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_TASKS,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }
}