package com.example.pomodoro.Pomodoro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pomodoro.Pomodoro.CreatePomodoroActivity;
import com.example.pomodoro.Pomodoro.EditPomodoroActivity;
import com.example.pomodoro.Pomodoro.PomodoroModel;
import com.example.pomodoro.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;

public class ManagementPomodoroActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PomodoroAdapter pomodoroAdapter;
    private List<PomodoroModel> pomodoroList;

    private CollectionReference pomodoroCollection;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_pomodoro);

        recyclerView = findViewById(R.id.recyclerView);

        progressBar = findViewById(R.id.progressBar);

        Button pomodoroButton = findViewById(R.id.pomodoro2);
        pomodoroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pomodoroAdapter.openCreatePomodoroActivity();
            }
        });

        pomodoroList = new ArrayList<>();
        pomodoroAdapter = new PomodoroAdapter(pomodoroList, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(pomodoroAdapter);

        loadPomodorosFromDatabase();
    }

    private void loadPomodorosFromDatabase() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        pomodoroCollection = FirebaseFirestore.getInstance().collection("pomodoro");

        pomodoroCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(this, "Error al cargar los Pomodoros", Toast.LENGTH_SHORT).show();

                Log.e("LoadData", "Error al cargar los datos " + e.getMessage());

                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                return;
            }

            List<PomodoroModel> previousList = new ArrayList<>(pomodoroList);

            pomodoroList.clear();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                PomodoroModel pomodoro = document.toObject(PomodoroModel.class);

                pomodoroList.add(pomodoro);
            }

            if (!pomodoroList.equals(previousList)) {
                pomodoroAdapter.notifyDataSetChanged();
            }

            Log.d("LoadData", "Data cargado " + pomodoroList.size());

            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });
    }


    public class PomodoroAdapter extends RecyclerView.Adapter<PomodoroAdapter.ViewHolder> {

        private final List<PomodoroModel> pomodoroList;
        private final Context context;

        public PomodoroAdapter(List<PomodoroModel> pomodoroList, Context context) {
            this.pomodoroList = pomodoroList;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pomodoro, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PomodoroModel pomodoro = pomodoroList.get(position);
            holder.bind(pomodoro);
        }

        @Override
        public int getItemCount() {
            return pomodoroList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final Button editarButton;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                editarButton = itemView.findViewById(R.id.editar);
            }

            public void bind(PomodoroModel pomodoro) {
                editarButton.setText(pomodoro.getName());

                editarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int adapterPosition = getAdapterPosition();
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            openEditPomodoroActivity(view, pomodoroList.get(adapterPosition));
                        }
                    }
                });
            }
        }

        private void openCreatePomodoroActivity() {
            Intent intent = new Intent(context, CreatePomodoroActivity.class);
            context.startActivity(intent);
        }

        private void openEditPomodoroActivity(View view, PomodoroModel pomodoro) {
            Intent intent = new Intent(context, EditPomodoroActivity.class);
            intent.putExtra("PomodoroModel", pomodoro);
            intent.putExtra("DocumentId", pomodoro.getDocumentId());
            context.startActivity(intent);
        }
    }
}