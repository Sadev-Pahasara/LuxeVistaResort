package com.example.luxevistaresort;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HistoryFragment extends Fragment {

    private static final SimpleDateFormat DF =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private RecyclerView rvHistory;
    private LinearLayout containerActive;
    private TextView tvBill;

    private DBHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_history, container, false);

        db = new DBHelper(requireContext());

        rvHistory = v.findViewById(R.id.rvHistory);
        containerActive = v.findViewById(R.id.containerActiveAddons);
        tvBill = v.findViewById(R.id.tvBill);

        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHistory.setNestedScrollingEnabled(false);

        loadHistory();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistory();
    }

    private void loadHistory() {

        containerActive.removeAllViews();

        // ✅ GET USERNAME FROM LOGIN SESSION
        SharedPreferences sp = requireContext().getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        String username = sp.getString("USERNAME", null);

        if (username == null || username.trim().isEmpty()) {
            username = "guest";
        }

        ArrayList<HistoryBookingsAdapter.BookingRow> rows = new ArrayList<>();
        double totalBill = 0;

        Cursor c = db.getBookingsByUsername(username);

        while (c.moveToNext()) {

            long bookingId = c.getLong(0);
            String roomTitle = c.getString(1);
            String checkIn = c.getString(2);
            String checkOut = c.getString(3);
            double total = c.getDouble(4);
            String status = c.getString(5);

            // ✅ HIDE cancelled bookings
            if ("CANCELLED".equalsIgnoreCase(status)) {
                continue;
            }

            int nights = calcNights(checkIn, checkOut);

            String addonsText = buildAddonsText(db, bookingId, nights);

            rows.add(new HistoryBookingsAdapter.BookingRow(
                    bookingId,
                    roomTitle,
                    checkIn,
                    checkOut,
                    nights,
                    status,
                    total,
                    addonsText
            ));

            // Only confirmed bookings affect billing
            if ("CONFIRMED".equalsIgnoreCase(status)) {
                totalBill += total;
                loadActiveAddons(db, bookingId, containerActive);
            }
        }

        c.close();

        if (rows.isEmpty()) {
            rows.add(new HistoryBookingsAdapter.BookingRow(
                    -1, "No bookings yet.", "", "", 0, "", 0, ""
            ));
        }

        rvHistory.setAdapter(new HistoryBookingsAdapter(
                requireContext(),
                rows,
                db,
                username,
                this::loadHistory
        ));

        tvBill.setText("$" + String.format(Locale.US, "%.2f", totalBill));

        if (containerActive.getChildCount() == 0) {
            TextView empty = new TextView(requireContext());
            empty.setText("No active add-ons.");
            containerActive.addView(empty);
        }
    }

    private void loadActiveAddons(DBHelper db, long bookingId, LinearLayout container) {

        Cursor a = db.getAddonsByBookingId(bookingId);

        while (a.moveToNext()) {

            String name = a.getString(0);
            int qty = a.getInt(2);

            TextView tv = new TextView(requireContext());
            tv.setText(name + "  x" + qty);
            tv.setBackgroundResource(R.drawable.bg_list_item);
            tv.setTextColor(0xFF0F172A);
            tv.setPadding(24, 20, 24, 20);

            container.addView(tv);
        }

        a.close();
    }

    private String buildAddonsText(DBHelper db, long bookingId, int nights) {

        Cursor a = db.getAddonsByBookingId(bookingId);

        StringBuilder sb = new StringBuilder();
        boolean has = false;

        while (a.moveToNext()) {

            has = true;

            String name = a.getString(0);
            double price = a.getDouble(1);
            int qty = a.getInt(2);

            double total = price * qty * nights;

            sb.append("• ")
                    .append(name)
                    .append(" x")
                    .append(qty)
                    .append(" = $")
                    .append(String.format(Locale.US, "%.2f", total))
                    .append("\n");
        }

        a.close();

        if (!has) return "Add-ons: None";
        return "Add-ons:\n" + sb.toString().trim();
    }

    private int calcNights(String in, String out) {

        try {

            Date d1 = DF.parse(in);
            Date d2 = DF.parse(out);

            if (d1 == null || d2 == null) return 0;

            long diff = d2.getTime() - d1.getTime();

            return (int) TimeUnit.MILLISECONDS.toDays(diff);

        } catch (ParseException e) {
            return 0;
        }
    }
}