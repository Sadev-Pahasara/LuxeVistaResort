package com.example.luxevistaresort;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class AdminRoomsActivity extends AppCompatActivity {

    private RecyclerView rvRoomsGrid;

    private EditText etRoomName;
    private EditText etRoomDescription;
    private EditText etRoomFeatures;
    private EditText etRoomPrice;

    private FrameLayout boxRoomImage;
    private MaterialButton btnAddRoom;

    private DBHelper db;

    private String selectedImageUri = null;

    private ArrayList<AdminRoom> rooms = new ArrayList<>();
    private AdminRoomsAdapter adapter;

    // Image picker
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {

                if (uri == null) return;

                try {
                    getContentResolver().takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                } catch (Exception ignored) {}

                selectedImageUri = uri.toString();

                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_rooms);

        db = new DBHelper(this);

        rvRoomsGrid = findViewById(R.id.rvAdminRoomsGrid);

        etRoomName = findViewById(R.id.etRoomName);
        etRoomDescription = findViewById(R.id.etRoomDescription);
        etRoomFeatures = findViewById(R.id.etRoomFeatures);
        etRoomPrice = findViewById(R.id.etRoomPrice);

        boxRoomImage = findViewById(R.id.boxRoomImage);
        btnAddRoom = findViewById(R.id.btnAddRoom);

        rvRoomsGrid.setLayoutManager(new GridLayoutManager(this, 2));

        loadRooms();

        boxRoomImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnAddRoom.setOnClickListener(v -> addRoom());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRooms();
    }

    // Load rooms from DB
    private void loadRooms() {

        rooms = db.getAllRoomsAdminList();

        adapter = new AdminRoomsAdapter(rooms);
        rvRoomsGrid.setAdapter(adapter);
    }

    // Add new room
    private void addRoom() {

        String name = etRoomName.getText().toString().trim();
        String desc = etRoomDescription.getText().toString().trim();
        String features = etRoomFeatures.getText().toString().trim();
        String priceStr = etRoomPrice.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            toast("Enter room name");
            return;
        }

        if (TextUtils.isEmpty(desc)) {
            toast("Enter description");
            return;
        }

        if (TextUtils.isEmpty(features)) {
            toast("Enter features");
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            toast("Enter price");
            return;
        }

        double price;

        try {
            price = Double.parseDouble(priceStr.replace("$", ""));
        } catch (Exception e) {
            toast("Invalid price");
            return;
        }

        // Convert features to bullet format
        String[] parts = features.split(",");
        StringBuilder featureBuilder = new StringBuilder();

        for (String p : parts) {

            String f = p.trim();

            if (f.isEmpty()) continue;

            if (featureBuilder.length() > 0) featureBuilder.append("\n");

            featureBuilder.append("• ").append(f);
        }

        long id = db.addRoomAdmin(
                name,
                desc,
                featureBuilder.toString(),
                price,
                "room1",          // fallback drawable
                selectedImageUri, // gallery image
                "AVAILABLE"
        );

        if (id == -1) {
            toast("Failed to add room");
            return;
        }

        toast("Room added");

        clearForm();

        loadRooms();
    }

    private void clearForm() {

        etRoomName.setText("");
        etRoomDescription.setText("");
        etRoomFeatures.setText("");
        etRoomPrice.setText("");

        selectedImageUri = null;
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}