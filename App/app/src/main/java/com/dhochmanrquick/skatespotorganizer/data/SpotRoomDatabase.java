package com.dhochmanrquick.skatespotorganizer.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.dhochmanrquick.skatespotorganizer.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.Random;

/**
 * The Room database class for the Spot SQLite table. This class's main purpose is to build the
 * Room database (via Room.databaseBuilder()) and define and return the DAOs that work with the
 * database (via getSpotDao()).
 * <p>
 * From the documentation: Room is a database layer on top of an SQLite database that takes care of
 * mundane tasks that you used to handle with an SQLiteOpenHelper. RoomDatabase is a database holder
 * that serves as an access point to the underlying SQLite database. The Room database uses the DAO
 * to issue queries to the SQLite database. Your Room class must be abstract and extend RoomDatabase.
 * Usually, you only need one instance of the Room database for the whole app.
 *
 * @author Daniel Hochman
 * @author Rob Quick
 */
@Database(entities = {Spot.class}, version = 1) // Annotate the class to be a Room database,
// declare the entities that belong in the database and set the version number. Listing the entities
// will create tables in the database.
public abstract class SpotRoomDatabase extends RoomDatabase {

    // Define the DAOs that work with the database. Provide an abstract "getter" method for each @Dao.
    // So far, the only DAO needed is SpotDao. This method returns that DAO; its implementation
    // seems to be defined elsewhere by the Room library.
    public abstract SpotDao getSpotDao();

    private static SpotRoomDatabase sSpotRoomDatabase; // The singleton instance of this SpotRoomDatabase
//    private static Context mContext;

    // A static (factory) method that returns the singleton instance of the database. The SpotRoomDatabase
    // is a singleton to prevent having multiple instances of the database opened at the same time.
    // This method is called by SpotRepository's constructor, mainly so that it can retrieve the SpotDao from it.
    public static SpotRoomDatabase getDatabase(final Context context) {
//        mContext = context;
        if (sSpotRoomDatabase == null) { // If no instance of the database exists yet, create it and return it
            synchronized (SpotRoomDatabase.class) {
                if (sSpotRoomDatabase == null) {
                    // Build database here: This code uses Room's database builder to create a
                    // RoomDatabase object in the Application Context from the SpotRoomDatabase
                    // class, and names it "spot_database".
                    sSpotRoomDatabase = Room.databaseBuilder(context.getApplicationContext(), // Build database
                            SpotRoomDatabase.class, "spot_database")
                            // add the callback to the database build sequence right before calling .build().
//                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return sSpotRoomDatabase; // Else; return the already instantiated singleton SpotRoomDatabase object
    }

    // When using the database debug library, this method must be overridden; otherwise
    // error: SpotRoomDatabase_Impl is not abstract and does not override abstract method clearAllTables() in RoomDatabase
    @Override
    public void clearAllTables() {
        // TODO: 8/14/18 Implement method
    }

    // A RoomDatabase.Callback overriding onOpen() to delete all content and repopulate the database
    // whenever the app is started. (Because you cannot do Room database operations on the UI thread,
    // onOpen() creates and executes an AsyncTask to add content to the database).
//    private static RoomDatabase.Callback sRoomDatabaseCallback =
//            new RoomDatabase.Callback() {
//
//                @Override
//                public void onOpen(@NonNull SupportSQLiteDatabase db) {
//                    super.onOpen(db);
////                    new PopulateDbAsync(INSTANCE).execute();
//                }
//            };
//
//    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
//
//        private final SpotDao mDao;
//
//        PopulateDbAsync(SpotRoomDatabase db) {
//            mDao = db.getSpotDao();
//        }
//
//        @Override
//        protected Void doInBackground(final Void... params) {
//            mDao.deleteAll();
//
//            // Populate database with a few real spots
//            Spot bulgwangLedge_Spot = new Spot(
//                    "Bulgwang Downledge Spot",
//                    new LatLng(37.61595, 126.92478),
////                Spot.Type.LEDGE,
//                    "Small marble downledge.",
//                    R.drawable.bulgwang_spot_landscape);
//
//            Spot pajuLedge_Spot = new Spot(
//                    "Paju Ledge Spot",
//                    new LatLng(37.707672, 126.747231),
////                Spot.Type.LEDGE,
//                    "3 perfect marble ledges in a row. Nice flat ground.",
//                    R.drawable.paju_spot_landscape);
//
//            Spot dangsan_Spot = new Spot(
//                    "Dangsan Ledge Spot",
//                    new LatLng(37.532908, 126.903882),
//                    "A few curved ledges. One skinny ledge that looks fun.",
//                    R.drawable.dangsan_spot_landscape);
//
//            mDao.insert(bulgwangLedge_Spot);
//            mDao.insert(pajuLedge_Spot);
//            mDao.insert(dangsan_Spot);
//
//            return null;
//        }
//    }
}