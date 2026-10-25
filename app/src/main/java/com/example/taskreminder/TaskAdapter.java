package com.example.taskreminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class TaskAdapter
        extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnDeleteClickListener {
        void onDelete(Task task);
    }

    private final List<Task> tasks;
    private final OnDeleteClickListener deleteListener;

    public TaskAdapter(List<Task> tasks,
                       OnDeleteClickListener deleteListener) {
        this.tasks = tasks;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull TaskViewHolder holder, int position) {

        Task task = tasks.get(position);

        holder.tvTitle.setText(task.getTitle());

        String description = task.getDescription();
        holder.tvDescription.setText(
                description == null || description.trim().isEmpty()
                        ? "No description" : description);

        Date date = new Date(task.getReminderTime());

        DateFormat formatter =
                DateFormat.getDateTimeInstance(
                        DateFormat.MEDIUM, DateFormat.SHORT);

        holder.tvDateTime.setText(formatter.format(date));

        holder.btnDelete.setOnClickListener(v ->
                deleteListener.onDelete(task));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvDescription;
        TextView tvDateTime;
        ImageButton btnDelete;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription =
                    itemView.findViewById(R.id.tvDescription);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}