package com.example.pomodoro.Pomodoro;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.pomodoro.Options.OptionFragment;
import com.example.pomodoro.R;
import com.example.pomodoro.Task.TaskSide.TasksActivity;


public class FirstAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_app);

        Spinner spinner = findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOption = parentView.getItemAtPosition(position).toString();
                showFragmentBasedOnOption(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void showFragmentBasedOnOption(String selectedOption) {
        Fragment fragment;

        switch (selectedOption) {
            case "Pomodoro":
                fragment = new PomodoroFragment();
                break;
            case "Tareas":
                fragment = new TasksActivity();
                break;
            case "Ajustes":
                fragment = new OptionFragment();
                break;
            default:
                fragment = new PomodoroFragment();
                break;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
