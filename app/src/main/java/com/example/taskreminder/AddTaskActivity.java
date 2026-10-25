package com.example.taskreminder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private TextView tvSelectedDateTime;
    private Calendar selectedDateTime;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        tvSelectedDateTime = findViewById(R.id.tvSelectedDateTime);

        Button btnDate = findViewById(R.id.btnDate);
        Button btnTime = findViewById(R.id.btnTime);
        Button btnSave = findViewById(R.id.btnSave);

        databaseHelper = new DatabaseHelper(this);

        // Default to the current date and time.
        selectedDateTime = Calendar.getInstance();

        updateDateTimeText();

        btnDate.setOnClickListener(v -> showDatePicker());
        btnTime.setOnClickListener(v -> showTimePicker());
        btnSave.setOnClickListener(v -> saveTask());
    }

    private void showDatePicker() {
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDateTime.set(Calendar.YEAR, selectedYear);
                    selectedDateTime.set(Calendar.MONTH, selectedMonth);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, selectedDay);
                    updateDateTimeText();
                },
                year, month, day
        );

        dialog.getDatePicker().setMinDate(
                System.currentTimeMillis() - 1000);

        dialog.show();
    }

    private void showTimePicker() {
        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                    selectedDateTime.set(Calendar.MINUTE, selectedMinute);
                    selectedDateTime.set(Calendar.SECOND, 0);
                    selectedDateTime.set(Calendar.MILLISECOND, 0);
                    updateDateTimeText();
                },
                hour, minute, false
        );

        dialog.show();
    }

    private void updateDateTimeText() {
        DateFormat formatter = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM, DateFormat.SHORT);

        tvSelectedDateTime.setText(
                "Reminder: " +
                        formatter.format(selectedDateTime.getTime()));
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String description =
                etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Enter a task title");
            etTitle.requestFocus();
            return;
        }

        long reminderTime = selectedDateTime.getTimeInMillis();

        if (reminderTime <= System.currentTimeMillis()) {
            Toast.makeText(this,
                    "Please select a future date and time",
                    Toast.LENGTH_LONG).show();
            return;
        }

        long taskId = databaseHelper.insertTask(
                title, description, reminderTime);

        if (taskId == -1) {
            Toast.makeText(this,
                    "Could not save task",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Schedule only after the database insert succeeds.
        AlarmScheduler.schedule(
                this,
                (int) taskId,
                title,
                description,
                reminderTime
        );

        Toast.makeText(this,
                "Task saved",
                Toast.LENGTH_SHORT).show();

        setResult(Activity.RESULT_OK);
        finish();
    }
}