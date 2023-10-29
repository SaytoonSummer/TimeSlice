package com.example.pomodoro.Task.ListSide;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pomodoro.R;
import com.example.pomodoro.Task.TaskSide.CreateTaskActivity;
import com.example.pomodoro.Task.TaskSide.TaskModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class TaskListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskListAdapter taskListAdapter;
    private List<TaskModel> taskList;
    private String listId;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        progressBar = view.findViewById(R.id.progressBar);

        recyclerView = view.findViewById(R.id.recyclerView2);
        taskList = new ArrayList<>();
        Bundle arguments = getArguments();
        if (arguments != null) {
            listId = arguments.getString("listId");
        }
        taskListAdapter = new TaskListAdapter(taskList, requireContext());
        taskListAdapter.initialize(listId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(taskListAdapter);

        Button createTaskButton = view.findViewById(R.id.createtask);
        createTaskButton.setOnClickListener(v -> openCreateTaskActivity());

        loadTasksFromDatabase(listId);

        return view;
    }


    private void openCreateTaskActivity() {
        Intent intent = new Intent(requireContext(), CreateTaskListActivity.class);
        intent.putExtra("listId", listId);
        startActivity(intent);
    }

    private void loadTasksFromDatabase(String listId) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        CollectionReference taskListCollection = FirebaseFirestore.getInstance()
                .collection("lists").document(listId).collection("taskslist");

        taskListCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(requireContext(), "Error cargando tareas en tiempo real", Toast.LENGTH_SHORT).show();

                Log.e("LoadData", "Error cargando datos en tiempo real: " + e.getMessage());

                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                return;
            }

            List<TaskModel> previousList = new ArrayList<>(taskList);

            taskList.clear();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                if (document.exists() && document.toObject(TaskModel.class) != null) {
                    TaskModel task = document.toObject(TaskModel.class);

                    taskList.add(task);
                }
            }

            if (!taskList.equals(previousList)) {
                taskListAdapter.notifyDataSetChanged();
            }

            Log.d("LoadData", "Datos cargados con éxito en tiempo real. Cantidad de elementos: " + taskList.size());

            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });
    }
}



