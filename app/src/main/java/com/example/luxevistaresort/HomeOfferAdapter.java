package com.example.luxevistaresort;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeOfferAdapter extends RecyclerView.Adapter<HomeOfferAdapter.VH> {

    public interface OnOfferClickListener {
        void onOfferClick(OfferItem item);
    }

    private final Context context;
    private final List<OfferItem> list;
    private final OnOfferClickListener listener;

    public HomeOfferAdapter(Context context, List<OfferItem> list, OnOfferClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_home_offer, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        OfferItem item = list.get(position);

        if (item.imageUri != null && !item.imageUri.trim().isEmpty()) {
            try {
                h.imgOffer.setImageURI(Uri.parse(item.imageUri));
            } catch (Exception e) {
                h.imgOffer.setImageResource(R.drawable.logo_black);
            }
        } else {
            h.imgOffer.setImageResource(R.drawable.logo_black);
        }

        h.itemView.setOnClickListener(v -> listener.onOfferClick(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgOffer;

        public VH(@NonNull View itemView) {
            super(itemView);
            imgOffer = itemView.findViewById(R.id.imgHomeOffer);
        }
    }
}