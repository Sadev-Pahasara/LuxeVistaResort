package com.example.luxevistaresort;

public class AdminBookingItem {

    public final long bookingId;
    public final String username;
    public final String guestName;
    public final String roomTitle;
    public final String checkIn;
    public final String checkOut;
    public final double totalPrice;
    public final String status;

    public AdminBookingItem(long bookingId,
                            String username,
                            String guestName,
                            String roomTitle,
                            String checkIn,
                            String checkOut,
                            double totalPrice,
                            String status) {

        this.bookingId = bookingId;
        this.username = username;
        this.guestName = guestName;
        this.roomTitle = roomTitle;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalPrice = totalPrice;
        this.status = status;
    }
}