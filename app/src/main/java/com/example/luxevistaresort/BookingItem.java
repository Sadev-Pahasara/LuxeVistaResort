package com.example.luxevistaresort;

public class BookingItem {
    public String id;
    public String guest;
    public String info;
    public String status;

    public BookingItem(String id, String guest, String info, String status) {
        this.id = id;
        this.guest = guest;
        this.info = info;
        this.status = status;
    }
}