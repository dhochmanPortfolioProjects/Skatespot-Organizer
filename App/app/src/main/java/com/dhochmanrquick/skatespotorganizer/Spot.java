package com.dhochmanrquick.skatespotorganizer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
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
//    private LatLng mLatLng;
//    private Type mType;

    @NonNull
    @ColumnInfo(name = "description")
    private String mDescription;
//    private ImageView mImage;

    public Spot() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Spot(String name, /*LatLng latLng, Type type,*/ String description /*ImageView image*/) {
//        mId = UUID.randomUUID();
        mName = name;
//        mLatLng = latLng;
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

//    public LatLng getLatLng() {
//        return mLatLng;
//    }
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

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setName(String name) {
        mName = name;
    }
}
