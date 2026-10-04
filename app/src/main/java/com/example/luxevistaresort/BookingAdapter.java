package com.example.luxevistaresort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.VH> {

    private final List<BookingItem> items;
    private final boolean showApproveButton; // true for admin/executive, false for visitor

    public BookingAdapter(List<BookingItem> items) {
        this(items, false); // default visitor mode
    }

    public BookingAdapter(List<BookingItem> items, boolean showApproveButton) {
        this.items = items;
        this.showApproveButton = showApproveButton;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        BookingItem b = items.get(position);

        // ✅ Booking ID (example BK-12)
        h.tvId.setText((b.id == null || b.id.trim().isEmpty()) ? "BK" : b.id);

        // ✅ Guest
        h.tvGuest.setText("Guest: " + (b.guest == null ? "" : b.guest));

        // ✅ Booking info (Room + dates + price etc.)
        h.tvInfo.setText(b.info == null ? "" : b.info);

        // ✅ Status
        h.tvStatus.setText(b.status == null ? "" : b.status);

        // ✅ Approve button (Admin only)
        h.btnApprove.setVisibility(showApproveButton ? View.VISIBLE : View.GONE);

        // View
        h.btnView.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "View " + b.id, Toast.LENGTH_SHORT).show()
        );

        // Approve (admin)
        h.btnApprove.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Approved " + b.id, Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvId, tvStatus, tvGuest, tvInfo;
        MaterialButton btnView, btnApprove;

        VH(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvBookingId);
            tvStatus = itemView.findViewById(R.id.tvBookingStatus);
            tvGuest = itemView.findViewById(R.id.tvGuestName);
            tvInfo = itemView.findViewById(R.id.tvBookingInfo);
            btnView = itemView.findViewById(R.id.btnView);
            btnApprove = itemView.findViewById(R.id.btnApprove);
        }
    }
}