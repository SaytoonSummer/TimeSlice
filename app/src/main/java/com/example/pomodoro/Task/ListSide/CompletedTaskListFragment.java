package com.example.pomodoro.Task.ListSide;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pomodoro.R;
import com.example.pomodoro.Task.TaskSide.TaskModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CompletedTaskListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskListAdapter2 taskListAdapter2;
    private List<TaskModel> completedTaskList;
    private String listId;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_task_list, container, false);

        progressBar = view.findViewById(R.id.progressBar5);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.recyclerView3);
        completedTaskList = new ArrayList<>();
        taskListAdapter2 = new TaskListAdapter2(completedTaskList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(taskListAdapter2);

        Bundle arguments = getArguments();
        if (arguments != null) {
            listId = arguments.getString("listId");
        }

        loadCompletedTasksFromDatabase(listId);

        return view;
    }

    private void loadCompletedTasksFromDatabase(String listId) {
        CollectionReference completedTaskListCollection = FirebaseFirestore.getInstance()
                .collection("lists").document(listId).collection("completedtaskslist");

        completedTaskListCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(requireContext(), "Error cargando tareas completadas en tiempo real", Toast.LENGTH_SHORT).show();

                Log.e("LoadCompletedTasks", "Error cargando datos de tareas completadas en tiempo real: " + e.getMessage());
                progressBar.setVisibility(View.GONE);
                return;
            }

            List<TaskModel> previousList = new ArrayList<>(completedTaskList);

            completedTaskList.clear();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                if (document.exists() && document.toObject(TaskModel.class) != null) {
                    TaskModel completedTask = document.toObject(TaskModel.class);
                    completedTaskList.add(completedTask);
                }
            }

            taskListAdapter2.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);

            Log.d("LoadCompletedTasks", "Datos de tareas completadas cargados con éxito. Cantidad de elementos: " + completedTaskList.size());
        });
    }
}
