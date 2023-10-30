package com.example.pomodoro.Options;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.pomodoro.R;

public class OptionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_option, container, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean soundEnabled = preferences.getBoolean("sound_preference", true);

        Button changeThemeButton = view.findViewById(R.id.button10);
        Button disableSoundButton = view.findViewById(R.id.button11);

        changeThemeButton.setOnClickListener(v -> changeTheme());

        disableSoundButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("sound_preference", !soundEnabled);
            editor.apply();

            String message = soundEnabled ? "Sonidos desactivados" : "Sonidos activados";
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void changeTheme() {
    }
}