package com.example.luxevistaresort;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        MaterialButton btnBookings = findViewById(R.id.btnManageBookings);
        MaterialButton btnRooms = findViewById(R.id.btnManageRooms);
        MaterialButton btnServices = findViewById(R.id.btnManageServices);
        MaterialButton btnOffers = findViewById(R.id.btnOffers);
        MaterialButton btnEvent = findViewById(R.id.btnEvent);

        btnBookings.setOnClickListener(v ->
                startActivity(new Intent(this, AdminBookingsActivity.class)));

        btnRooms.setOnClickListener(v ->
                startActivity(new Intent(this, AdminRoomsActivity.class)));

        btnServices.setOnClickListener(v ->
                startActivity(new Intent(this, AdminServicesActivity.class)));

        btnOffers.setOnClickListener(v ->
                startActivity(new Intent(this, AdminOffersActivity.class)));

        btnEvent.setOnClickListener(v ->
                startActivity(new Intent(this, AdminEventsActivity.class)));
    }
}