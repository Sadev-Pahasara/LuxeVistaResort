package com.example.luxevistaresort;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeEventAdapter extends RecyclerView.Adapter<HomeEventAdapter.VH> {

    public interface OnEventClickListener {
        void onEventClick(EventItem item);
    }

    private final Context context;
    private final List<EventItem> list;
    private final OnEventClickListener listener;

    public HomeEventAdapter(Context context, List<EventItem> list, OnEventClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_home_event, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        EventItem item = list.get(position);

        if (item.imageUri != null && !item.imageUri.trim().isEmpty()) {
            try {
                h.imgEvent.setImageURI(Uri.parse(item.imageUri));
            } catch (Exception e) {
                h.imgEvent.setImageResource(R.drawable.logo_black);
            }
        } else {
            h.imgEvent.setImageResource(R.drawable.logo_black);
        }

        h.itemView.setOnClickListener(v -> listener.onEventClick(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgEvent;

        public VH(@NonNull View itemView) {
            super(itemView);
            imgEvent = itemView.findViewById(R.id.imgHomeEvent);
        }
    }
}