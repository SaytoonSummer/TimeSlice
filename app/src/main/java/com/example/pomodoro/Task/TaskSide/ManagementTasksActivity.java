package com.example.pomodoro.Task.TaskSide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Button;

import com.example.pomodoro.R;

public class ManagementTasksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_tasks);

        loadFragment(new TareasFragment());

        Button buttonTareas = findViewById(R.id.button8);
        Button buttonTareasCompletadas = findViewById(R.id.button9);

        buttonTareas.setOnClickListener(v -> loadFragment(new TareasFragment()));

        buttonTareasCompletadas.setOnClickListener(v -> loadFragment(new TareasCompletadasFragment()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, fragment);
        transaction.commit();
    }

}