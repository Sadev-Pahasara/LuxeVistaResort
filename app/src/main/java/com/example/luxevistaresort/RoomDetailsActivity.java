package com.example.luxevistaresort;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RoomDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_ROOM_ID = "room_id"; // ✅ NEW
    public static final String EXTRA_ROOM_NAME = "room_name";
    public static final String EXTRA_ROOM_PRICE = "room_price";
    public static final String EXTRA_ROOM_DESC = "room_desc";
    public static final String EXTRA_ROOM_IMAGE = "room_image"; // drawable fallback
    public static final String EXTRA_ROOM_FEATURES = "room_features";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        TextView tvTitle = findViewById(R.id.tvRoomTitle);
        ImageView imgRoom = findViewById(R.id.imgRoomDetail);
        TextView tvDesc = findViewById(R.id.tvRoomDesc);
        TextView tvPrice = findViewById(R.id.tvRoomPriceDetail);
        RecyclerView rvFeatures = findViewById(R.id.rvKeyFeatures);
        Button btnBookNow = findViewById(R.id.btnBookNow);

        int roomId = getIntent().getIntExtra(EXTRA_ROOM_ID, -1); // ✅ NEW
        String roomName = getIntent().getStringExtra(EXTRA_ROOM_NAME);
        String roomPrice = getIntent().getStringExtra(EXTRA_ROOM_PRICE);
        String roomDesc = getIntent().getStringExtra(EXTRA_ROOM_DESC);
        int roomImage = getIntent().getIntExtra(EXTRA_ROOM_IMAGE, R.drawable.room1);

        ArrayList<String> features = getIntent().getStringArrayListExtra(EXTRA_ROOM_FEATURES);
        if (features == null) features = new ArrayList<>();

        if (roomName == null) roomName = "Room";
        if (roomPrice == null) roomPrice = "";
        if (roomDesc == null) roomDesc = "";

        tvTitle.setText(roomName);
        tvPrice.setText(roomPrice);
        tvDesc.setText(roomDesc);

        // ✅ Load gallery image if available (image_uri) else drawable fallback
        String uri = null;
        if (roomId != -1) {
            DBHelper db = new DBHelper(this);
            uri = db.getRoomImageUri(roomId);
        }

        if (!TextUtils.isEmpty(uri)) {
            Glide.with(this)
                    .load(uri)
                    .placeholder(roomImage) // ✅ avoids blank while loading
                    .error(roomImage)       // ✅ fallback if uri fails
                    .centerCrop()
                    .into(imgRoom);
        } else {
            imgRoom.setImageResource(roomImage);
        }

        rvFeatures.setLayoutManager(new LinearLayoutManager(this));
        rvFeatures.setNestedScrollingEnabled(false);
        rvFeatures.setAdapter(new KeyFeaturesAdapter(features));

        final String finalRoomName = roomName;
        btnBookNow.setOnClickListener(v -> {
            Toast.makeText(this, "Booking: " + finalRoomName, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}