package com.example.luxevistaresort;

import java.util.ArrayList;
import java.util.Arrays;

public class RoomItem {
    public int id;
    public String name;
    public String priceText;
    public String description;
    public int imageRes;

    public double basePrice;      // numeric price from DB
    public String keyFeatures;    // stored as text with lines

    public RoomItem(int id, String name, String priceText, String description,
                    int imageRes, double basePrice, String keyFeatures) {
        this.id = id;
        this.name = name;
        this.priceText = priceText;
        this.description = description;
        this.imageRes = imageRes;
        this.basePrice = basePrice;
        this.keyFeatures = keyFeatures;
    }

    public ArrayList<String> getFeaturesList() {
        if (keyFeatures == null) return new ArrayList<>();
        String cleaned = keyFeatures.replace("•", "").trim();
        String[] parts = cleaned.split("\n");
        return new ArrayList<>(Arrays.asList(parts));
    }
}