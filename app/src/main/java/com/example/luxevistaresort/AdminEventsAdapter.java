package com.example.luxevistaresort;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AdminEventsAdapter extends RecyclerView.Adapter<AdminEventsAdapter.VH> {

    public interface EventActionListener {
        void onEditEvent(EventItem item);
        void onToggleEvent(EventItem item);
        void onDeleteEvent(EventItem item);
    }

    private final Context context;
    private final List<EventItem> list;
    private final EventActionListener listener;

    public AdminEventsAdapter(Context context, List<EventItem> list, EventActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_admin_event, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        EventItem item = list.get(position);

        h.tvTitle.setText(item.title);
        h.tvDescription.setText(item.description);
        h.tvDate.setText("Date: " + item.eventDate);
        h.tvTime.setText("Time: " + item.eventTime);
        h.tvLocation.setText("Location: " + item.location);
        h.tvStatus.setText(item.status);

        if ("ACTIVE".equalsIgnoreCase(item.status)) {
            h.tvStatus.setTextColor(0xFF16A34A);
        } else {
            h.tvStatus.setTextColor(0xFFD97706);
        }

        if (item.imageUri != null && !item.imageUri.trim().isEmpty()) {
            try {
                h.ivEventImage.setImageURI(Uri.parse(item.imageUri));
            } catch (Exception e) {
                h.ivEventImage.setImageResource(R.drawable.logo_black);
            }
        } else {
            h.ivEventImage.setImageResource(R.drawable.logo_black);
        }

        h.btnEdit.setOnClickListener(v -> listener.onEditEvent(item));
        h.btnToggle.setOnClickListener(v -> listener.onToggleEvent(item));
        h.btnDelete.setOnClickListener(v -> listener.onDeleteEvent(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivEventImage;
        TextView tvTitle, tvDescription, tvDate, tvTime, tvLocation, tvStatus;
        MaterialButton btnEdit, btnToggle, btnDelete;

        public VH(@NonNull View itemView) {
            super(itemView);

            ivEventImage = itemView.findViewById(R.id.ivEventImage);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDescription = itemView.findViewById(R.id.tvEventDescription);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            tvTime = itemView.findViewById(R.id.tvEventTime);
            tvLocation = itemView.findViewById(R.id.tvEventLocation);
            tvStatus = itemView.findViewById(R.id.tvEventStatus);

            btnEdit = itemView.findViewById(R.id.btnEditEvent);
            btnToggle = itemView.findViewById(R.id.btnToggleEvent);
            btnDelete = itemView.findViewById(R.id.btnDeleteEvent);
        }
    }
}