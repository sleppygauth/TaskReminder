package com.example.taskreminder;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerTasks;
    private TextView tvEmpty;

    private DatabaseHelper databaseHelper;
    private TaskAdapter taskAdapter;
    private final List<Task> taskList = new ArrayList<>();

    private final ActivityResultLauncher<Intent> addTaskLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> loadTasks()
            );

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        // Permission result received.
                        // Notifications can be displayed when allowed.
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        recyclerTasks = findViewById(R.id.recyclerTasks);
        tvEmpty = findViewById(R.id.tvEmpty);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        NotificationHelper.createChannel(this);
        requestNotificationPermission();

        recyclerTasks.setLayoutManager(
                new LinearLayoutManager(this));

        taskAdapter = new TaskAdapter(taskList, task -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete task")
                    .setMessage(
                            "Delete \"" + task.getTitle() + "\"?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete", (dialog, which) -> {
                        databaseHelper.deleteTask(task.getId());
                        AlarmScheduler.cancel(
                                MainActivity.this, task.getId());
                        loadTasks();
                    })
                    .show();
        });

        recyclerTasks.setAdapter(taskAdapter);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this, AddTaskActivity.class);
            addTaskLauncher.launch(intent);
        });

        loadTasks();
    }

    private void loadTasks() {
        taskList.clear();
        taskList.addAll(databaseHelper.getAllTasks());

        taskAdapter.notifyDataSetChanged();

        boolean isEmpty = taskList.isEmpty();

        tvEmpty.setVisibility(isEmpty ? TextView.VISIBLE : TextView.GONE);
        recyclerTasks.setVisibility(
                isEmpty ? RecyclerView.GONE : RecyclerView.VISIBLE);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {

            notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}