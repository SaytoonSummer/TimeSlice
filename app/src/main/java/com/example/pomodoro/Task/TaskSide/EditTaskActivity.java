package com.example.pomodoro.Task.TaskSide;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pomodoro.R;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditTaskActivity extends AppCompatActivity {

    private TaskModel task;
    private String documentId;

    private EditText nameEditText, tagsEditText, descriptionEditText, notesEditText;
    private Button lowPriorityButton, mediumPriorityButton, highPriorityButton;
    private String selectedPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        task = getIntent().getParcelableExtra("TaskModel");
        documentId = getIntent().getStringExtra("DocumentId");

        nameEditText = findViewById(R.id.editTextText);
        tagsEditText = findViewById(R.id.editTextText3);
        descriptionEditText = findViewById(R.id.editTextText5);
        notesEditText = findViewById(R.id.editTextText6);
        lowPriorityButton = findViewById(R.id.button13);
        mediumPriorityButton = findViewById(R.id.button14);
        highPriorityButton = findViewById(R.id.button15);

        selectedPriority = task.getPriority();

        lowPriorityButton.setOnClickListener(v -> setPriority("Baja"));
        mediumPriorityButton.setOnClickListener(v -> setPriority("Media"));
        highPriorityButton.setOnClickListener(v -> setPriority("Alta"));

        Button editButton = findViewById(R.id.createPomodoro);
        editButton.setOnClickListener(view -> onEditTaskClick());

        Button deleteButton = findViewById(R.id.createPomodoro2);
        deleteButton.setOnClickListener(view -> onDeleteTaskClick());

        if (task != null) {
            nameEditText.setText(task.getTaskName());
            tagsEditText.setText(task.getTag());
            descriptionEditText.setText(task.getDescription());
            notesEditText.setText(task.getNote());
            updatePriorityButtons();
        }
    }

    private void onEditTaskClick() {
        editTask();
    }

    private void onDeleteTaskClick() {
        deleteTask();
    }

    private void updateTaskInDatabase() {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("task").document(documentId);

        docRef.set(task)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Tarea actualizada correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al actualizar la tarea", Toast.LENGTH_SHORT).show();
                });
    }

    private void editTask() {
        String newName = nameEditText.getText().toString().trim();
        String newTags = tagsEditText.getText().toString().trim();
        String newDescription = descriptionEditText.getText().toString().trim();
        String newNotes = notesEditText.getText().toString().trim();

        if (!newName.isEmpty()) {
            task.setTaskName(newName);
            task.setTag(newTags);
            task.setDescription(newDescription);
            task.setNote(newNotes);

            task.setPriority(selectedPriority);

            updateTaskInDatabase();
        } else {
            Toast.makeText(this, "Por favor rellena el nombre de la tarea", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteTask() {
        if (documentId == null || documentId.isEmpty()) {
            Toast.makeText(this, "Error: ID invalido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("task").document(documentId);

        docRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Tarea eliminada correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al eliminar la tarea", Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePriorityButtons() {
        lowPriorityButton.setBackgroundColor(selectedPriority.equals("Baja") ? Color.parseColor("#FF6666") : Color.TRANSPARENT);
        mediumPriorityButton.setBackgroundColor(selectedPriority.equals("Media") ? Color.parseColor("#FF6666") : Color.TRANSPARENT);
        highPriorityButton.setBackgroundColor(selectedPriority.equals("Alta") ? Color.parseColor("#FF6666") : Color.TRANSPARENT);
    }

    private void setPriority(String priority) {
        selectedPriority = priority;
        updatePriorityButtons();
    }
}
