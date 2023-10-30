package com.example.pomodoro.Task.TaskSide;

import static com.example.pomodoro.AppSettings.soundEnabled;

import android.content.Context;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pomodoro.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<TaskModel> taskList;
    private Context context;
    private OnTaskClickListener onTaskClickListener;
    private OnTaskCompleteListener onTaskCompleteListener;
    private MediaPlayer mediaPlayer;
    private boolean soundEnabled;

    public TaskAdapter(List<TaskModel> taskList, Context context, OnTaskClickListener onTaskClickListener, OnTaskCompleteListener onTaskCompleteListener) {
        this.taskList = taskList;
        this.context = context;
        this.onTaskClickListener = onTaskClickListener;
        this.onTaskCompleteListener = onTaskCompleteListener;
        this.mediaPlayer = MediaPlayer.create(context, R.raw.tasksound);
        this.soundEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sound_preference", true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskModel task = taskList.get(position);
        holder.bind(task);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < taskList.size()) {
            taskList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatButton editarButton;
        private ImageView circleImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editarButton = itemView.findViewById(R.id.editar);
            circleImageView = itemView.findViewById(R.id.imageView4);

            editarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        TaskModel clickedTask = taskList.get(adapterPosition);
                        onTaskClickListener.onEditButtonClick(clickedTask);
                    }
                }
            });

            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        TaskModel completedTask = taskList.get(adapterPosition);

                        soundEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sound_preference", true);

                        if (soundEnabled) {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.stop();
                                mediaPlayer.release();
                                mediaPlayer = MediaPlayer.create(itemView.getContext(), R.raw.tasksound);
                            }

                            mediaPlayer.start();
                        }

                        onTaskCompleteListener.onCompleteTask(completedTask);
                    }
                }
            });
        }

        public void bind(TaskModel task) {
            editarButton.setText(task.getTaskName());

            circleImageView.setImageResource(R.drawable.circleprogress);
        }
    }

    public interface OnTaskClickListener {
        void onEditButtonClick(TaskModel task);
    }

    public interface OnTaskCompleteListener {
        void onCompleteTask(TaskModel task);
    }
}

