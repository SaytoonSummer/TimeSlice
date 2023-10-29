package com.example.pomodoro.Task.ListSide;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pomodoro.R;
import com.example.pomodoro.Task.TaskSide.TaskModel;

import java.util.List;

public class TaskListAdapter2 extends RecyclerView.Adapter<TaskListAdapter2.TaskViewHolder2> {

    private List<TaskModel> completedTaskList;

    public TaskListAdapter2(List<TaskModel> completedTaskList) {
        this.completedTaskList = completedTaskList;
    }

    @NonNull
    @Override
    public TaskViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completed_task, parent, false);
        return new TaskViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder2 holder, int position) {
        TaskModel completedTask = completedTaskList.get(position);
        holder.bind(completedTask);
    }

    @Override
    public int getItemCount() {
        return completedTaskList.size();
    }

    public class TaskViewHolder2 extends RecyclerView.ViewHolder {

        private Button completedTaskButton;

        public TaskViewHolder2(@NonNull View itemView) {
            super(itemView);
            completedTaskButton = itemView.findViewById(R.id.editar);
        }

        public void bind(TaskModel completedTask) {
            completedTaskButton.setText(completedTask.getTaskName());
        }
    }
}
