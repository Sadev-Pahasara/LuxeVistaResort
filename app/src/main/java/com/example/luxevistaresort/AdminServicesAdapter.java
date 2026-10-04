package com.example.luxevistaresort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminServicesAdapter extends RecyclerView.Adapter<AdminServicesAdapter.VH> {

    public interface Listener {
        void onServiceClicked(AdminServiceItem item);
        void onServiceLongPressed(AdminServiceItem item);
    }

    private final List<AdminServiceItem> items = new ArrayList<>();
    private final Listener listener;

    public AdminServicesAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<AdminServiceItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_service, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        AdminServiceItem it = items.get(position);

        h.icon.setImageResource(it.iconRes);
        h.title.setText(it.title);
        h.desc.setText(it.desc);
        h.price.setText(String.format(Locale.US, "LKR %.0f", it.price));

        // show status in title (simple + clear)
        String s = it.status == null ? "ACTIVE" : it.status.toUpperCase(Locale.US);
        h.title.setText(it.title + " (" + s + ")");

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onServiceClicked(it);
        });

        h.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onServiceLongPressed(it);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title, desc, price;

        VH(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.imgServiceIcon);
            title = itemView.findViewById(R.id.tvServiceTitle);
            desc = itemView.findViewById(R.id.tvServiceDesc);
            price = itemView.findViewById(R.id.tvServicePrice);
        }
    }
}