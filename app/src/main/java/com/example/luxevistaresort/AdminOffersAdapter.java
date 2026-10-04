package com.example.luxevistaresort;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AdminOffersAdapter extends RecyclerView.Adapter<AdminOffersAdapter.VH> {

    public interface OfferActionListener {
        void onEditOffer(OfferItem item);
        void onToggleOffer(OfferItem item);
        void onDeleteOffer(OfferItem item);
    }

    private final Context context;
    private final List<OfferItem> list;
    private final OfferActionListener listener;

    public AdminOffersAdapter(Context context, List<OfferItem> list, OfferActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_admin_offer, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        OfferItem item = list.get(position);

        h.tvTitle.setText(item.title);
        h.tvDescription.setText(item.description);
        h.tvDiscount.setText(item.discountText);
        h.tvExpiry.setText("Expiry: " + item.expiryDate);
        h.tvStatus.setText(item.status);

        if ("ACTIVE".equalsIgnoreCase(item.status)) {
            h.tvStatus.setTextColor(0xFF16A34A);
        } else {
            h.tvStatus.setTextColor(0xFFD97706);
        }

        if (item.imageUri != null && !item.imageUri.trim().isEmpty()) {
            try {
                h.ivOfferImage.setImageURI(Uri.parse(item.imageUri));
            } catch (Exception e) {
                h.ivOfferImage.setImageResource(R.drawable.logo_black);
            }
        } else {
            h.ivOfferImage.setImageResource(R.drawable.logo_black);
        }

        h.btnEdit.setOnClickListener(v -> listener.onEditOffer(item));
        h.btnToggle.setOnClickListener(v -> listener.onToggleOffer(item));
        h.btnDelete.setOnClickListener(v -> listener.onDeleteOffer(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivOfferImage;
        TextView tvTitle, tvDescription, tvDiscount, tvExpiry, tvStatus;
        MaterialButton btnEdit, btnToggle, btnDelete;

        public VH(@NonNull View itemView) {
            super(itemView);

            ivOfferImage = itemView.findViewById(R.id.ivOfferImage);
            tvTitle = itemView.findViewById(R.id.tvOfferTitle);
            tvDescription = itemView.findViewById(R.id.tvOfferDescription);
            tvDiscount = itemView.findViewById(R.id.tvOfferDiscount);
            tvExpiry = itemView.findViewById(R.id.tvOfferExpiry);
            tvStatus = itemView.findViewById(R.id.tvOfferStatus);

            btnEdit = itemView.findViewById(R.id.btnEditOffer);
            btnToggle = itemView.findViewById(R.id.btnToggleOffer);
            btnDelete = itemView.findViewById(R.id.btnDeleteOffer);
        }
    }
}