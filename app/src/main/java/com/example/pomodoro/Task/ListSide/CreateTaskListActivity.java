package com.example.pomodoro.Task.ListSide;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pomodoro.R;
import com.example.pomodoro.Task.TaskSide.TaskModel;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateTaskListActivity extends AppCompatActivity {

    private Button lowPriorityButton, mediumPriorityButton, highPriorityButton;
    private String selectedPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task_list);

        Button createTaskButton = findViewById(R.id.createPomodoro);

        lowPriorityButton = findViewById(R.id.button13);
        mediumPriorityButton = findViewById(R.id.button14);
        highPriorityButton = findViewById(R.id.button15);



        selectedPriority = "Baja";

        createTaskButton.setOnClickListener(v -> createTask());

        lowPriorityButton.setOnClickListener(v -> setPriority("Baja"));
        mediumPriorityButton.setOnClickListener(v -> setPriority("Media"));
        highPriorityButton.setOnClickListener(v -> setPriority("Alta"));
    }

    private void setPriority(String priority) {
        lowPriorityButton.setBackgroundColor(priority.equals("Baja") ? Color.parseColor("#FF6666") : Color.TRANSPARENT);
        mediumPriorityButton.setBackgroundColor(priority.equals("Media") ? Color.parseColor("#FF6666") : Color.TRANSPARENT);
        highPriorityButton.setBackgroundColor(priority.equals("Alta") ? Color.parseColor("#FF6666") : Color.TRANSPARENT);

        selectedPriority = priority;
    }

    private void createTask() {
        EditText nameEditText = findViewById(R.id.editTextText);
        EditText descriptionEditText = findViewById(R.id.editTextText5);
        EditText noteEditText = findViewById(R.id.editTextText6);
        EditText tagEditText = findViewById(R.id.editTextText3);

        String listId = getIntent().getStringExtra("listId");

        CollectionReference taskListCollection = FirebaseFirestore.getInstance()
                .collection("lists").document(listId).collection("taskslist");

        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String note = noteEditText.getText().toString().trim();
        String tag = tagEditText.getText().toString().trim();

        TaskModel task = new TaskModel(name, "", description, selectedPriority, note, tag);

        taskListCollection.add(task)
                .addOnSuccessListener(documentReference -> {
                    task.setDocumentId(documentReference.getId());
                    documentReference.set(task)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(CreateTaskListActivity.this,
                                        "Tarea creada con éxito", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(CreateTaskListActivity.this,
                                        "Error al actualizar la tarea con la ID", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateTaskListActivity.this,
                            "Error al guardar la tarea", Toast.LENGTH_SHORT).show();
                });
    }
}
