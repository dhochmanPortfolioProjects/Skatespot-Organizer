package com.dhochmanrquick.skatespotorganizer.dummy;

import android.content.Context;

import com.dhochmanrquick.skatespotorganizer.Spot;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public List<Spot> mSpots = new ArrayList<>();

    private static DummyContent sDummyContent; // Reference to singleton instance of this class

    public static DummyContent get(Context context) {
        // If the singleton instance of this class hasn't been created yet, create it
        if (sDummyContent == null) {
            sDummyContent = new DummyContent(context); // Internal constructor call
        }
        // Else; return the already existing object
        return sDummyContent;
    }

    // Private constructor; cannot be called by other classes
    private DummyContent(Context context) {
        // Create list of dummy Spots and populate it
        mSpots = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Spot spot = new Spot("Spot " + i,
//                    new LatLng(new Random().nextDouble(), new Random().nextDouble()),
//                    Spot.Type.GAP,
                    "Amazing gap spot" + i);
            mSpots.add(spot);
        }
    }

    public List<Spot> getSpots() {
        return mSpots;
    }

    public Spot getSpot(UUID id) {
        for (Spot spot : mSpots) {
            if (spot.getId().equals(id)) {
                return spot;
            }
        }
        return null;
    }

//    /**
//     * A map of sample (dummy) items, by ID.
//     */
//    public static final Map<String, Spot> ITEM_MAP = new HashMap<String, Spot>();
//
//    private static final int COUNT = 25;
//
//    static {
//        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createDummyItem(i));
//        }
//    }
//
//    private static void addItem(Spot item) {
//        mSpots.add(item);
////        ITEM_MAP.put(item.getId(), item);
//    }
//
//    private static Spot createDummyItem(int position) {
//        LatLng latLng = new LatLng(new Random().nextDouble(), new Random().nextDouble());
//
//        return new Spot(position, "Spot " + position, latLng, Spot.Type.GAP, "Great gap!");
//
//
////        return new Spot(String.valueOf(position), "Spot " + position, makeDetails(position));
//    }

//    private static String makeDetails(int position) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("Details about Item: ").append(position);
//        for (int i = 0; i < position; i++) {
//            builder.append("\nMore details information here.");
//        }
//        return builder.toString();
//    }

    /**
     * A dummy item representing a piece of content.
     */
//    public static class DummyItem {
//        public final String id;
//        public final String content;
//        public final String details;
//
//        public DummyItem(String id, String content, String details) {
//            this.id = id;
//            this.content = content;
//            this.details = details;
//        }
//
//        @Override
//        public String toString() {
//            return content;
//        }
//    }
}
