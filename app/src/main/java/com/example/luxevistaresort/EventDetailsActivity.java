package com.example.luxevistaresort;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        ImageView imgEvent = findViewById(R.id.imgEventDetail);
        TextView tvTitle = findViewById(R.id.tvEventDetailTitle);
        TextView tvDesc = findViewById(R.id.tvEventDetailDesc);
        TextView tvDate = findViewById(R.id.tvEventDetailDate);
        TextView tvTime = findViewById(R.id.tvEventDetailTime);
        TextView tvLocation = findViewById(R.id.tvEventDetailLocation);
        MaterialButton btnParticipate = findViewById(R.id.btnParticipateEvent);

        String title = getIntent().getStringExtra("event_title");
        String desc = getIntent().getStringExtra("event_desc");
        String imageUri = getIntent().getStringExtra("event_image_uri");
        String date = getIntent().getStringExtra("event_date");
        String time = getIntent().getStringExtra("event_time");
        String location = getIntent().getStringExtra("event_location");

        tvTitle.setText(title != null ? title : "Event");
        tvDesc.setText(desc != null ? desc : "");
        tvDate.setText("Date: " + (date != null ? date : "-"));
        tvTime.setText("Time: " + (time != null ? time : "-"));
        tvLocation.setText("Location: " + (location != null ? location : "-"));

        if (imageUri != null && !imageUri.trim().isEmpty()) {
            try {
                imgEvent.setImageURI(Uri.parse(imageUri));
            } catch (Exception e) {
                imgEvent.setImageResource(R.drawable.logo_black);
            }
        } else {
            imgEvent.setImageResource(R.drawable.logo_black);
        }

        btnParticipate.setOnClickListener(v ->
                Toast.makeText(this, "Participation successful", Toast.LENGTH_SHORT).show());
    }
}