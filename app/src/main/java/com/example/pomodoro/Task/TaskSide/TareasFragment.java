package com.example.pomodoro.Task.TaskSide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pomodoro.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TareasFragment extends Fragment implements TaskAdapter.OnTaskClickListener, TaskAdapter.OnTaskCompleteListener {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TaskModel> taskList;
    private CollectionReference taskCollection;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tareas, container, false);

        progressBar = view.findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.recyclerView2);
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, requireContext(), this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(taskAdapter);

        loadTasksFromDatabase();

        Button createTaskButton = view.findViewById(R.id.button2);

        createTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateTaskActivity();
            }
        });

        return view;
    }

    private void loadTasksFromDatabase() {
        taskList.clear();

        taskCollection = FirebaseFirestore.getInstance().collection("task");

        taskCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.e("LoadData", "Error al cargar datos en tiempo real: " + e.getMessage());
                return;
            }

            taskList.clear();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                TaskModel task = document.toObject(TaskModel.class);
                taskList.add(task);
            }

            taskAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);

            Log.d("LoadData", "Datos cargados exitosamente. Cantidad de elementos: " + taskList.size());
        });
    }

    private void openCreateTaskActivity() {
        Intent intent = new Intent(requireContext(), CreateTaskActivity.class);
        startActivity(intent);
    }

    @Override
    public void onEditButtonClick(TaskModel task) {
        openEditTaskActivity(task);
    }

    private void openEditTaskActivity(TaskModel task) {
        Intent intent = new Intent(requireContext(), EditTaskActivity.class);
        intent.putExtra("TaskModel", task);
        intent.putExtra("DocumentId", task.getDocumentId());
        startActivity(intent);
    }

    @Override
    public void onCompleteTask(TaskModel task) {
        Log.d("CompleteTask", "onCompleteTask" + task.getTaskName());

        int position = taskList.indexOf(task);
        taskAdapter.removeItem(position);

        Log.d("CompleteTask", "Eliminando");
        removeTaskFromDatabase(task);

        addCompletedTaskToDatabase(task);

        taskAdapter.notifyDataSetChanged();
    }

    private void removeTaskFromDatabase(TaskModel task) {
        taskCollection.document(task.getDocumentId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("RemoveTask", "Tarea eliminada con éxito");
                })
                .addOnFailureListener(e -> {
                    Log.e("RemoveTask", "Error al eliminar tarea: " + e.getMessage());
                });
    }

    private void addCompletedTaskToDatabase(TaskModel task) {
        FirebaseFirestore.getInstance().collection("completedtasks")
                .add(task)
                .addOnSuccessListener(documentReference -> {
                    Log.d("AddCompletedTask", "Tarea completada agregada con ID: " + documentReference.getId());

                    taskAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("AddCompletedTask", "Error al agregar tarea completada", e);
                });
    }

}
