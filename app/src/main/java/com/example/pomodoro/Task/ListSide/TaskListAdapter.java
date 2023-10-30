package com.example.pomodoro.Task.ListSide;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pomodoro.R;
import com.example.pomodoro.Task.TaskSide.EditTaskActivity;
import com.example.pomodoro.Task.TaskSide.TaskModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    private List<TaskModel> taskList;
    private Context context;
    private String listId;
    private MediaPlayer mediaPlayer;
    private boolean soundEnabled;

    public TaskListAdapter(List<TaskModel> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
        this.mediaPlayer = MediaPlayer.create(context, R.raw.tasksound);
        this.soundEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sound_preference", true);
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
        notifyDataSetChanged();
    }

    public void initialize(String listId) {
        this.listId = listId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel task = taskList.get(position);

        holder.editButton.setText(task.getTaskName());

        holder.imageView.setOnClickListener(v -> completeTask(task));

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditTaskListActivity.class);
            intent.putExtra("TaskModel", task);
            intent.putExtra("DocumentId", task.getDocumentId());
            intent.putExtra("ListId", listId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button editButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView4);
            editButton = itemView.findViewById(R.id.editar);
        }
    }

    private void completeTask(TaskModel task) {
        moveTaskToCompletedList(task);
        deleteTask(task);

        if (soundEnabled) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(context, R.raw.tasksound);
            }
            mediaPlayer.start();
        }
    }

    private void moveTaskToCompletedList(TaskModel task) {
        CollectionReference completedTasksCollection = FirebaseFirestore.getInstance()
                .collection("lists").document(listId).collection("completedtaskslist");

        completedTasksCollection.add(task)
                .addOnSuccessListener(documentReference -> {
                    Log.d("CompleteTask", "Tarea completada añadida a la subcolección");
                })
                .addOnFailureListener(e -> {
                    Log.e("CompleteTask", "Error al añadir tarea completada a la subcolección: " + e.getMessage());
                });
    }

    private void deleteTask(TaskModel task) {
        DocumentReference taskReference = FirebaseFirestore.getInstance()
                .collection("lists").document(listId).collection("taskslist").document(task.getDocumentId());

        taskReference.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("CompleteTask", "Tarea eliminada de la colección original");
                })
                .addOnFailureListener(e -> {
                    Log.e("CompleteTask", "Error al eliminar tarea de la colección original: " + e.getMessage());
                });
    }
}

