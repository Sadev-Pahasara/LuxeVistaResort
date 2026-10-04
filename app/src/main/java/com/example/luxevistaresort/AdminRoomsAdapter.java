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

public class AdminRoomsAdapter extends RecyclerView.Adapter<AdminRoomsAdapter.RoomVH> {

    private final ArrayList<AdminRoom> rooms;

    public AdminRoomsAdapter(ArrayList<AdminRoom> rooms) {
        this.rooms = (rooms != null) ? rooms : new ArrayList<>();
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
        AdminRoom room = rooms.get(position);

        // ✅ Always set name/price
        holder.tvName.setText(room.name);
        holder.tvPrice.setText(room.price);

        // ✅ Load gallery image if available, otherwise drawable fallback
        if (!TextUtils.isEmpty(room.imageUri)) {

            Glide.with(holder.itemView.getContext())
                    .load(room.imageUri)
                    .placeholder(room.imageRes)  // ✅ avoids blank while loading
                    .error(room.imageRes)        // ✅ fallback if uri fails
                    .centerCrop()
                    .into(holder.imgRoom);

        } else {
            // ✅ important: clear any old Glide request (RecyclerView recycling)
            Glide.with(holder.itemView.getContext()).clear(holder.imgRoom);
            holder.imgRoom.setImageResource(room.imageRes);
        }

        // ✅ Info button -> open AdminRoomInfoActivity
        holder.btnInfo.setOnClickListener(v -> {
            Context context = v.getContext();

            Intent i = new Intent(context, AdminRoomInfoActivity.class);

            // ✅ PASS ID so info screen can fetch image_uri from DB
            i.putExtra(AdminRoomInfoActivity.EXTRA_ROOM_ID, room.id);

            i.putExtra(AdminRoomInfoActivity.EXTRA_ROOM_NAME, room.name);
            i.putExtra(AdminRoomInfoActivity.EXTRA_ROOM_PRICE, room.price);
            i.putExtra(AdminRoomInfoActivity.EXTRA_ROOM_DESC, room.description);
            i.putExtra(AdminRoomInfoActivity.EXTRA_ROOM_IMAGE, room.imageRes);

            ArrayList<String> feats = (room.features != null) ? room.features : new ArrayList<>();
            i.putStringArrayListExtra(AdminRoomInfoActivity.EXTRA_FEATURES, feats);

            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    static class RoomVH extends RecyclerView.ViewHolder {
        ImageView imgRoom;
        TextView tvName, tvPrice;
        ImageButton btnInfo;

        RoomVH(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            tvName  = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnInfo = itemView.findViewById(R.id.btnInfo);
        }
    }
}