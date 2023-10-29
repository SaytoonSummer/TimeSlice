package com.example.pomodoro.Pomodoro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pomodoro.R;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreatePomodoroActivity extends AppCompatActivity {

    private EditText nameEditText;
    private Slider focusSlider, breakSlider, longBreakSlider, roundsSlider;
    private Button createPomodoroButton;
    private CollectionReference pomodoroCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pomodoro);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        pomodoroCollection = firestore.collection("pomodoro");

        nameEditText = findViewById(R.id.editTextText2);
        focusSlider = findViewById(R.id.slider5);
        breakSlider = findViewById(R.id.slider3);
        longBreakSlider = findViewById(R.id.slider4);
        roundsSlider = findViewById(R.id.slider2);
        createPomodoroButton = findViewById(R.id.createPomodoro);

        createPomodoroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePomodoroToFirestore();
            }
        });

        focusSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                ((TextView) findViewById(R.id.focustext)).setText(String.valueOf((int) value));
            }
        });

        breakSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                ((TextView) findViewById(R.id.tiempofocus5)).setText(String.valueOf((int) value));
            }
        });

        longBreakSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                ((TextView) findViewById(R.id.tiempofocustext)).setText(String.valueOf((int) value));
            }
        });

        roundsSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                ((TextView) findViewById(R.id.tiempofocus)).setText(String.valueOf((int) value));
            }
        });

        ((TextView) findViewById(R.id.focustext)).setText(String.valueOf((int) focusSlider.getValue()));
        ((TextView) findViewById(R.id.tiempofocus5)).setText(String.valueOf((int) breakSlider.getValue()));
        ((TextView) findViewById(R.id.tiempofocustext)).setText(String.valueOf((int) longBreakSlider.getValue()));
        ((TextView) findViewById(R.id.tiempofocus)).setText(String.valueOf((int) roundsSlider.getValue()));
    }


    private void savePomodoroToFirestore() {
        String name = nameEditText.getText().toString().trim();
        float focus = focusSlider.getValue();
        float breakTime = breakSlider.getValue();
        float longBreak = longBreakSlider.getValue();
        float rounds = roundsSlider.getValue();

        if (!name.isEmpty()) {
            PomodoroModel pomodoro = new PomodoroModel(name, (int) focus, (int) breakTime, (int) longBreak, (int) rounds, "");


            pomodoroCollection.add(pomodoro)
                    .addOnSuccessListener(documentReference -> {
                        pomodoro.setDocumentId(documentReference.getId());
                        documentReference.set(pomodoro)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(CreatePomodoroActivity.this, "Pomodoro guardado exitosamente", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(CreatePomodoroActivity.this, "Error al actualizar el Pomodoro con la ID", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CreatePomodoroActivity.this, "Error al guardar el Pomodoro", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Por favor, completa el nombre del pomodoro", Toast.LENGTH_SHORT).show();
        }
    }
}