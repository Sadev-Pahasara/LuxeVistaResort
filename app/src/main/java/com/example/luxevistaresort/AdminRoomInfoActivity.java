package com.example.luxevistaresort;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Locale;

public class AdminRoomInfoActivity extends AppCompatActivity {

    public static final String EXTRA_ROOM_ID = "room_id";
    public static final String EXTRA_ROOM_NAME = "room_name";
    public static final String EXTRA_ROOM_PRICE = "room_price";
    public static final String EXTRA_ROOM_DESC = "room_desc";
    public static final String EXTRA_ROOM_IMAGE = "room_image";
    public static final String EXTRA_FEATURES = "features";

    private int roomId = -1;

    private ImageView imgRoom;
    private TextView tvTitle, tvPrice, tvDesc;
    private RecyclerView rvFeatures;

    private MaterialButton btnEditRoom, btnToggleAvailability, btnDeleteRoom;

    private DBHelper db;
    private ArrayList<String> featuresList = new ArrayList<>();

    private String imageUri = null;
    private int imageResFallback = R.drawable.room1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_room_info);

        db = new DBHelper(this);

        imgRoom = findViewById(R.id.imgRoomDetail);
        tvTitle = findViewById(R.id.tvRoomTitle);
        tvPrice = findViewById(R.id.tvRoomPriceDetail);
        tvDesc  = findViewById(R.id.tvRoomDesc);
        rvFeatures = findViewById(R.id.rvKeyFeatures);

        btnEditRoom = findViewById(R.id.btnEditRoom);
        btnToggleAvailability = findViewById(R.id.btnToggleAvailability);
        btnDeleteRoom = findViewById(R.id.btnDeleteRoom);

        // ----- get intent -----
        roomId = getIntent().getIntExtra(EXTRA_ROOM_ID, -1);

        String roomName = getIntent().getStringExtra(EXTRA_ROOM_NAME);
        String roomPrice = getIntent().getStringExtra(EXTRA_ROOM_PRICE);
        String roomDesc = getIntent().getStringExtra(EXTRA_ROOM_DESC);
        imageResFallback = getIntent().getIntExtra(EXTRA_ROOM_IMAGE, R.drawable.room1);

        ArrayList<String> feats = getIntent().getStringArrayListExtra(EXTRA_FEATURES);
        if (feats != null) featuresList = feats;

        if (roomName == null) roomName = "Room";
        if (roomPrice == null) roomPrice = "";
        if (roomDesc == null) roomDesc = "";

        tvTitle.setText(roomName);
        tvPrice.setText(roomPrice);
        tvDesc.setText(roomDesc);

        // ✅ Try load image_uri from DB (so admin sees uploaded image too)
        if (roomId != -1) {
            imageUri = db.getRoomImageUri(roomId);
        }

        if (!TextUtils.isEmpty(imageUri)) {
            Glide.with(this).load(imageUri).centerCrop().into(imgRoom);
        } else {
            imgRoom.setImageResource(imageResFallback);
        }

        rvFeatures.setLayoutManager(new LinearLayoutManager(this));
        rvFeatures.setNestedScrollingEnabled(false);
        rvFeatures.setAdapter(new KeyFeaturesAdapter(featuresList));

        // ----- buttons -----
        refreshToggleButtonText();

        btnEditRoom.setOnClickListener(v -> showEditDialog());
        btnToggleAvailability.setOnClickListener(v -> toggleAvailability());
        btnDeleteRoom.setOnClickListener(v -> confirmDelete());
    }

    private void refreshToggleButtonText() {
        if (roomId == -1) {
            btnToggleAvailability.setText("Toggle Availability");
            return;
        }
        String status = db.getRoomStatus(roomId);
        if (status == null) status = "AVAILABLE";

        if ("AVAILABLE".equalsIgnoreCase(status)) {
            btnToggleAvailability.setText("Toggle Availability (AVAILABLE)");
        } else {
            btnToggleAvailability.setText("Toggle Availability (UNAVAILABLE)");
        }
    }

    private void showEditDialog() {
        if (roomId == -1) {
            toast("Room id missing");
            return;
        }

        // current values from UI
        String curName = tvTitle.getText().toString().trim();

        // tvPrice is like "From $200" OR "$200" — extract number safely
        double curPriceVal = extractPrice(tvPrice.getText().toString());

        String curFeaturesBullets = listToBullets(featuresList);

        // dialog views
        EditText etName = new EditText(this);
        etName.setHint("Room Name");
        etName.setText(curName);

        EditText etPrice = new EditText(this);
        etPrice.setHint("Price (number only)");
        etPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etPrice.setText(String.valueOf(curPriceVal));

        EditText etFeatures = new EditText(this);
        etFeatures.setHint("Key Features (one per line)");
        etFeatures.setMinLines(4);
        etFeatures.setText(curFeaturesBullets);

        // container
        android.widget.LinearLayout box = new android.widget.LinearLayout(this);
        box.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        box.setPadding(pad, pad, pad, pad);

        box.addView(etName);
        box.addView(etPrice);
        box.addView(etFeatures);

        new AlertDialog.Builder(this)
                .setTitle("Edit Room")
                .setView(box)
                .setPositiveButton("Save", (d, w) -> {
                    String newName = etName.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();
                    String newFeatures = etFeatures.getText().toString();

                    if (TextUtils.isEmpty(newName)) {
                        toast("Name required");
                        return;
                    }
                    if (TextUtils.isEmpty(priceStr)) {
                        toast("Price required");
                        return;
                    }

                    double newPrice;
                    try {
                        newPrice = Double.parseDouble(priceStr);
                    } catch (Exception e) {
                        toast("Invalid price");
                        return;
                    }

                    // normalize bullets: allow user to type plain lines, we store with • prefix
                    String normalizedBullets = normalizeToBullets(newFeatures);

                    boolean ok = db.updateRoomAdmin(roomId, newName, newPrice, normalizedBullets);
                    if (!ok) {
                        toast("Update failed");
                        return;
                    }

                    // update UI
                    tvTitle.setText(newName);
                    tvPrice.setText(String.format(Locale.US, "From $%d", (int) newPrice));

                    featuresList = parseBulletsToList(normalizedBullets);
                    rvFeatures.setAdapter(new KeyFeaturesAdapter(featuresList));

                    toast("Updated");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void toggleAvailability() {
        if (roomId == -1) {
            toast("Room id missing");
            return;
        }
        boolean ok = db.toggleRoomAvailability(roomId);
        if (!ok) {
            toast("Toggle failed");
            return;
        }
        refreshToggleButtonText();
        toast("Status updated");
    }

    private void confirmDelete() {
        if (roomId == -1) {
            toast("Room id missing");
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Delete Room")
                .setMessage("Are you sure you want to delete this room?")
                .setPositiveButton("Delete", (d, w) -> {
                    boolean ok = db.deleteRoomAdmin(roomId);
                    if (ok) {
                        toast("Room deleted");
                        finish();
                    } else {
                        toast("Delete failed");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private double extractPrice(String priceText) {
        if (priceText == null) return 0;
        String digits = priceText.replaceAll("[^0-9.]", "");
        if (TextUtils.isEmpty(digits)) return 0;
        try { return Double.parseDouble(digits); } catch (Exception e) { return 0; }
    }

    private String listToBullets(ArrayList<String> list) {
        if (list == null || list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            if (TextUtils.isEmpty(s)) continue;
            if (sb.length() > 0) sb.append("\n");
            sb.append("• ").append(s.trim());
        }
        return sb.toString();
    }

    private String normalizeToBullets(String text) {
        if (text == null) return "";
        String[] lines = text.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            String clean = line.replace("•", "").trim();
            if (clean.isEmpty()) continue;
            if (sb.length() > 0) sb.append("\n");
            sb.append("• ").append(clean);
        }
        return sb.toString();
    }

    private ArrayList<String> parseBulletsToList(String bullets) {
        ArrayList<String> list = new ArrayList<>();
        if (bullets == null) return list;

        String[] lines = bullets.split("\n");
        for (String line : lines) {
            String clean = line.replace("•", "").trim();
            if (!clean.isEmpty()) list.add(clean);
        }
        return list;
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}