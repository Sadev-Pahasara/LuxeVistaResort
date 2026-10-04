package com.example.luxevistaresort;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // Show logged-in user's full name
        TextView tvUser = v.findViewById(R.id.tvUser);
        SharedPreferences sp = requireActivity().getSharedPreferences("USER_SESSION", 0);
        String fullName = sp.getString("FULL_NAME", "User");
        if (tvUser != null) {
            tvUser.setText(fullName);
        }

        // Top room carousel
        ViewPager2 pager = v.findViewById(R.id.carouselPager);
        int[] images = new int[]{
                R.drawable.room1,
                R.drawable.room2,
                R.drawable.room3
        };
        pager.setAdapter(new CarouselAdapter(images));

        // RecyclerViews
        RecyclerView rvHomeEvents = v.findViewById(R.id.rvHomeEvents);
        RecyclerView rvHomeOffers = v.findViewById(R.id.rvHomeOffers);

        rvHomeEvents.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        rvHomeOffers.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        DBHelper db = new DBHelper(requireContext());

        ArrayList<EventItem> events = db.getAllActiveEvents();
        ArrayList<OfferItem> offers = db.getAllActiveOffers();

        HomeEventAdapter eventAdapter = new HomeEventAdapter(requireContext(), events, item -> {
            Intent i = new Intent(requireContext(), EventDetailsActivity.class);
            i.putExtra("event_id", item.id);
            i.putExtra("event_title", item.title);
            i.putExtra("event_desc", item.description);
            i.putExtra("event_image_uri", item.imageUri);
            i.putExtra("event_date", item.eventDate);
            i.putExtra("event_time", item.eventTime);
            i.putExtra("event_location", item.location);
            startActivity(i);
        });

        HomeOfferAdapter offerAdapter = new HomeOfferAdapter(requireContext(), offers, item -> {
            Intent i = new Intent(requireContext(), OfferDetailsActivity.class);
            i.putExtra("offer_id", item.id);
            i.putExtra("offer_title", item.title);
            i.putExtra("offer_desc", item.description);
            i.putExtra("offer_discount", item.discountText);
            i.putExtra("offer_image_uri", item.imageUri);
            i.putExtra("offer_expiry", item.expiryDate);
            startActivity(i);
        });

        rvHomeEvents.setAdapter(eventAdapter);
        rvHomeOffers.setAdapter(offerAdapter);

        // Room booking button
        v.findViewById(R.id.btnRoomBooking).setOnClickListener(view ->
                startActivity(new Intent(requireActivity(), RoomBookingActivity.class))
        );

        return v;
    }
}