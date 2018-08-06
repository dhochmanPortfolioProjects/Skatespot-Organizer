package com.dhochmanrquick.skatespotorganizer;

import com.google.android.gms.maps.model.LatLng;

public class Spot {

    public enum Type{
        LEDGE, STAIRS, GAP, HANDRAIL
    }

    private int mId;
    private String mName;
    private LatLng mLatLng;
    private Type mType;
    private String mDescription;
//    private ImageView mImage;

    public Spot() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Spot(int id, String name, LatLng latLng, Type type, String description /*ImageView image*/) {
        mId = id;
        mName = name;
        mLatLng = latLng;
        mType = type;
        mDescription = description;
//        mImage = image;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public Type getType() {
        return mType;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setName(String name) {
        mName = name;
    }
}
