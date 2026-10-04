package com.example.luxevistaresort;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomVH> {

    private final List<RoomItem> rooms;
    private final boolean selectable;
    private int selectedPos = RecyclerView.NO_POSITION;

    // ✅ DB helper for image_uri lookup
    private final DBHelper db;

    public RoomsAdapter(Context context, List<RoomItem> rooms) {
        this(context, rooms, false);
    }

    public RoomsAdapter(Context context, List<RoomItem> rooms, boolean selectable) {
        this.rooms = rooms;
        this.selectable = selectable;
        this.db = new DBHelper(context.getApplicationContext());
    }

    @NonNull
    @Override
    public RoomVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room_card, parent, false);
        return new RoomVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomVH holder, int position) {
        RoomItem room = rooms.get(position);

        holder.tvName.setText(room.name);
        holder.tvPrice.setText(room.priceText);

        // ✅ Load gallery image if exists; else fallback drawable
        String uri = db.getRoomImageUri(room.id);
        if (!TextUtils.isEmpty(uri)) {
            Glide.with(holder.itemView.getContext())
                    .load(uri)
                    .placeholder(room.imageRes)   // ✅ prevents blank while loading
                    .error(room.imageRes)         // ✅ fallback if uri fails
                    .centerCrop()
                    .into(holder.imgRoom);
        } else {
            // ✅ clear previous Glide request (RecyclerView reuse fix)
            Glide.with(holder.itemView.getContext()).clear(holder.imgRoom);
            holder.imgRoom.setImageResource(room.imageRes);
        }

        holder.itemView.setSelected(selectable && position == selectedPos);

        // ✅ INFO -> Visitor RoomDetailsActivity
        holder.btnInfo.setOnClickListener(v -> {
            Context context = v.getContext();

            Intent i = new Intent(context, RoomDetailsActivity.class);

            // ✅ IMPORTANT: pass room id so RoomDetailsActivity can load image_uri
            i.putExtra(RoomDetailsActivity.EXTRA_ROOM_ID, room.id);

            i.putExtra(RoomDetailsActivity.EXTRA_ROOM_NAME, room.name);
            i.putExtra(RoomDetailsActivity.EXTRA_ROOM_PRICE, room.priceText);
            i.putExtra(RoomDetailsActivity.EXTRA_ROOM_DESC, room.description);

            // drawable fallback
            i.putExtra(RoomDetailsActivity.EXTRA_ROOM_IMAGE, room.imageRes);

            ArrayList<String> feats = room.getFeaturesList();
            i.putStringArrayListExtra(RoomDetailsActivity.EXTRA_ROOM_FEATURES, feats);

            context.startActivity(i);
        });

        // tap card selects room (only if selectable = true)
        if (selectable) {
            holder.itemView.setOnClickListener(v -> {
                int old = selectedPos;
                selectedPos = holder.getBindingAdapterPosition();
                notifyItemChanged(old);
                notifyItemChanged(selectedPos);
            });
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public RoomItem getSelectedRoom() {
        if (selectedPos == RecyclerView.NO_POSITION) return null;
        if (selectedPos < 0 || selectedPos >= rooms.size()) return null;
        return rooms.get(selectedPos);
    }

    static class RoomVH extends RecyclerView.ViewHolder {
        ImageView imgRoom;
        TextView tvName, tvPrice;
        ImageButton btnInfo;

        RoomVH(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnInfo = itemView.findViewById(R.id.btnInfo);
        }
    }
}