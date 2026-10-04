package com.example.luxevistaresort;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class AddonDetailsActivity extends AppCompatActivity {

    private ImageView img;
    private TextView tvTitle, tvPrice, tvDesc, tvFeatures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addon_details);

        // ✅ IDs must match activity_addon_details.xml
        img = findViewById(R.id.imgAddonDetail);
        tvTitle = findViewById(R.id.tvAddonTitle);
        tvPrice = findViewById(R.id.tvAddonPrice);
        tvDesc = findViewById(R.id.tvAddonDesc);
        tvFeatures = findViewById(R.id.tvAddonFeatures);

        int addonId = getIntent().getIntExtra("addon_id", -1);
        if (addonId == -1) {
            Toast.makeText(this, "Addon not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DBHelper db = new DBHelper(this);

        // ✅ Correct type (DBHelper.Addon)
        DBHelper.Addon a = db.getAddonById(addonId);

        if (a == null) {
            Toast.makeText(this, "Addon not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTitle.setText(a.name);
        tvPrice.setText(String.format(Locale.US, "$%.0f", a.price));
        tvDesc.setText(a.desc);
        tvFeatures.setText(a.features);

        // convert image_name -> drawable id
        int imgRes = db.getDrawableIdByName(a.imageName);
        if (imgRes != 0) img.setImageResource(imgRes);
    }
}