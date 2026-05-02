package com.safaemirr.kampusbildirimleri;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EmergencyAdapter
        extends RecyclerView.Adapter<EmergencyAdapter.ViewHolder> {

    Context context;
    ArrayList<Emergency> list;

    public EmergencyAdapter(Context context, ArrayList<Emergency> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_emergency, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Emergency ann = list.get(position);

        holder.txtTitle.setText(ann.getTitle());
        holder.txtDescription.setText(ann.getDescription());
        holder.txtDate.setText(ann.getDate());

        // 👉 ACİL DURUM DETAYINA GİT
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(
                    context,
                    EmergencyDetailActivity.class
            );
            i.putExtra("id", ann.getId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtDescription, txtDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle       = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtDate        = itemView.findViewById(R.id.txtDate);
        }
    }
}
