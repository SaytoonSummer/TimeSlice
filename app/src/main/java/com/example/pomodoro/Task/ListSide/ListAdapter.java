package com.example.pomodoro.Task.ListSide;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pomodoro.R;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<ListModel> listModels;
    private Context context;

    public ListAdapter(List<ListModel> listModels, Context context) {
        this.listModels = listModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListModel listModel = listModels.get(position);
        holder.bind(listModel);
    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatButton listButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listButton = itemView.findViewById(R.id.lista);

            listButton.setOnClickListener(v -> openTasksListActivity(listModels.get(getAdapterPosition())));
        }

        public void bind(ListModel listModel) {
            listButton.setText(listModel.getListName());
        }

        private void openTasksListActivity(ListModel listModel) {
            Intent intent = new Intent(context, TasksListActivity.class);
            intent.putExtra("listId", listModel.getDocumentId());
            context.startActivity(intent);
        }
    }
}