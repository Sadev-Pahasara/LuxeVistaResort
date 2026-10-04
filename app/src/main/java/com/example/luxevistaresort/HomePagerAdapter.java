package com.example.luxevistaresort;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HomePagerAdapter extends FragmentStateAdapter {

    public HomePagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new HomeFragment();     // carousel page
        if (position == 1) return new HistoryFragment();   // billing/services
        return new ProfileFragment();                      // profile form
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
