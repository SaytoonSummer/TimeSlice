package com.example.pomodoro.Task.TaskSide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pomodoro.R;

import java.util.List;

public class TaskAdapter2 extends RecyclerView.Adapter<TaskAdapter2.ViewHolder> {

    private List<TaskModel> taskList2;
    private Context context;
    private OnTaskClickListener onTaskClickListener;

    public TaskAdapter2(List<TaskModel> taskList2, Context context, OnTaskClickListener onTaskClickListener) {
        this.taskList2 = taskList2;
        this.context = context;
        this.onTaskClickListener = onTaskClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completed_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskModel task = taskList2.get(position);
        holder.bind(task);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < taskList2.size()) {
            taskList2.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemCount() {
        return taskList2.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatButton completedTaskButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            completedTaskButton = itemView.findViewById(R.id.editar);

            // Configura el clic del botón
            completedTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        TaskModel completedTask = taskList2.get(adapterPosition);
                        onTaskClickListener.onCompleteTask(completedTask, adapterPosition);
                    }
                }
            });
        }

        public void bind(TaskModel task) {
            completedTaskButton.setText(task.getTaskName());
            // Puedes agregar más configuraciones aquí si es necesario
        }
    }

    public interface OnTaskClickListener {
        void onEditButtonClick(TaskModel task);

        void onCompleteTask(TaskModel task, int position);
    }
}
