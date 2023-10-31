package com.example.pomodoro.Task.ListSide;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pomodoro.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagementListActivity extends AppCompatActivity {

    private EditText editTextListName;
    private Button createListButton;
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private List<ListModel> listModels;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_list);

        db = FirebaseFirestore.getInstance();

        editTextListName = findViewById(R.id.editTextText4);
        createListButton = findViewById(R.id.button3);
        recyclerView = findViewById(R.id.recyclerView4);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        listModels = new ArrayList<>();
        listAdapter = new ListAdapter(listModels, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);

        collectionReference = db.collection("collections");

        loadCollectionsFromDatabase();

        createListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createList();
            }
        });
    }

    private void createList() {
        String listName = editTextListName.getText().toString().trim();

        if (!listName.isEmpty()) {
            Map<String, Object> listData = new HashMap<>();
            listData.put("listName", listName);

            db.collection("lists")
                    .add(listData)
                    .addOnSuccessListener(documentReference -> {
                        db.collection("lists").document(documentReference.getId())
                                .update("documentId", documentReference.getId())
                                .addOnSuccessListener(aVoid -> Log.d("CreateList", "ID Cargado"))
                                .addOnFailureListener(e -> Log.e("CreateList", "Error al cargar la ID" + e.getMessage()));

                        listModels.add(new ListModel(listName, documentReference.getId()));
                        listAdapter.notifyDataSetChanged();

                        editTextListName.getText().clear();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ManagementListActivity.this, "Error creando el documento", Toast.LENGTH_SHORT).show();

                        Log.e("CreateList", "Error al crear el documento: " + e.getMessage());
                    });
        }
    }


    private void createSubcollection(String documentId, String subcollectionName) {
        CollectionReference subcollectionRef = db.collection("lists").document(documentId).collection(subcollectionName);

        subcollectionRef.limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                Log.d("CreateSubcollection", "Subcolección ya contiene documentos: " + subcollectionName);
            } else {
                subcollectionRef.add(new HashMap<>())
                        .addOnSuccessListener(documentReference -> {
                            Log.d("CreateSubcollection", "Subcolección creada: " + subcollectionName);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("CreateSubcollection", "Error al crear la subcolección: " + e.getMessage());
                        });
            }
        });
    }


    private void loadCollectionsFromDatabase() {
        listModels.clear();

        db.collection("lists").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ListModel list = document.toObject(ListModel.class);
                        listModels.add(list);

                        Log.d("LoadLists", "Lista cargada: " + list.getListName());
                    }

                    listAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    Log.d("LoadLists", "Datos de listas cargados exitosamente. Cantidad de elementos: " + listModels.size());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ManagementListActivity.this, "Error cargando listas", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                    Log.e("LoadLists", "Error al cargar datos de listas: " + e.getMessage());
                });
    }
}
