package com.example.luxevistaresort;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminBookingsAdapter extends RecyclerView.Adapter<AdminBookingsAdapter.VH> {

    public static final int MODE_ACCEPTED = 1;
    public static final int MODE_PENDING = 2;

    public interface BookingActionListener {
        void onInfo(AdminBookingItem item);
        void onAccept(AdminBookingItem item);
        void onReject(AdminBookingItem item);
    }

    private final Context context;
    private final List<AdminBookingItem> items = new ArrayList<>();
    private final int mode;
    private final BookingActionListener listener;

    public AdminBookingsAdapter(Context context,
                                List<AdminBookingItem> initial,
                                int mode,
                                BookingActionListener listener) {
        this.context = context;
        this.mode = mode;
        this.listener = listener;
        if (initial != null) items.addAll(initial);
    }

    public void setItems(List<AdminBookingItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_booking, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        AdminBookingItem it = items.get(position);

        h.tvGuestName.setText(it.guestName);
        h.tvRoomType.setText(it.roomTitle);

        String dateText = it.checkIn;
        if (it.checkOut != null && !it.checkOut.trim().isEmpty()) {
            dateText = it.checkIn + " → " + it.checkOut;
        }
        h.tvDate.setText(dateText);

        h.tvStatus.setText(it.status != null ? it.status.toUpperCase(Locale.US) : "-");

        if ("CONFIRMED".equalsIgnoreCase(it.status)) {
            h.tvStatus.setTextColor(0xFF15803D);
        } else if ("PENDING".equalsIgnoreCase(it.status)) {
            h.tvStatus.setTextColor(0xFFD97706);
        } else if ("REJECTED".equalsIgnoreCase(it.status)) {
            h.tvStatus.setTextColor(0xFFB91C1C);
        } else {
            h.tvStatus.setTextColor(0xFF334155);
        }

        if (mode == MODE_ACCEPTED) {
            h.btnInfo.setVisibility(View.VISIBLE);
            h.layoutPendingButtons.setVisibility(View.GONE);

            h.btnInfo.setOnClickListener(v -> listener.onInfo(it));
        } else {
            h.btnInfo.setVisibility(View.GONE);
            h.layoutPendingButtons.setVisibility(View.VISIBLE);

            h.btnAccept.setOnClickListener(v -> listener.onAccept(it));
            h.btnReject.setOnClickListener(v -> listener.onReject(it));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvGuestName, tvRoomType, tvDate, tvStatus;
        MaterialButton btnInfo, btnAccept, btnReject;
        View layoutPendingButtons;

        VH(@NonNull View itemView) {
            super(itemView);
            tvGuestName = itemView.findViewById(R.id.tvGuestName);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnInfo = itemView.findViewById(R.id.btnInfo);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
            layoutPendingButtons = itemView.findViewById(R.id.layoutPendingButtons);
        }
    }

    public static class BookingActions {
        public static void openBookingDetails(Context c, AdminBookingItem it) {
            Intent i = new Intent(c, BookingDetailsActivity.class);
            i.putExtra("booking_id", it.bookingId);
            i.putExtra("guest_name", it.guestName);
            i.putExtra("room_type", it.roomTitle);
            i.putExtra("status", it.status);
            i.putExtra("check_in", it.checkIn);
            i.putExtra("check_out", it.checkOut);
            i.putExtra("total_price", it.totalPrice);
            c.startActivity(i);
        }
    }
}