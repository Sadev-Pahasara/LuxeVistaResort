package com.example.luxevistaresort;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RoomBookingActivity extends AppCompatActivity {

    private RecyclerView rvRooms, rvAddons;
    private TextView tvCheckIn, tvCheckOut;
    private MaterialButton btnBook;
    private RoomsAdapter roomsAdapter;
    private AddonAdapter addonAdapter;
    private ArrayList<AddonItem> addonItems = new ArrayList<>();
    private String checkIn = null;
    private String checkOut = null;
    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_booking);

        rvRooms = findViewById(R.id.rvRooms);
        rvAddons = findViewById(R.id.rvAddons);
        tvCheckIn = findViewById(R.id.tvCheckIn);
        tvCheckOut = findViewById(R.id.tvCheckOut);
        btnBook = findViewById(R.id.btnBook);

        DBHelper db = new DBHelper(this);

        db.seedRoomsIfEmpty();
        db.seedAddonsIfEmpty();

        // ---------- Rooms ----------
        ArrayList<RoomItem> rooms = db.getAllRooms();
        rvRooms.setLayoutManager(new GridLayoutManager(this, 2));

        roomsAdapter = new RoomsAdapter(this, rooms, true);
        rvRooms.setAdapter(roomsAdapter);

        // ---------- Addons ----------
        addonItems = db.getAllAddons();
        rvAddons.setLayoutManager(new LinearLayoutManager(this));

        addonAdapter = new AddonAdapter(addonItems);
        rvAddons.setAdapter(addonAdapter);

        // ---------- Date Pickers ----------
        tvCheckIn.setOnClickListener(v -> pickDate(true));
        tvCheckOut.setOnClickListener(v -> pickDate(false));

        // ---------- BOOK ----------
        btnBook.setOnClickListener(v -> doBook(db));
    }

    private void pickDate(boolean isCheckIn) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dlg = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.MONTH, month);
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String picked = DF.format(c.getTime());

                    if (isCheckIn) {
                        checkIn = picked;
                        tvCheckIn.setText("Check-in: " + picked);
                    } else {
                        checkOut = picked;
                        tvCheckOut.setText("Check-out: " + picked);
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );

        dlg.show();
    }

    private void doBook(DBHelper db) {
        RoomItem selectedRoom = roomsAdapter.getSelectedRoom();
        if (selectedRoom == null) {
            Toast.makeText(this, "Please select a room", Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkIn == null || checkOut == null) {
            Toast.makeText(this, "Please select check-in and check-out dates", Toast.LENGTH_SHORT).show();
            return;
        }

        int nights = calcNights(checkIn, checkOut);

        if (nights <= 0) {
            Toast.makeText(this, "Check-out must be after check-in", Toast.LENGTH_SHORT).show();
            return;
        }

        // ---------- Calculate Price ----------
        double roomPerDay = selectedRoom.basePrice;

        double addonsPerDay = 0.0;
        ArrayList<AddonItem> selectedAddons = getSelectedAddonsFromList(addonItems);

        for (AddonItem a : selectedAddons) {
            addonsPerDay += a.price;
        }

        double perDayTotal = roomPerDay + addonsPerDay;
        double grandTotal = perDayTotal * nights;


        SharedPreferences sp = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        String username = sp.getString("USERNAME", null);

        if (username == null || username.trim().isEmpty()) {
            Toast.makeText(this, "Please login before booking", Toast.LENGTH_SHORT).show();
            return;
        }

        // ---------- Insert Booking ----------
        long bookingId = db.addBookingReturnId(
                username,
                selectedRoom.name,
                checkIn,
                checkOut,
                grandTotal,
                "PENDING"
        );

        if (bookingId == -1) {
            Toast.makeText(this, "Booking failed", Toast.LENGTH_SHORT).show();
            return;
        }

        // ---------- Insert Booking Addons ----------
        for (AddonItem a : selectedAddons) {
            db.addAddonToBooking(bookingId, a.title, a.price, 1);
        }

        Toast.makeText(
                this,
                "Booked! " + nights + " nights | Total: $" +
                        String.format(Locale.US, "%.2f", grandTotal),
                Toast.LENGTH_LONG
        ).show();

        finish();
    }

    private ArrayList<AddonItem> getSelectedAddonsFromList(ArrayList<AddonItem> all) {

        ArrayList<AddonItem> selected = new ArrayList<>();

        if (all == null) return selected;

        for (AddonItem a : all) {
            if (a != null && a.added) {
                selected.add(a);
            }
        }

        return selected;
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