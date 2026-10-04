package com.example.luxevistaresort;

public class AdminServiceItem {
    public final int id;              // a_id
    public final int iconRes;         // drawable from image_name
    public final String title;        // a_name
    public final String desc;         // description
    public final String features;     // key_features
    public final double price;        // price
    public final String category;     // category
    public final String status;       // ACTIVE / INACTIVE

    public AdminServiceItem(int id,
                            int iconRes,
                            String title,
                            String desc,
                            String features,
                            double price,
                            String category,
                            String status) {
        this.id = id;
        this.iconRes = iconRes;
        this.title = title;
        this.desc = desc;
        this.features = features;
        this.price = price;
        this.category = category;
        this.status = status;
    }
}