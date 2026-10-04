package com.example.luxevistaresort;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewPager = findViewById(R.id.viewPager);

        HomePagerAdapter adapter = new HomePagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.setUserInputEnabled(false);

        // Custom bottom bar clicks
        findViewById(R.id.navHome).setOnClickListener(v -> selectTab(0));
        findViewById(R.id.navHistory).setOnClickListener(v -> selectTab(1));
        findViewById(R.id.navProfile).setOnClickListener(v -> selectTab(2));

        // Sync when user swipes pages
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                selectTab(position);
            }
        });

        selectTab(0); // default
    }

    private void selectTab(int index) {
        View navHome = findViewById(R.id.navHome);
        View navHistory = findViewById(R.id.navHistory);
        View navProfile = findViewById(R.id.navProfile);

        ImageView icHome = findViewById(R.id.icHome);
        ImageView icHistory = findViewById(R.id.icHistory);
        ImageView icProfile = findViewById(R.id.icProfile);

        TextView txtHome = findViewById(R.id.txtHome);
        TextView txtHistory = findViewById(R.id.txtHistory);
        TextView txtProfile = findViewById(R.id.txtProfile);

        ImageView imgNavBg = findViewById(R.id.imgNavBg);
        View fabCircle = findViewById(R.id.fabCircle);
        ImageView fabIcon = findViewById(R.id.fabIcon);

        int gray = 0xFF9E9E9E;
        int purple = 0xFF6A1B9A;

        // reset colors
        icHome.setColorFilter(gray);     txtHome.setTextColor(gray);
        icHistory.setColorFilter(gray);  txtHistory.setTextColor(gray);
        icProfile.setColorFilter(gray);  txtProfile.setTextColor(gray);

        // move circle to clicked item center
        View target = (index == 0) ? navHome : (index == 1) ? navHistory : navProfile;
        target.post(() -> {
            float targetCenterX = target.getX() + (target.getWidth() / 2f);
            float circleHalf = fabCircle.getWidth() / 2f;
            fabCircle.animate().x(targetCenterX - circleHalf).setDuration(220).start();
        });

        // set selected colors + circle icon + background
        if (index == 0) {
            icHome.setColorFilter(purple); txtHome.setTextColor(purple);
            fabIcon.setImageResource(R.drawable.ic_home);
            imgNavBg.setImageResource(R.drawable.bg_nav_left);
        } else if (index == 1) {
            icHistory.setColorFilter(purple); txtHistory.setTextColor(purple);
            fabIcon.setImageResource(R.drawable.ic_history);
            imgNavBg.setImageResource(R.drawable.bg_nav_center);
        } else {
            icProfile.setColorFilter(purple); txtProfile.setTextColor(purple);
            fabIcon.setImageResource(R.drawable.ic_profile);
            imgNavBg.setImageResource(R.drawable.bg_nav_right);
        }

        // avoid infinite loop when swiping triggers selectTab()
        if (viewPager.getCurrentItem() != index) {
            viewPager.setCurrentItem(index, true);
        }
    }
}