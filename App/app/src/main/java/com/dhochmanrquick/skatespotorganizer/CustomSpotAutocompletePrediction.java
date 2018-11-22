package com.dhochmanrquick.skatespotorganizer;

import android.support.annotation.Nullable;
import android.text.style.CharacterStyle;

import com.google.android.gms.location.places.AutocompletePrediction;

import java.util.List;

public class CustomSpotAutocompletePrediction implements AutocompletePrediction {

    private int mSpotId;
    private String mSpotName;

    public CustomSpotAutocompletePrediction(int spotId, String spotName) {
        mSpotId = spotId;
        mSpotName = spotName;
    }

    @Override
    public CharSequence getFullText(@Nullable CharacterStyle characterStyle) {
//        return null;
        return mSpotName;
    }

    @Override
    public CharSequence getPrimaryText(@Nullable CharacterStyle characterStyle) {
//        return null;
        return mSpotName;
    }

    @Override
    public CharSequence getSecondaryText(@Nullable CharacterStyle characterStyle) {
//        return null;
        return mSpotName;
    }

    @Nullable
    @Override
    public String getPlaceId() {
//        return null;
        return String.valueOf(mSpotId);
    }

    @Nullable
    @Override
    public List<Integer> getPlaceTypes() {
        return null;
    }

    @Override
    public AutocompletePrediction freeze() {
        return null;
    }

    @Override
    public boolean isDataValid() {
        return false;
    }
}
