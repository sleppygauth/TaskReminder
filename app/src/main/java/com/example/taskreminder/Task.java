package com.example.taskreminder;

public class Task {
    private int id;
    private String title;
    private String description;
    private long reminderTime;

    public Task(int id, String title,
                String description, long reminderTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.reminderTime = reminderTime;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getReminderTime() {
        return reminderTime;
    }
}