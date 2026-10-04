package com.example.luxevistaresort;

import java.util.HashMap;
import java.util.Map;

public class AddonDetailsData {

    public static class AddonDetails {
        public final String title;
        public final String price;
        public final int imageRes;
        public final String description;
        public final String keyFeatures;

        public AddonDetails(String title, String price, int imageRes, String description, String keyFeatures) {
            this.title = title;
            this.price = price;
            this.imageRes = imageRes;
            this.description = description;
            this.keyFeatures = keyFeatures;
        }
    }

    private static final Map<String, AddonDetails> DATA = new HashMap<>();

    static {
        DATA.put("spa", new AddonDetails(
                "Spa & Wellness", "$60",
                R.drawable.addon_spa,
                "Relax your body and mind with professional spa treatments, soothing massages, and holistic wellness therapies.",
                "• Full-body massage\n• Aromatherapy and hot stone therapy\n• Steam room and sauna\n• Relaxation lounge\n• Yoga and meditation sessions"
        ));

        DATA.put("nature", new AddonDetails(
                "Nature & Cultural Experiences", "$50",
                R.drawable.addon_nature,
                "Explore local traditions and natural beauty through guided tours and cultural experiences.",
                "• Guided village tours\n• Cultural workshops\n• Waterfall visits\n• Scenic hikes\n• Local heritage experience"
        ));

        DATA.put("evening", new AddonDetails(
                "Evening Entertainment", "$50",
                R.drawable.addon_evening,
                "Unwind with live music and performances in a vibrant seaside atmosphere.",
                "• Live music\n• Cultural performances\n• Outdoor lounge ambiance\n• Themed nights\n• Beachfront entertainment"
        ));

        DATA.put("kids", new AddonDetails(
                "Kids Activities", "$50",
                R.drawable.addon_kids,
                "Fun and safe activities for children including beach games and supervised programs.",
                "• Kids play zone\n• Beach games and activities\n• Creative arts and crafts\n• Supervised kids club\n• Child-friendly facilities"
        ));

        DATA.put("dining", new AddonDetails(
                "Dining Experiences", "$50",
                R.drawable.addon_dining,
                "Indulge in gourmet cuisine and special dining moments in unforgettable settings.",
                "• Fine dining restaurant\n• Beachfront candlelight dinner\n• International cuisine options\n• Personalized chef experience\n• Premium beverage selection"
        ));

        DATA.put("beach", new AddonDetails(
                "Beach Activities", "$50",
                R.drawable.addon_beach,
                "Enjoy thrilling water activities and guided coastal adventures.",
                "• Kayaking and paddle boarding\n• Snorkeling sessions\n• Guided beach walks\n• Water sports packages\n• Safety equipment provided"
        ));

        DATA.put("fitness", new AddonDetails(
                "Fitness Center", "Free",
                R.drawable.addon_fitness,
                "Stay active with modern gym equipment in a spacious, well-equipped environment.",
                "• Modern cardio & strength machines\n• Personal training options\n• Air-conditioned gym\n• Locker and shower facilities\n• Stretching area"
        ));

        DATA.put("cabanas", new AddonDetails(
                        "Poolside Cabanas", "$60",
                        R.drawable.addon_cabanas,
                        "Relax in private poolside cabanas with comfortable seating and a peaceful luxury atmosphere.",
                        "• Private shaded cabanas\n• Comfortable lounge seating\n• Refreshing beverages\n• Butler service\n• Scenic poolside view"
                )
        );
    }

    public static AddonDetails get(String id) {
        return DATA.get(id);
    }
}
