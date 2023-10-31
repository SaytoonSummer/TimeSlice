package com.example.pomodoro.Pomodoro;

import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pomodoro.R;
import com.example.pomodoro.Task.TaskSide.TasksActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class PomodoroFragment extends Fragment {
    private Button pomodoro;
    private TextView mTextViewCountDown;
    private ImageView mButtonStartPause;
    private ImageView mButtonReset;

    private MediaPlayer mediaPlayer;
    private AppCompatButton mButtonSkip;
    private ProgressBar progressBar;

    private CountDownTimer mCountDownTimer;

    private TextView mTextViewFocusTime;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;

    private boolean isFocusPhase = true;

    private PomodoroModel selectedPomodoro;
    private boolean pomodoroCompleted = false;

    private boolean isSoundPlaying = false;
    private int currentRound = 0;

    private boolean soundEnabled;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pomodoro, container, false);

        pomodoro = view.findViewById(R.id.pomodoro);
        pomodoro.setOnClickListener(v -> openCreatePomodoroActivity());
        mTextViewFocusTime = view.findViewById(R.id.textView);

        mTextViewCountDown = view.findViewById(R.id.textViewCountDown2);
        mButtonStartPause = view.findViewById(R.id.play);
        mButtonReset = view.findViewById(R.id.reset);
        mButtonSkip = view.findViewById(R.id.skip2);
        progressBar = view.findViewById(R.id.progress_bar);

        mButtonStartPause.setOnClickListener(v -> toggleTimer());
        mButtonReset.setOnClickListener(v -> resetTimer());
        mButtonSkip.setOnClickListener(v -> skipToNextPhase());

        soundEnabled = PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("sound_preference", true);

        getDefaultPomodoroFromDatabase();

        return view;
    }

    private void startTimer(long timeInMillis) {
        if (soundEnabled) {
            playSound(R.raw.roundpass);
        }

        if (!mTimerRunning) {
            mCountDownTimer = new CountDownTimer(timeInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTimeLeftInMillis = millisUntilFinished;
                    updateCountDownText();
                    updateProgressBar();
                }

                @Override
                public void onFinish() {
                    mTimerRunning = false;
                    mButtonStartPause.setImageResource(R.drawable._75);
                    mButtonReset.setVisibility(View.VISIBLE);

                    currentRound++;

                    if (currentRound < selectedPomodoro.getRounds() * 2) {
                        handlePhaseChange();
                        startTimer(mTimeLeftInMillis);
                    } else {
                        resetPomodoro();

                        if (soundEnabled) {
                            playSound(R.raw.pomodoropass);
                        }
                    }
                }
            };

            mCountDownTimer.start();
            mTimerRunning = true;
            mButtonStartPause.setImageResource(R.drawable._1180);
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonSkip.setVisibility(View.VISIBLE);
        }
    }


    private void updateProgressBar() {
        int progress = (int) ((mTimeLeftInMillis * 100) / getPhaseTime(selectedPomodoro, currentRound));
        progressBar.setProgress(progress);
    }

    private void toggleTimer() {
        if (selectedPomodoro != null) {
            if (mTimerRunning) {
                pauseTimer();
            } else {
                if (currentRound >= selectedPomodoro.getRounds() * 2) {
                    resetPomodoro();
                } else {
                    if (!mTimerRunning) {
                        if (mTimeLeftInMillis > 0) {
                            resumeTimer();
                        } else {
                            mTimeLeftInMillis = getPhaseTime(selectedPomodoro, currentRound);
                            startTimer(mTimeLeftInMillis);
                            updateSkipButtonVisibility();
                        }
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "No se ha seleccionado un Pomodoro", Toast.LENGTH_SHORT).show();
        }
    }


    private long getPhaseTime(PomodoroModel pomodoro, int round) {
        if (round % 2 == 0) {
            return pomodoro.getFocus() * 60 * 1000;
        } else {
            return round < pomodoro.getRounds() * 2 - 1 ? pomodoro.getBreakTime() * 60 * 1000 : pomodoro.getLongBreak() * 60 * 1000;
        }
    }


    private void resetPomodoro() {
        currentRound = 0;
        pomodoroCompleted = true;
        mButtonSkip.setVisibility(View.INVISIBLE);
        mTimeLeftInMillis = 0;
        updateCountDownText();
        mButtonSkip.setVisibility(View.INVISIBLE);

        isFocusPhase = true;
        selectedPomodoro = null;
        mTimerRunning = false;

        updateFocusTimeTextView();
        updateSkipButtonVisibility();
        mButtonStartPause.setImageResource(R.drawable._75);
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonSkip.setVisibility(View.INVISIBLE);

        getDefaultPomodoroFromDatabase();
    }

    private void updateFocusTimeTextView() {
        if (isFocusPhase) {
            mTextViewFocusTime.setText("Tiempo Focus");
        } else {
            if (currentRound % 2 == 0 && currentRound < selectedPomodoro.getRounds() * 2 - 1) {
                mTextViewFocusTime.setText("Descanso Largo");
            } else {
                if (currentRound < selectedPomodoro.getRounds() * 2 - 2) {
                    mTextViewFocusTime.setText("Descanso");
                } else {
                    mTextViewFocusTime.setText("Descanso Largo");
                }
            }
        }
    }

    private void pauseTimer() {
        if (mCountDownTimer != null && mTimerRunning) {
            mCountDownTimer.cancel();
            mTimerRunning = false;
            mButtonStartPause.setImageResource(R.drawable._75);
            mButtonReset.setVisibility(View.VISIBLE);
            mTimeLeftInMillis = mTimeLeftInMillis;
        }
    }

    private void resumeTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        startTimer(mTimeLeftInMillis);
    }

    private void resetTimer() {
        if (mTimerRunning) {
            pauseTimer();
        }

        // Reiniciar todas las variables y vistas relacionadas con el temporizador
        mTimeLeftInMillis = getPhaseTime(selectedPomodoro, 0);
        currentRound = 0;
        isFocusPhase = true;
        mTimerRunning = false;
        updateCountDownText();
        updateFocusTimeTextView();

        // Ocultar o restablecer cualquier otra vista según sea necesario
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setImageResource(R.drawable._75);
        mButtonSkip.setVisibility(View.INVISIBLE);

        // Reiniciar el Pomodoro si es necesario
        if (pomodoroCompleted) {
            resetPomodoro();
            pomodoroCompleted = false;
        }
    }

    private void skipToNextPhase() {
        pauseTimer();

        currentRound++;

        if (selectedPomodoro != null) {
            if (currentRound < selectedPomodoro.getRounds() * 2) {
                isFocusPhase = currentRound % 2 == 0;
                mTimeLeftInMillis = getPhaseTime(selectedPomodoro, currentRound);
            } else {
                resetPomodoro();
                Toast.makeText(requireContext(), "Pomodoro completado", Toast.LENGTH_SHORT).show();
                return;
            }

            updateCountDownText();
            updateFocusTimeTextView();
            updateSkipButtonVisibility();
        }

        if (!mTimerRunning) {
            startTimer(mTimeLeftInMillis);
        }
    }

    private void updateSkipButtonVisibility() {
        if (selectedPomodoro != null) {
            mButtonSkip.setVisibility(!mTimerRunning && currentRound < selectedPomodoro.getRounds() ? View.VISIBLE : View.INVISIBLE);
        } else {
            mButtonSkip.setVisibility(View.INVISIBLE);
        }
    }

    private void getDefaultPomodoroFromDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("pomodoro").document("QFp3zL6J1itFKsVz9Ty9")
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(requireContext(), "Error al obtener el Pomodoro predeterminado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        PomodoroModel fetchedPomodoro = snapshot.toObject(PomodoroModel.class);

                        Log.d("PomodoroInfo", "Pomodoro obtenido: " + fetchedPomodoro);

                        if (fetchedPomodoro != null) {
                            selectedPomodoro = fetchedPomodoro;

                            PomodoroModel selectedPomodoroExtra = getIntent().getParcelableExtra("SelectedPomodoro");
                            if (selectedPomodoroExtra != null) {
                                this.selectedPomodoro = selectedPomodoroExtra;
                            }

                            if (this.selectedPomodoro != null) {
                                mTimeLeftInMillis = isFocusPhase ?
                                        this.selectedPomodoro.getFocus() * 60 * 1000 :
                                        this.selectedPomodoro.getBreakTime() * 60 * 1000;

                                updateCountDownText();
                                pomodoro.setText(this.selectedPomodoro.getName());
                            } else {
                                Toast.makeText(requireContext(), "El Pomodoro seleccionado es nulo", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireContext(), "El Pomodoro predeterminado es nulo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private Intent getIntent() {
        Intent intent = requireActivity().getIntent();

        PomodoroModel selectedPomodoroExtra = intent.getParcelableExtra("SelectedPomodoro");
        if (selectedPomodoroExtra != null) {
            this.selectedPomodoro = selectedPomodoroExtra;
        }
        return intent;
    }

    private void handlePhaseChange() {
        if (selectedPomodoro != null) {
            if (currentRound % 2 == 0) {
                mTimeLeftInMillis = selectedPomodoro.getFocus() * 60 * 1000;
                isFocusPhase = true;
                int color = ContextCompat.getColor(requireContext(), R.color.colorRed);
                progressBar.setProgressTintList(ColorStateList.valueOf(color));
                Drawable progressDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.circleprogress);
                progressBar.setProgressDrawable(progressDrawable);

                if (soundEnabled) {
                    playSound(R.raw.roundpass);
                }
            } else {
                if (currentRound < selectedPomodoro.getRounds() * 2 - 1) {
                    mTimeLeftInMillis = selectedPomodoro.getBreakTime() * 60 * 1000;
                    isFocusPhase = false;
                    int color = ContextCompat.getColor(requireContext(), R.color.colorGreen);
                    progressBar.setProgressTintList(ColorStateList.valueOf(color));
                    Drawable progressDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.circleprogress);
                    progressBar.setProgressDrawable(progressDrawable);

                    if (soundEnabled) {
                        playSound(R.raw.roundpass);
                    }
                } else {
                    mTimeLeftInMillis = selectedPomodoro.getLongBreak() * 60 * 1000;
                    isFocusPhase = false;
                    int color = ContextCompat.getColor(requireContext(), R.color.colorBlue);
                    progressBar.setProgressTintList(ColorStateList.valueOf(color));
                    Drawable progressDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.circleprogress);
                    progressBar.setProgressDrawable(progressDrawable);

                    if (soundEnabled) {
                        playSound(R.raw.roundpass);
                    }
                }
            }
        }
    }


    private void openCreatePomodoroActivity() {
        Intent intent = new Intent(requireContext(), ManagementPomodoroActivity.class);
        startActivityForResult(intent, 1);
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);

        if (isFocusPhase) {
            mTextViewFocusTime.setText("Tiempo Focus");
            int color = ContextCompat.getColor(requireContext(), R.color.colorRed);
            progressBar.setProgressTintList(ColorStateList.valueOf(color));
            Drawable progressDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.circleprogress);
            progressBar.setProgressDrawable(progressDrawable);
        } else {
            if (currentRound % 2 == 0 && currentRound < selectedPomodoro.getRounds() * 2 - 1) {
                mTextViewFocusTime.setText("Descanso Largo");
                int color = ContextCompat.getColor(requireContext(), R.color.colorBlue);
                progressBar.setProgressTintList(ColorStateList.valueOf(color));
                Drawable progressDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.circleprogress);
                progressBar.setProgressDrawable(progressDrawable);
            } else {
                if (currentRound < selectedPomodoro.getRounds() * 2 - 2) {
                    mTextViewFocusTime.setText("Descanso");
                    int color = ContextCompat.getColor(requireContext(), R.color.colorGreen);
                    progressBar.setProgressTintList(ColorStateList.valueOf(color));
                    Drawable progressDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.circleprogress);
                    progressBar.setProgressDrawable(progressDrawable);
                } else {
                    mTextViewFocusTime.setText("Descanso Largo");
                    int color = ContextCompat.getColor(requireContext(), R.color.colorBlue);
                    progressBar.setProgressTintList(ColorStateList.valueOf(color));
                    Drawable progressDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.circleprogress);
                    progressBar.setProgressDrawable(progressDrawable);
                }
            }
        }

        updateFocusTimeTextView();
    }

    private void playSound(int soundResource) {
        if (!isSoundPlaying) {
            isSoundPlaying = true;

            mediaPlayer = MediaPlayer.create(requireContext(), soundResource);
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    isSoundPlaying = false;
                }
            });
        }
    }
}