package com.dhochmanrquick.skatespotorganizer.dummy;

import com.dhochmanrquick.skatespotorganizer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();


    private static final int COUNT = 5;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
    }

    private static DummyItem createDummyItem(int position) {

        if(position % 2 == 0) {
            return new DummyItem("River Side Pyramid", "Watch out for passing bikes!" , R.drawable.spot1_landscape);
        }else {
            return new DummyItem("Double Stacked", "Better have some quick feet!", R.drawable.spot2_landscape);
        }
    }


    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String spotName;
        public final String spotDescription;
        public final int spotImage;


        public DummyItem(String name, String description, int image) {
            this.spotName = name;
            this.spotDescription = description;
            this.spotImage = image;
        }

    }
}
