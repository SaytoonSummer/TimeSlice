package com.example.pomodoro.Task.TaskSide;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pomodoro.R;
import com.example.pomodoro.Task.ListSide.ManagementListActivity;
import com.example.pomodoro.Task.TaskSide.ManagementTasksActivity;

public class TasksActivity extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_tasks, container, false);

        Button buttonTasks = view.findViewById(R.id.button);
        Button buttonLists = view.findViewById(R.id.button4);

        buttonTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ManagementTasksActivity.class);
                startActivity(intent);
            }
        });

        buttonLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ManagementListActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}