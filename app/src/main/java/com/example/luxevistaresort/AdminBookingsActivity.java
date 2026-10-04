package com.example.luxevistaresort;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminBookingsActivity extends AppCompatActivity {

    private DBHelper db;

    private RecyclerView rvAcceptedBookings;
    private RecyclerView rvPendingBookings;

    private AdminBookingsAdapter acceptedAdapter;
    private AdminBookingsAdapter pendingAdapter;

    private TextView tvNoAccepted;
    private TextView tvNoPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bookings);

        db = new DBHelper(this);

        rvAcceptedBookings = findViewById(R.id.rvAcceptedBookings);
        rvPendingBookings = findViewById(R.id.rvPendingBookings);
        tvNoAccepted = findViewById(R.id.tvNoAccepted);
        tvNoPending = findViewById(R.id.tvNoPending);

        rvAcceptedBookings.setLayoutManager(new LinearLayoutManager(this));
        rvPendingBookings.setLayoutManager(new LinearLayoutManager(this));

        rvAcceptedBookings.setNestedScrollingEnabled(false);
        rvPendingBookings.setNestedScrollingEnabled(false);

        acceptedAdapter = new AdminBookingsAdapter(
                this,
                new ArrayList<>(),
                AdminBookingsAdapter.MODE_ACCEPTED,
                new AdminBookingsAdapter.BookingActionListener() {
                    @Override
                    public void onInfo(AdminBookingItem item) {
                        AdminBookingsAdapter.BookingActions.openBookingDetails(AdminBookingsActivity.this, item);
                    }

                    @Override
                    public void onAccept(AdminBookingItem item) {
                        // not used in accepted mode
                    }

                    @Override
                    public void onReject(AdminBookingItem item) {
                        // not used in accepted mode
                    }
                }
        );

        pendingAdapter = new AdminBookingsAdapter(
                this,
                new ArrayList<>(),
                AdminBookingsAdapter.MODE_PENDING,
                new AdminBookingsAdapter.BookingActionListener() {
                    @Override
                    public void onInfo(AdminBookingItem item) {
                        // not used in pending mode
                    }

                    @Override
                    public void onAccept(AdminBookingItem item) {
                        boolean ok = db.updateBookingStatusAdmin(item.bookingId, "CONFIRMED");
                        if (ok) loadBookings();
                    }

                    @Override
                    public void onReject(AdminBookingItem item) {
                        boolean ok = db.updateBookingStatusAdmin(item.bookingId, "REJECTED");
                        if (ok) loadBookings();
                    }
                }
        );

        rvAcceptedBookings.setAdapter(acceptedAdapter);
        rvPendingBookings.setAdapter(pendingAdapter);

        loadBookings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
    }

    private void loadBookings() {
        ArrayList<AdminBookingItem> accepted = db.getAcceptedBookingsAdmin();
        ArrayList<AdminBookingItem> pending = db.getPendingBookingsAdmin();

        acceptedAdapter.setItems(accepted);
        pendingAdapter.setItems(pending);

        tvNoAccepted.setVisibility(accepted.isEmpty() ? View.VISIBLE : View.GONE);
        rvAcceptedBookings.setVisibility(accepted.isEmpty() ? View.GONE : View.VISIBLE);

        tvNoPending.setVisibility(pending.isEmpty() ? View.VISIBLE : View.GONE);
        rvPendingBookings.setVisibility(pending.isEmpty() ? View.GONE : View.VISIBLE);
    }
}