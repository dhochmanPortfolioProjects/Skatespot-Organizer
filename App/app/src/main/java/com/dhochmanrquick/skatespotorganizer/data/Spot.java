package com.dhochmanrquick.skatespotorganizer.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

@Entity(tableName = "spot_table")
public class Spot {

//    public enum Type{
//        LEDGE, STAIRS, GAP, HANDRAIL
//    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int mId;
//    private UUID mId;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

//    @Embedded
//    private LatLng mLatLng;
    @NonNull
    @ColumnInfo(name = "longitude")
    private double mLongitude;

    @NonNull
    @ColumnInfo(name = "latitude")
    private double mLatitude;

//    private Type mType;

    @NonNull
    @ColumnInfo(name = "description")
    private String mDescription;
//    private ImageView mImage;

//    @Ignore
    public Spot() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Spot(String name, double longitude, double latitude, /*, Type type,*/ String description /*ImageView image*/) {
//        mId = UUID.randomUUID();
        mName = name;
//        mLatLng = latLng;
        mLongitude = longitude;
        mLatitude = latitude;

//        mType = type;
        mDescription = description;
//        mImage = image;
    }

//    public UUID getId() {
//        return mId;
//    }
    public int getId() {
    return mId;
}

    public String getName() {
        return mName;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public LatLng getLatLng() {
        return new LatLng(mLatitude, mLongitude);
    }
//
//    public Type getType() {
//        return mType;
//    }

    public String getDescription() {
        return mDescription;
    }

    public void setId(@NonNull int id) {
        mId = id;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

}