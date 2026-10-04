package com.example.luxevistaresort;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class AdminOffersActivity extends AppCompatActivity
        implements AdminOffersAdapter.OfferActionListener {

    private RecyclerView rvOffers;
    private MaterialButton btnAddOffer;

    private DBHelper db;
    private AdminOffersAdapter adapter;
    private final ArrayList<OfferItem> offerList = new ArrayList<>();

    private Uri selectedOfferImageUri = null;
    private ImageView ivOfferPreview;

    private final ActivityResultLauncher<String[]> pickOfferImageLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    selectedOfferImageUri = uri;

                    try {
                        final int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        getContentResolver().takePersistableUriPermission(uri, flags);
                    } catch (Exception ignored) {
                    }

                    if (ivOfferPreview != null) {
                        ivOfferPreview.setImageURI(uri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_offers);

        db = new DBHelper(this);

        rvOffers = findViewById(R.id.rvOffers);
        btnAddOffer = findViewById(R.id.btnAddOffer);

        rvOffers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOffersAdapter(this, offerList, this);
        rvOffers.setAdapter(adapter);

        loadOffers();

        btnAddOffer.setOnClickListener(v -> showOfferDialog(null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOffers();
    }

    private void loadOffers() {
        offerList.clear();
        offerList.addAll(db.getAllOffersAdminList());
        adapter.notifyDataSetChanged();
    }

    private void showOfferDialog(OfferItem item) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_offer, null, false);

        EditText etTitle = view.findViewById(R.id.etOfferTitle);
        EditText etDescription = view.findViewById(R.id.etOfferDescription);
        EditText etDiscountText = view.findViewById(R.id.etOfferDiscount);
        EditText etExpiryDate = view.findViewById(R.id.etOfferExpiryDate);
        ivOfferPreview = view.findViewById(R.id.ivOfferPreview);
        MaterialButton btnPickOfferImage = view.findViewById(R.id.btnPickOfferImage);

        boolean isEdit = item != null;
        selectedOfferImageUri = null;

        if (isEdit) {
            etTitle.setText(item.title);
            etDescription.setText(item.description);
            etDiscountText.setText(item.discountText);
            etExpiryDate.setText(item.expiryDate);

            if (item.imageUri != null && !item.imageUri.trim().isEmpty()) {
                try {
                    selectedOfferImageUri = Uri.parse(item.imageUri);
                    ivOfferPreview.setImageURI(selectedOfferImageUri);
                } catch (Exception e) {
                    ivOfferPreview.setImageResource(R.drawable.logo_black);
                }
            } else {
                ivOfferPreview.setImageResource(R.drawable.logo_black);
            }
        } else {
            ivOfferPreview.setImageResource(R.drawable.logo_black);
        }

        btnPickOfferImage.setOnClickListener(v -> pickOfferImageLauncher.launch(new String[]{"image/*"}));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(isEdit ? "Edit Offer" : "Add Offer")
                .setView(view)
                .setPositiveButton(isEdit ? "Update" : "Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String discountText = etDiscountText.getText().toString().trim();
            String expiryDate = etExpiryDate.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                etTitle.setError("Enter title");
                return;
            }
            if (TextUtils.isEmpty(description)) {
                etDescription.setError("Enter description");
                return;
            }
            if (TextUtils.isEmpty(discountText)) {
                etDiscountText.setError("Enter discount text");
                return;
            }
            if (TextUtils.isEmpty(expiryDate)) {
                etExpiryDate.setError("Enter expiry date");
                return;
            }

            String imageUriString;
            if (selectedOfferImageUri != null) {
                imageUriString = selectedOfferImageUri.toString();
            } else if (isEdit) {
                imageUriString = item.imageUri;
            } else {
                imageUriString = null;
            }

            if (isEdit) {
                boolean success = db.updateOfferAdmin(
                        item.id,
                        title,
                        description,
                        discountText,
                        imageUriString,
                        expiryDate
                );

                if (success) {
                    Toast.makeText(this, "Offer updated", Toast.LENGTH_SHORT).show();
                    loadOffers();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                }

            } else {
                long res = db.addOfferAdmin(
                        title,
                        description,
                        discountText,
                        imageUriString,
                        expiryDate,
                        "ACTIVE"
                );

                if (res != -1) {
                    Toast.makeText(this, "Offer added", Toast.LENGTH_SHORT).show();
                    loadOffers();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Insert failed", Toast.LENGTH_SHORT).show();
                }
            }
        }));

        dialog.show();
    }

    @Override
    public void onEditOffer(OfferItem item) {
        showOfferDialog(item);
    }

    @Override
    public void onToggleOffer(OfferItem item) {
        boolean ok = db.toggleOfferStatusAdmin(item.id);
        if (ok) {
            Toast.makeText(this, "Offer status updated", Toast.LENGTH_SHORT).show();
            loadOffers();
        } else {
            Toast.makeText(this, "Status update failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteOffer(OfferItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Offer")
                .setMessage("Are you sure you want to delete this offer?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean ok = db.deleteOfferAdmin(item.id);
                    if (ok) {
                        Toast.makeText(this, "Offer deleted", Toast.LENGTH_SHORT).show();
                        loadOffers();
                    } else {
                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}