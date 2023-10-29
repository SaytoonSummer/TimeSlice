package com.example.pomodoro.Task.TaskSide;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pomodoro.R;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText nameEditText, descriptionEditText, noteEditText, tagEditText;
    private Button createTaskButton, lowPriorityButton, mediumPriorityButton, highPriorityButton;
    private CollectionReference taskCollection;

    private String selectedPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        taskCollection = firestore.collection("task");

        nameEditText = findViewById(R.id.editTextText);
        descriptionEditText = findViewById(R.id.editTextText5);
        noteEditText = findViewById(R.id.editTextText6);
        tagEditText = findViewById(R.id.editTextText3);
        createTaskButton = findViewById(R.id.createPomodoro);
        lowPriorityButton = findViewById(R.id.button13);
        mediumPriorityButton = findViewById(R.id.button14);
        highPriorityButton = findViewById(R.id.button15);

        selectedPriority = "Baja";

        createTaskButton.setOnClickListener(v -> saveTaskToFirestore());

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

    private void saveTaskToFirestore() {
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String note = noteEditText.getText().toString().trim();
        String tag = tagEditText.getText().toString().trim();

        if (!name.isEmpty()) {
            TaskModel task = new TaskModel(name, "", description, selectedPriority, note, tag);

            taskCollection.add(task)
                    .addOnSuccessListener(documentReference -> {
                        task.setDocumentId(documentReference.getId());
                        documentReference.set(task)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(CreateTaskActivity.this, "Tarea guardada exitosamente", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(CreateTaskActivity.this, "Error al actualizar la Tarea con la ID", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CreateTaskActivity.this, "Error al guardar la Tarea", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Por favor, completa el nombre de la tarea", Toast.LENGTH_SHORT).show();
        }
    }
}
