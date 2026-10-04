package com.example.luxevistaresort;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class OfferDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);

        ImageView imgOffer = findViewById(R.id.imgOfferDetail);
        TextView tvTitle = findViewById(R.id.tvOfferDetailTitle);
        TextView tvDesc = findViewById(R.id.tvOfferDetailDesc);
        TextView tvDiscount = findViewById(R.id.tvOfferDetailDiscount);
        TextView tvExpiry = findViewById(R.id.tvOfferDetailExpiry);
        MaterialButton btnBookOfferRoom = findViewById(R.id.btnBookOfferRoom);

        String title = getIntent().getStringExtra("offer_title");
        String desc = getIntent().getStringExtra("offer_desc");
        String discount = getIntent().getStringExtra("offer_discount");
        String imageUri = getIntent().getStringExtra("offer_image_uri");
        String expiry = getIntent().getStringExtra("offer_expiry");

        tvTitle.setText(title != null ? title : "Offer");
        tvDesc.setText(desc != null ? desc : "");
        tvDiscount.setText(discount != null ? discount : "");
        tvExpiry.setText("Expiry: " + (expiry != null ? expiry : "-"));

        if (imageUri != null && !imageUri.trim().isEmpty()) {
            try {
                imgOffer.setImageURI(Uri.parse(imageUri));
            } catch (Exception e) {
                imgOffer.setImageResource(R.drawable.logo_black);
            }
        } else {
            imgOffer.setImageResource(R.drawable.logo_black);
        }

        btnBookOfferRoom.setOnClickListener(v -> {
            Intent i = new Intent(this, RoomBookingActivity.class);
            i.putExtra("offer_title", title);
            i.putExtra("offer_discount", discount);
            startActivity(i);
        });
    }
}