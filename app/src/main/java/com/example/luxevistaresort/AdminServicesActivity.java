package com.example.luxevistaresort;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class AdminServicesActivity extends AppCompatActivity implements AdminServicesAdapter.Listener {

    private DBHelper db;
    private AdminServicesAdapter adapter;
    private RecyclerView rv;

    private EditText etName, etDesc, etFeatures, etPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_services);

        db = new DBHelper(this);

        rv = findViewById(R.id.rvAdminServices);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setNestedScrollingEnabled(true);

        adapter = new AdminServicesAdapter(this);
        rv.setAdapter(adapter);

        // Form
        FrameLayout boxImage = findViewById(R.id.boxServiceImage);
        etName = findViewById(R.id.etServiceName);
        etDesc = findViewById(R.id.etServiceDescription);
        etFeatures = findViewById(R.id.etServiceFeatures);
        etPrice = findViewById(R.id.etServicePrice);
        MaterialButton btnAdd = findViewById(R.id.btnAddService);

        boxImage.setOnClickListener(v ->
                Toast.makeText(this, "Image upload: connect picker later (optional)", Toast.LENGTH_SHORT).show()
        );

        btnAdd.setOnClickListener(v -> addService());

        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        adapter.setItems(db.getAllAddonsAdminList());
    }

    private void addService() {
        String name = etName.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String features = etFeatures.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (name.isEmpty() || desc.isEmpty() || features.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = parseDouble(priceStr);
        if (price <= 0) {
            Toast.makeText(this, "Enter a valid price", Toast.LENGTH_SHORT).show();
            return;
        }

        // image_name: keep default for now. You can connect image picker later.
        long id = db.addAddonAdmin(
                name, desc, features,
                price,
                "addon_spa",     // fallback drawable name
                "OTHER",
                "ACTIVE"
        );

        if (id != -1) {
            etName.setText("");
            etDesc.setText("");
            etFeatures.setText("");
            etPrice.setText("");
            Toast.makeText(this, "Service added", Toast.LENGTH_SHORT).show();
            refresh();
            rv.scrollToPosition(0);
        } else {
            Toast.makeText(this, "Add failed", Toast.LENGTH_SHORT).show();
        }
    }

    // -------- Adapter callbacks --------
    @Override
    public void onServiceClicked(AdminServiceItem item) {
        // quick toggle on tap (optional). If you prefer, remove this.
        boolean ok = db.toggleAddonStatusAdmin(item.id);
        if (ok) {
            Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show();
            refresh();
        } else {
            Toast.makeText(this, "Status update failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onServiceLongPressed(AdminServiceItem item) {
        String toggleText = "ACTIVE".equalsIgnoreCase(item.status) ? "Deactivate" : "Activate";

        String[] options = new String[]{
                "Edit",
                toggleText,
                "Delete"
        };

        new AlertDialog.Builder(this)
                .setTitle(item.title)
                .setItems(options, (d, which) -> {
                    if (which == 0) showEditDialog(item);
                    else if (which == 1) {
                        boolean ok = db.toggleAddonStatusAdmin(item.id);
                        Toast.makeText(this, ok ? "Updated" : "Failed", Toast.LENGTH_SHORT).show();
                        refresh();
                    } else if (which == 2) {
                        confirmDelete(item);
                    }
                })
                .show();
    }

    private void showEditDialog(AdminServiceItem item) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (14 * getResources().getDisplayMetrics().density);
        root.setPadding(pad, pad, pad, pad);

        EditText eName = new EditText(this);
        eName.setHint("Service Name");
        eName.setText(item.title);

        EditText eDesc = new EditText(this);
        eDesc.setHint("Description");
        eDesc.setText(item.desc);

        EditText eFeat = new EditText(this);
        eFeat.setHint("Key Features");
        eFeat.setText(item.features);

        EditText ePrice = new EditText(this);
        ePrice.setHint("Price");
        ePrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ePrice.setText(String.format(Locale.US, "%.0f", item.price));

        root.addView(eName);
        root.addView(space());
        root.addView(eDesc);
        root.addView(space());
        root.addView(eFeat);
        root.addView(space());
        root.addView(ePrice);

        new AlertDialog.Builder(this)
                .setTitle("Edit Service")
                .setView(root)
                .setPositiveButton("Save", (d, w) -> {
                    String name = eName.getText().toString().trim();
                    String desc = eDesc.getText().toString().trim();
                    String feat = eFeat.getText().toString().trim();
                    double price = parseDouble(ePrice.getText().toString().trim());

                    if (name.isEmpty() || desc.isEmpty() || feat.isEmpty() || price <= 0) {
                        Toast.makeText(this, "Invalid fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean ok = db.updateAddonAdmin(item.id, name, desc, feat, price, item.category);
                    Toast.makeText(this, ok ? "Saved" : "Save failed", Toast.LENGTH_SHORT).show();
                    refresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete(AdminServiceItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Service?")
                .setMessage("This will remove \"" + item.title + "\" permanently.")
                .setPositiveButton("Delete", (d, w) -> {
                    boolean ok = db.deleteAddonAdmin(item.id);
                    Toast.makeText(this, ok ? "Deleted" : "Delete failed", Toast.LENGTH_SHORT).show();
                    refresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private View space() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (10 * getResources().getDisplayMetrics().density)
        ));
        return v;
    }

    private double parseDouble(String s) {
        try {
            if (s == null) return 0;
            s = s.trim().replace(",", "");
            if (s.isEmpty()) return 0;
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }
}