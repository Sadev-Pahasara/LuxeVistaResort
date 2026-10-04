package com.example.luxevistaresort;

public class OfferItem {
    public int id;
    public String title;
    public String description;
    public String discountText;
    public String imageUri;
    public String expiryDate;
    public String status;

    public OfferItem(int id, String title, String description,
                     String discountText, String imageUri,
                     String expiryDate, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.discountText = discountText;
        this.imageUri = imageUri;
        this.expiryDate = expiryDate;
        this.status = status;
    }
}