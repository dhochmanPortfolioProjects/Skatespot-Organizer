package com.dhochmanrquick.skatespotorganizer.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

/**
 * A class to represent and store information about skatespots.
 * <p>
 * A <code>Spot</code> is a place known to be good for skating. Each <code>spot</code> consists of:
 * <p><ol>
 * <li> A spot name
 * <li> Longitude, latitude coordinates
 * <li> A short description of the skatespot (i.e., what obstacles are available to skate, etc.).
 * </ol></p>
 * <p>
 * This class is also marked as a Room @Entity; it is made into a table in an SQLite database.
 *
 * @author Daniel Hochman
 * @author Rob Quick
 */
@Entity(tableName = "spot_table")
public class Spot {

//    public enum Type{
//        LEDGE, STAIRS, GAP, HANDRAIL
//    }

    /* Fields *************************************************************************************/
    /**
     * A unique spot ID, auto-generated by Room and assigned to each <code>spot</code>. Although
     * there are getter and setter methods for this field, they should only be used by Room;
     * an ID should be immutable once it has been generated and assigned. Currently, the data type
     * is int, because Room only works with primitive data types, so it is unable to handle UUID,
     * which would be otherwise used.
     */
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int mId;
//    private UUID mId;

    /**
     * The name of a spot. Examples of spot names are:
     * <p><ul>
     * <li>Singil (the name of the location)
     * <li>Brooklyn Banks (a name consisting of the location and main obstacle)
     * </ul></p>
     */
    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    // Without @Embedded annotation, error: Cannot figure out how to save this field into database.
    // You can consider adding a type converter for it.
//    @Embedded
//    private LatLng mLatLng;

    @NonNull
    @ColumnInfo(name = "longitude")
    private double mLongitude;

    @NonNull
    @ColumnInfo(name = "latitude")
    private double mLatitude;

    /**
     * A short description of a skatespot.
     * <p>
     * Example:
     * <p>
     * A spot with several good ledges. One is a little lower than knee height and has ends on
     * both sides. One is an outledge that starts at the same height but goes out over 3 stairs.
     * One is a waist-tall ledge.
     * </p>
     */
    @NonNull
    @ColumnInfo(name = "description")
    private String mDescription;

    // Todo: Integrate these fields into Room and the rest of the app
    //    private Type mType;
    //    private ImageView mImage;

    /* END Fields *********************************************************************************/


    /* Constructors *******************************************************************************/

    /**
     * Default, empty constructor required for calls to DataSnapshot.getValue(User.class) used by the
     * Google Maps API.
     */
    public Spot() {

    }

    /**
     * Constructor to create a new <code>Spot</code> object.
     * <p>
     * Each <code>Spot</code> requires, at a minimum, a name, latitude-longitude coordinates,
     * and a description.
     *
     * @param name        a string containing the name of a <code>Spot</code> (i.e., "Singil",
     *                    "Empire Ledges", "Beer Banks")
     * @param latLng      the geographic coordinates
     * @param description a string containing a brief description of the <code>Spot</code> (i.e.,
     *                    "A spot with several good ledges. One is a little lower than knee height
     *                    and has ends on both sides. One is an outledge that starts at the same
     *                    height but goes out over 3 stairs. One is a waist-tall ledge."
     */
    @Ignore
    public Spot(String name, LatLng latLng, /*double longitude, double latitude,*/ /*, Type type,*/ String description /*ImageView image*/)
    throws IllegalArgumentException {
//        mId = UUID.randomUUID();
        if (name.isEmpty()){
            throw new IllegalArgumentException();
        }
        mName = name;
        mLatitude = latLng.latitude;
        mLongitude = latLng.longitude;
//        mLatLng = latLng;
//        mLongitude = longitude;
//        mLatitude = latitude;
        mDescription = description;
//        mType = type;
//        mImage = image;
    }
    /* END Constructors ***************************************************************************/


    /* Getter methods *****************************************************************************/

    /**
     *
     * @return  the unique spot ID
     */
    public int getId() {
        return mId;
    }

    /**
     *
     * @return  the string containing the spot's name
     */
    public String getName() {
        return mName;
    }

    /**
     * This method is required by Room.
     *
     * @return  the double representing the longitude coordinate of the spot
     */
    public double getLongitude() {
        return mLongitude;
    }

    /**
     * This method is required by Room.
     *
     * @return  the double representing the latitude coordinate of the spot
     */
    public double getLatitude() {
        return mLatitude;
    }

    /**
     * Gets the latitude and longitude coordinates of the spot and returns them in a LatLng
     * object.
     *
     * @return  a LatLng object containing two doubles representing the latitude and longitude
     * coordinates of the spot
     */
    public LatLng getLatLng() {
        return new LatLng(mLatitude, mLongitude);
//        return mLatLng;
    }

    /**
     *
     * @return  the string containing the description of the spot
     */
    public String getDescription() {
        return mDescription;
    }

    // Todo: Implement
//    public Type getType() {
//        return mType;
//    }
    /* END Getter methods *************************************************************************/


    /* Setter methods *****************************************************************************/

    /**
     * This method should only be used by Room; an ID, once auto-generated and assigned to a
     * <code>Spot</code>, should be immutable. However, Room requires that each field have a public
     * setter.
     *
     * @param id    an int representing the spot ID number
     */
    public void setId(@NonNull int id) {
        mId = id;
    }

    /**
     * Sets the nName field to the String value passed in.
     * <p>
     * The intended use of this method is to allow the user to edit the name of a <code>Spot</code>
     * after creation.
     *
     * @param name  a string containing the spot name
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Sets the mLongitude field.
     * <p>
     * This method is mainly just a requirement of Room. However, it could also be used to allow
     * a user to modify the location of the <code>Spot</code>.
     *
     * @param longitude a double representing representing the longitude coordinate of the spot
     */
    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    /**
     * Sets the mLatitude field.
     * <p>
     * This method is mainly just a requirement of Room. However, it could also be used to allow
     * a user to modify the location of the <code>Spot</code>.
     *
     * @param latitude a double representing representing the latitude coordinate of the spot
     */
    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    /**
     * Sets the latitude and longitude fields by extracting those values from the LatLng object
     * passed in as an argument.
     * <p>
     *     This is mainly just a convenience method.
     *
     * @param latLng    a LatLng object representing the geographical location of a spot
     */
    public void setLatLng(LatLng latLng) {
        mLatitude = latLng.latitude;
        mLongitude = latLng.longitude;
//        mLatLng = latLng;
    }

    /**
     *
     * @param description a string description of the spot
     */
    public void setDescription(String description) {
        mDescription = description;
    }
    /* END Setter methods *************************************************************************/
}