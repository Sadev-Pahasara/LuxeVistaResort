package com.example.luxevistaresort;

public class EventItem {
    public int id;
    public String title;
    public String description;
    public String imageUri;
    public String eventDate;
    public String eventTime;
    public String location;
    public String status;

    public EventItem(int id, String title, String description,
                     String imageUri, String eventDate,
                     String eventTime, String location, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUri = imageUri;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.location = location;
        this.status = status;
    }
}