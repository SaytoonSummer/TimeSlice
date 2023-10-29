package com.example.pomodoro.Pomodoro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pomodoro.R;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.slider.Slider;

public class EditPomodoroActivity extends AppCompatActivity {

    private PomodoroModel pomodoro;
    private String documentId;

    private EditText nameEditText;
    private Slider focusSlider, breakSlider, longBreakSlider, roundsSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pomodoro);

        pomodoro = getIntent().getParcelableExtra("PomodoroModel");
        documentId = getIntent().getStringExtra("DocumentId");

        nameEditText = findViewById(R.id.editTextText2);
        focusSlider = findViewById(R.id.slider5);
        breakSlider = findViewById(R.id.slider2);
        longBreakSlider = findViewById(R.id.slider4);
        roundsSlider = findViewById(R.id.slider3);

        if (pomodoro != null) {
            nameEditText.setText(pomodoro.getName());
            focusSlider.setValue((float) pomodoro.getFocus());
            breakSlider.setValue((float) pomodoro.getBreakTime());
            longBreakSlider.setValue((float) pomodoro.getLongBreak());
            roundsSlider.setValue((float) pomodoro.getRounds());
        }

        focusSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                ((TextView) findViewById(R.id.tiempofocus)).setText(String.valueOf((int) value));
            }
        });

        breakSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                ((TextView) findViewById(R.id.tiempodescanso)).setText(String.valueOf((int) value));
            }
        });

        longBreakSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                ((TextView) findViewById(R.id.tiempodescansolargo)).setText(String.valueOf((int) value));
            }
        });

        roundsSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                ((TextView) findViewById(R.id.rondasrecuperadas)).setText(String.valueOf((int) value));
            }
        });

        ((TextView) findViewById(R.id.tiempofocus)).setText(String.valueOf((int) focusSlider.getValue()));
        ((TextView) findViewById(R.id.tiempodescanso)).setText(String.valueOf((int) breakSlider.getValue()));
        ((TextView) findViewById(R.id.tiempodescansolargo)).setText(String.valueOf((int) longBreakSlider.getValue()));
        ((TextView) findViewById(R.id.rondasrecuperadas)).setText(String.valueOf((int) roundsSlider.getValue()));
    }

    public void onAssignPomodoroClick(View view) {
        assignPomodoro();
    }

    public void onEditPomodoroClick(View view) {
        editPomodoro();
    }

    public void onDeletePomodoroClick(View view) {
        deletePomodoro();
    }

    private void updatePomodoroInDatabase() {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("pomodoro").document(documentId);

        docRef.set(pomodoro)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Pomodoro Actualizado Correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al Actualizar el Pomodoro", Toast.LENGTH_SHORT).show();
                });
    }

    private void assignPomodoro() {
        Intent intent = new Intent(EditPomodoroActivity.this, FirstAppActivity.class);
        intent.putExtra("SelectedPomodoro", pomodoro);
        startActivity(intent);
        finish();
    }

    private void editPomodoro() {
        String newName = nameEditText.getText().toString().trim();
        int newFocus = (int) focusSlider.getValue();
        int newBreakTime = (int) breakSlider.getValue();
        int newLongBreak = (int) longBreakSlider.getValue();
        int newRounds = (int) roundsSlider.getValue();

        if (!newName.isEmpty()) {
            pomodoro.setName(newName);
            pomodoro.setFocus(newFocus);
            pomodoro.setBreakTime(newBreakTime);
            pomodoro.setLongBreak(newLongBreak);
            pomodoro.setRounds(newRounds);

            updatePomodoroInDatabase();
        } else {
            Toast.makeText(this, "Por favor, rellena el Pomodoro", Toast.LENGTH_SHORT).show();
        }
    }

    private void deletePomodoro() {
        if (documentId == null || documentId.isEmpty()) {
            Toast.makeText(this, "Error: ID del documento no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("pomodoro").document(documentId);

        docRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Pomodoro eliminado exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al eliminar el Pomodoro", Toast.LENGTH_SHORT).show();
                    Log.e("DeletePomodoro", "Error al eliminar el Pomodoro", e);
                });
    }
}
