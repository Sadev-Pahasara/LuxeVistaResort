package com.example.luxevistaresort;

import java.util.ArrayList;

public class AdminRoom {
    public int id;

    public String name;
    public String price;
    public String description;

    public int imageRes;          // fallback drawable
    public String imageUri;       // gallery / file uri (can be null)

    public ArrayList<String> features;

    // ✅ Needed for toggle availability feature
    public String status; // "AVAILABLE" or "UNAVAILABLE"

    // ✅ Empty constructor (prevents errors if you ever used: new AdminRoom())
    public AdminRoom() {
        this.features = new ArrayList<>();
        this.status = "AVAILABLE";
    }

    // ✅ main constructor used by DBHelper
    public AdminRoom(int id, String name, String price, String description,
                     int imageRes, String imageUri, ArrayList<String> features) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageRes = imageRes;
        this.imageUri = imageUri;
        this.features = (features != null) ? features : new ArrayList<>();
        this.status = "AVAILABLE";
    }

    // ✅ Optional: constructor that includes status too (if you want)
    public AdminRoom(int id, String name, String price, String description,
                     int imageRes, String imageUri, ArrayList<String> features, String status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageRes = imageRes;
        this.imageUri = imageUri;
        this.features = (features != null) ? features : new ArrayList<>();
        this.status = (status != null) ? status : "AVAILABLE";
    }
}