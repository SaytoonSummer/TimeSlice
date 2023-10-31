package com.example.pomodoro.Task.TaskSide;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class TareasCompletadasFragment extends Fragment implements TaskAdapter2.OnTaskClickListener {

    private RecyclerView recyclerView;
    private TaskAdapter2 taskAdapter2;
    private List<TaskModel> taskList2;
    private CollectionReference completedTaskCollection;
    private CollectionReference taskCollection;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tareas_completadas, container, false);

        progressBar = view.findViewById(R.id.progressBar4);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.recyclerView3);
        taskList2 = new ArrayList<>();
        taskAdapter2 = new TaskAdapter2(taskList2, requireContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(taskAdapter2);

        completedTaskCollection = FirebaseFirestore.getInstance().collection("completedtasks");
        taskCollection = FirebaseFirestore.getInstance().collection("task");

        loadCompletedTasksFromDatabase();

        return view;
    }

    private void loadCompletedTasksFromDatabase() {
        taskList2.clear();

        completedTaskCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        TaskModel task = document.toObject(TaskModel.class);
                        taskList2.add(task);
                    }

                    taskAdapter2.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    Log.d("LoadCompletedTasks", "Datos de tareas completadas cargados exitosamente. Cantidad de elementos: " + taskList2.size());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error cargando tareas completadas", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                    Log.e("LoadCompletedTasks", "Error al cargar datos de tareas completadas: " + e.getMessage());
                });
    }

    @Override
    public void onEditButtonClick(TaskModel task) {
        // Implementación según necesidades
    }

    @Override
    public void onCompleteTask(TaskModel task, int position) {
        // Implementación según necesidades
    }

    private void removeCompletedTaskFromDatabase(TaskModel task) {
        // Implementación según necesidades
    }
}
