package com.example.luxevistaresort;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class BookingDetailsActivity extends AppCompatActivity {

    private TextView tvCustomerName, tvPaymentStatus;
    private EditText etRoomType, etAdditionalServices, etPrice, etDiscount, etTotalPrice;

    private DBHelper db;
    private long bookingId = -1;
    private String loadedStatus = "PENDING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        db = new DBHelper(this);

        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);

        etRoomType = findViewById(R.id.etRoomType);
        etAdditionalServices = findViewById(R.id.etAdditionalServices);
        etPrice = findViewById(R.id.etPrice);
        etDiscount = findViewById(R.id.etDiscount);
        etTotalPrice = findViewById(R.id.etTotalPrice);

        MaterialButton btnSave = findViewById(R.id.btnSave);
        MaterialButton btnDelete = findViewById(R.id.btnDelete);

        // make fields safer
        etRoomType.setKeyListener(null);
        etAdditionalServices.setKeyListener(null);
        etTotalPrice.setKeyListener(null);

        bookingId = getIntent().getLongExtra("booking_id", -1);

        if (bookingId != -1) {
            loadBookingFromDatabase(bookingId);
        } else {
            loadFallbackFromIntent();
            Toast.makeText(this, "Booking id not found", Toast.LENGTH_SHORT).show();
        }

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                calculateTotal();
            }
        };

        etPrice.addTextChangedListener(watcher);
        etDiscount.addTextChangedListener(watcher);

        btnSave.setOnClickListener(v -> saveBookingChanges());
        btnDelete.setOnClickListener(v -> deleteBooking());
    }

    private void loadBookingFromDatabase(long id) {
        Cursor c = db.getBookingByIdAdmin(id);

        if (c != null && c.moveToFirst()) {
            String username = c.getString(c.getColumnIndexOrThrow("username"));
            String fullName = c.getString(c.getColumnIndexOrThrow("full_name"));
            String roomTitle = c.getString(c.getColumnIndexOrThrow("room_title"));
            double totalPrice = c.getDouble(c.getColumnIndexOrThrow("total_price"));
            loadedStatus = c.getString(c.getColumnIndexOrThrow("status"));

            if (fullName == null || fullName.trim().isEmpty()) {
                fullName = username;
            }

            tvCustomerName.setText(fullName);
            etRoomType.setText(roomTitle);
            etPrice.setText(String.format(Locale.US, "%.2f", totalPrice));
            etDiscount.setText("0");
            etAdditionalServices.setText(buildAddonsText(id));

            applyPaymentStatus(loadedStatus);
            calculateTotal();
        } else {
            Toast.makeText(this, "Booking data not found", Toast.LENGTH_SHORT).show();
            loadFallbackFromIntent();
        }

        if (c != null) c.close();
    }

    private void loadFallbackFromIntent() {
        String guestName = getIntent().getStringExtra("guest_name");
        String roomType = getIntent().getStringExtra("room_type");
        String status = getIntent().getStringExtra("status");
        double totalPrice = getIntent().getDoubleExtra("total_price", 0);

        if (guestName != null && !guestName.trim().isEmpty()) {
            tvCustomerName.setText(guestName);
        }

        if (roomType != null) {
            etRoomType.setText(roomType);
        }

        etPrice.setText(String.format(Locale.US, "%.2f", totalPrice));
        etDiscount.setText("0");
        etAdditionalServices.setText("No additional services.");

        if (status != null) {
            loadedStatus = status;
        }

        applyPaymentStatus(loadedStatus);
        calculateTotal();
    }

    private String buildAddonsText(long bookingId) {
        Cursor c = db.getAddonsByBookingId(bookingId);
        if (c == null) return "No additional services.";

        StringBuilder sb = new StringBuilder();
        boolean hasData = false;

        while (c.moveToNext()) {
            hasData = true;

            String addonName = c.getString(0);
            double addonPrice = c.getDouble(1);
            int qty = c.getInt(2);

            sb.append("• ")
                    .append(addonName)
                    .append("  x")
                    .append(qty)
                    .append("  ($")
                    .append(String.format(Locale.US, "%.2f", addonPrice))
                    .append(")")
                    .append("\n");
        }

        c.close();

        if (!hasData) return "No additional services.";
        return sb.toString().trim();
    }

    private void applyPaymentStatus(String status) {
        if (status == null) status = "PENDING";

        String s = status.trim().toUpperCase(Locale.US);

        if (s.equals("COMPLETED") || s.equals("PAID")) {
            tvPaymentStatus.setText("Paid");
            tvPaymentStatus.setTextColor(0xFF16A34A);
        } else {
            tvPaymentStatus.setText("On-going");
            tvPaymentStatus.setTextColor(0xFFF59E0B);
        }
    }

    private void calculateTotal() {
        double price = parseDouble(etPrice.getText().toString());
        double discount = parseDouble(etDiscount.getText().toString());

        if (discount < 0) discount = 0;
        if (discount > 100) discount = 100;

        double total = price - (price * discount / 100.0);
        if (total < 0) total = 0;

        etTotalPrice.setText(String.format(Locale.US, "%.2f", total));
    }

    private void saveBookingChanges() {
        if (bookingId == -1) {
            Toast.makeText(this, "Invalid booking id", Toast.LENGTH_SHORT).show();
            return;
        }

        double newTotal = parseDouble(etTotalPrice.getText().toString());

        SQLiteDatabase wdb = db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("total_price", newTotal);

        int rows = wdb.update(
                "bookings",
                cv,
                "id=?",
                new String[]{String.valueOf(bookingId)}
        );

        if (rows > 0) {
            Toast.makeText(this, "Booking updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBooking() {
        if (bookingId == -1) {
            Toast.makeText(this, "Invalid booking id", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok = db.deleteBookingAdmin(bookingId);

        if (ok) {
            Toast.makeText(this, "Booking deleted", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
        }
    }

    private double parseDouble(String s) {
        try {
            if (s == null) return 0;
            s = s.trim().replace("%", "");
            if (s.isEmpty()) return 0;
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }
}