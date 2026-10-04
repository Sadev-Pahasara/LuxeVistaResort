package com.example.luxevistaresort;

public class Booking {
    public final int id;
    public final String roomTitle;
    public final String checkIn;
    public final String checkOut;
    public final double totalPrice;
    public final String status;

    public Booking(int id, String roomTitle, String checkIn, String checkOut, double totalPrice, String status) {
        this.id = id;
        this.roomTitle = roomTitle;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalPrice = totalPrice;
        this.status = status;
    }
}