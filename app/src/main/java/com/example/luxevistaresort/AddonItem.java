package com.example.luxevistaresort;

public class AddonItem {
    public int id;
    public String title;
    public String desc;
    public double price;
    public int imageRes;
    public String featuresText;
    public boolean added;

    public AddonItem(int id, String title, String desc, double price, int imageRes, String featuresText) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.price = price;
        this.imageRes = imageRes;
        this.featuresText = featuresText;
        this.added = false;
    }
}