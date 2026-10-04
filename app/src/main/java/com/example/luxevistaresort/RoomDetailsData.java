package com.example.luxevistaresort;

public class RoomDetailsData {

    public static class RoomDetails {
        public final String title;
        public final String price;
        public final int imageRes;
        public final String description;

        public RoomDetails(String title, String price, int imageRes, String description) {
            this.title = title;
            this.price = price;
            this.imageRes = imageRes;
            this.description = description;
        }
    }

    public static RoomDetails get(int index) {
        // IMPORTANT: keep this same order as RoomBookingActivity rooms array
        switch (index) {
            case 0:
                return new RoomDetails(
                        "Standard Room",
                        "From $80",
                        R.drawable.room1,
                        "The Standard Room offers a comfortable and affordable stay, ideal for solo travelers or short visits. Designed with modern furnishings and warm tones, this room provides all essential amenities for a relaxing experience.\n\n" +
                                "Key Features:\n• Queen-size bed\n• Garden or city view\n• Free Wi-Fi\n• Air conditioning\n• Flat-screen TV\n• Mini bar"
                );
            case 1:
                return new RoomDetails(
                        "Deluxe Room",
                        "From $120",
                        R.drawable.room2,
                        "The Deluxe Room provides enhanced comfort with stylish interiors and additional space. Guests can enjoy a private balcony and relaxing views, making it a perfect choice for couples and leisure travelers.\n\n" +
                                "Key Features:\n• King-size bed\n• Private balcony\n• Pool/partial ocean view\n• Mini bar\n• Air conditioning\n• Coffee/tea maker"
                );
            case 2:
                return new RoomDetails(
                        "Executive Room",
                        "From $180",
                        R.drawable.room4,
                        "The Executive Room is designed for business and premium travelers who require both comfort and functionality. Featuring a dedicated work area and premium amenities, this room ensures productivity and relaxation during your stay.\n\n" +
                                "Key Features:\n• King-size bed\n• Dedicated work desk & ergonomic chair\n• High-speed Wi-Fi\n• Lounge/executive seating\n• Premium toiletries\n• Late check-out (subject to availability)"
                );
            case 3:
                return new RoomDetails(
                        "Ocean View Suite",
                        "From $220",
                        R.drawable.room5,
                        "The Ocean View Suite delivers a luxurious experience with breathtaking panoramic sea views. Guests can unwind in a spacious room featuring elegant décor and a private balcony overlooking the ocean.\n\n" +
                                "Key Features:\n• King-size bed\n• Full ocean view\n• Private balcony\n• Luxury bathroom with bathtub\n• Complimentary breakfast\n• Premium room service"
                );
            case 4:
                return new RoomDetails(
                        "Family Room",
                        "From $300",
                        R.drawable.room6,
                        "The Family Suite offers spacious accommodation tailored for families and groups. With separate living areas and additional sleeping space, this suite ensures comfort and convenience for everyone.\n\n" +
                                "Key Features:\n• Two queen beds or king + sofa bed\n• Separate living area\n• Child-friendly facilities\n• Large bathroom\n• Free Wi-Fi\n• Entertainment area"
                );
            default:
                return new RoomDetails(
                        "Presidential Villa",
                        "From $600",
                        R.drawable.room3,
                        "The Presidential Villa represents the pinnacle of luxury at LuxeVista Resort. Offering complete privacy, exclusive facilities, and premium services, this villa is ideal for VIP guests and special occasions.\n\n" +
                                "Key Features:\n• Private villa with living and dining areas\n• Private swimming pool\n• King-size bed\n• Butler service\n• Luxury bathroom\n• Exclusive outdoor lounge"
                );
        }
    }
}
