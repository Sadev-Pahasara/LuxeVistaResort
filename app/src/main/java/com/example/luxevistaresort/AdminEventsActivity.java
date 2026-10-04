package com.example.luxevistaresort;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class AdminEventsActivity extends AppCompatActivity
        implements AdminEventsAdapter.EventActionListener {

    private RecyclerView rvEvents;
    private MaterialButton btnAddEvent;

    private DBHelper db;
    private AdminEventsAdapter adapter;
    private final ArrayList<EventItem> eventList = new ArrayList<>();

    private Uri selectedEventImageUri = null;
    private ImageView ivEventPreview;

    private final ActivityResultLauncher<String[]> pickEventImageLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    selectedEventImageUri = uri;

                    try {
                        final int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        getContentResolver().takePersistableUriPermission(uri, flags);
                    } catch (Exception ignored) {
                    }

                    if (ivEventPreview != null) {
                        ivEventPreview.setImageURI(uri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_events);

        db = new DBHelper(this);

        rvEvents = findViewById(R.id.rvEvents);
        btnAddEvent = findViewById(R.id.btnAddEvent);

        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminEventsAdapter(this, eventList, this);
        rvEvents.setAdapter(adapter);

        loadEvents();

        btnAddEvent.setOnClickListener(v -> showEventDialog(null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        eventList.clear();
        eventList.addAll(db.getAllEventsAdminList());
        adapter.notifyDataSetChanged();
    }

    private void showEventDialog(EventItem item) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_event, null, false);

        EditText etTitle = view.findViewById(R.id.etEventTitle);
        EditText etDescription = view.findViewById(R.id.etEventDescription);
        EditText etDate = view.findViewById(R.id.etEventDate);
        EditText etTime = view.findViewById(R.id.etEventTime);
        EditText etLocation = view.findViewById(R.id.etEventLocation);
        ivEventPreview = view.findViewById(R.id.ivEventPreview);
        MaterialButton btnPickEventImage = view.findViewById(R.id.btnPickEventImage);

        boolean isEdit = item != null;
        selectedEventImageUri = null;

        if (isEdit) {
            etTitle.setText(item.title);
            etDescription.setText(item.description);
            etDate.setText(item.eventDate);
            etTime.setText(item.eventTime);
            etLocation.setText(item.location);

            if (item.imageUri != null && !item.imageUri.trim().isEmpty()) {
                try {
                    selectedEventImageUri = Uri.parse(item.imageUri);
                    ivEventPreview.setImageURI(selectedEventImageUri);
                } catch (Exception e) {
                    ivEventPreview.setImageResource(R.drawable.logo_black);
                }
            } else {
                ivEventPreview.setImageResource(R.drawable.logo_black);
            }
        } else {
            ivEventPreview.setImageResource(R.drawable.logo_black);
        }

        btnPickEventImage.setOnClickListener(v -> pickEventImageLauncher.launch(new String[]{"image/*"}));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(isEdit ? "Edit Event" : "Add Event")
                .setView(view)
                .setPositiveButton(isEdit ? "Update" : "Add", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String eventDate = etDate.getText().toString().trim();
            String eventTime = etTime.getText().toString().trim();
            String location = etLocation.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                etTitle.setError("Enter title");
                return;
            }
            if (TextUtils.isEmpty(description)) {
                etDescription.setError("Enter description");
                return;
            }
            if (TextUtils.isEmpty(eventDate)) {
                etDate.setError("Enter event date");
                return;
            }
            if (TextUtils.isEmpty(eventTime)) {
                etTime.setError("Enter event time");
                return;
            }
            if (TextUtils.isEmpty(location)) {
                etLocation.setError("Enter location");
                return;
            }

            String imageUriString;
            if (selectedEventImageUri != null) {
                imageUriString = selectedEventImageUri.toString();
            } else if (isEdit) {
                imageUriString = item.imageUri;
            } else {
                imageUriString = null;
            }

            if (isEdit) {
                boolean success = db.updateEventAdmin(
                        item.id,
                        title,
                        description,
                        imageUriString,
                        eventDate,
                        eventTime,
                        location
                );

                if (success) {
                    Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
                    loadEvents();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                }

            } else {
                long res = db.addEventAdmin(
                        title,
                        description,
                        imageUriString,
                        eventDate,
                        eventTime,
                        location,
                        "ACTIVE"
                );

                if (res != -1) {
                    Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();
                    loadEvents();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Insert failed", Toast.LENGTH_SHORT).show();
                }
            }
        }));

        dialog.show();
    }

    @Override
    public void onEditEvent(EventItem item) {
        showEventDialog(item);
    }

    @Override
    public void onToggleEvent(EventItem item) {
        boolean ok = db.toggleEventStatusAdmin(item.id);
        if (ok) {
            Toast.makeText(this, "Event status updated", Toast.LENGTH_SHORT).show();
            loadEvents();
        } else {
            Toast.makeText(this, "Status update failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteEvent(EventItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean ok = db.deleteEventAdmin(item.id);
                    if (ok) {
                        Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
                        loadEvents();
                    } else {
                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}