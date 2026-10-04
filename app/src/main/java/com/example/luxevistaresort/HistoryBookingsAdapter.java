package com.example.luxevistaresort;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Locale;

public class HistoryBookingsAdapter extends RecyclerView.Adapter<HistoryBookingsAdapter.VH> {

    public interface OnChangedListener {
        void onChanged();
    }

    public static class BookingRow {
        public long bookingId;
        public String roomTitle;
        public String checkIn;
        public String checkOut;
        public int nights;
        public String status;
        public double total;
        public String addonsText;

        public BookingRow(long bookingId, String roomTitle, String checkIn, String checkOut,
                          int nights, String status, double total, String addonsText) {
            this.bookingId = bookingId;
            this.roomTitle = roomTitle;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.nights = nights;
            this.status = status;
            this.total = total;
            this.addonsText = addonsText;
        }
    }

    private final ArrayList<BookingRow> data;
    private final DBHelper db;
    private final String username;
    private final OnChangedListener listener;

    public HistoryBookingsAdapter(Context ctx,
                                  ArrayList<BookingRow> data,
                                  DBHelper db,
                                  String username,
                                  OnChangedListener listener) {
        this.data = (data != null) ? data : new ArrayList<>();
        this.db = db;
        this.username = username;
        this.listener = listener;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvInfo;
        MaterialButton btnCancel;

        VH(@NonNull View itemView) {
            super(itemView);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            btnCancel = itemView.findViewById(R.id.btnCancelBooking);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_history, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        BookingRow row = data.get(position);

        String text =
                "Room: " + row.roomTitle +
                        "\nDates: " + row.checkIn + " → " + row.checkOut +
                        " (" + row.nights + " nights)" +
                        "\nStatus: " + row.status +
                        "\nTotal: $" + String.format(Locale.US, "%.2f", row.total) +
                        "\n" + row.addonsText;

        holder.tvInfo.setText(text);

        boolean canCancel =
                "PENDING".equalsIgnoreCase(row.status) ||
                        "CONFIRMED".equalsIgnoreCase(row.status);

        holder.btnCancel.setVisibility(canCancel ? View.VISIBLE : View.GONE);

        holder.btnCancel.setOnClickListener(v -> {
            Context ctx = v.getContext();

            new AlertDialog.Builder(ctx)
                    .setTitle("Cancel booking?")
                    .setMessage("Do you want to cancel this booking?\n\nBooking ID: " + row.bookingId)
                    .setPositiveButton("Cancel Booking", (d, which) -> {
                        boolean ok = db.cancelBooking(row.bookingId, username);
                        if (ok) {
                            Toast.makeText(ctx, "Booking cancelled", Toast.LENGTH_SHORT).show();
                            if (listener != null) listener.onChanged();
                        } else {
                            Toast.makeText(ctx, "Cannot cancel this booking", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Keep", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}