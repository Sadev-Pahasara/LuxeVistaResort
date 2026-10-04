package com.example.luxevistaresort;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddonAdapter extends RecyclerView.Adapter<AddonAdapter.VH> {

    private final List<AddonItem> items;

    // ✅ Works with List<AddonItem>
    public AddonAdapter(List<AddonItem> items) {
        this.items = (items != null) ? items : new ArrayList<>();
    }

    // ✅ Works with AddonItem[]
    public AddonAdapter(AddonItem[] itemsArray) {
        if (itemsArray == null) {
            this.items = new ArrayList<>();
        } else {
            this.items = new ArrayList<>(Arrays.asList(itemsArray));
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgInfo;
        TextView tvTitle, tvDesc;
        Button btnAdd;

        VH(@NonNull View itemView) {
            super(itemView);
            imgInfo = itemView.findViewById(R.id.imgInfo);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc  = itemView.findViewById(R.id.tvDesc);
            btnAdd  = itemView.findViewById(R.id.btnAdd);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_addon, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        AddonItem a = items.get(position);

        h.tvTitle.setText(a.title);
        h.tvDesc.setText(a.desc);

        // button UI
        h.btnAdd.setText(a.added ? "Added" : "Add");
        h.btnAdd.setAlpha(a.added ? 0.6f : 1f);

        h.btnAdd.setOnClickListener(v -> {
            a.added = !a.added;
            notifyItemChanged(h.getBindingAdapterPosition());
        });

        h.imgInfo.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), AddonDetailsActivity.class);
            i.putExtra("addon_id", a.id);
            v.getContext().startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public ArrayList<AddonItem> getSelectedAddons() {
        ArrayList<AddonItem> selected = new ArrayList<>();
        for (AddonItem a : items) {
            if (a.added) selected.add(a);
        }
        return selected;
    }

    // Optional helper if you ever need the list
    public List<AddonItem> getItems() {
        return items;
    }
}