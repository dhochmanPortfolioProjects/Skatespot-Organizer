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
 * The Room database class for the Spot SQLite table. Room is a database layer on top of SQLite database
 * that takes care of mundane tasks that you used to handle
 * with an SQLiteOpenHelper. Database holder that serves as an access point to the underlying SQLite
 * database. The Room database uses the DAO to issue queries to the SQLite database. Your Room class
 * must be abstract and extend RoomDatabase. Usually, you only need one instance of the Room database
 * for the whole app.
 *
 * @author Daniel Hochman
 * @author Rob Quick
 */
@Database(entities = {Spot.class}, version = 1) // Annotate the class to be a Room database,
// declare the entities that belong in the database and set the version number. Listing the entities will create tables in the database.
public abstract class SpotRoomDatabase extends RoomDatabase {

    // Define the DAOs that work with the database. Provide an abstract "getter" method for each @Dao.
    // So far, the only DAO needed is SpotDao. This method returns that DAO.
    public abstract SpotDao getSpotDao();

    private static SpotRoomDatabase INSTANCE; // The Singleton instance of this SpotRoomDatabase

    private static Context mContext;

    // A static (factory) method that returns the singleton instance of the database. The SpotRoomDatabase
    // is a singleton to prevent having multiple instances of the database opened at the same time.
    // This method is called by SpotRepository's constructor
    public static SpotRoomDatabase getDatabase(final Context context) {
        mContext = context;
        if (INSTANCE == null) { // If no instance of the database exists yet, create it and return it
            synchronized (SpotRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here: This code uses Room's database builder to create a
                    // RoomDatabase object in the application context from the SpotRoomDatabase
                    // class and names it "spot_database".
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SpotRoomDatabase.class, "spot_database")
                            .addCallback(sRoomDatabaseCallback) // add the callback to the database build sequence right before calling .build().
                            .build();
                }
            }
        }
        return INSTANCE; // Else; return the already instantiated singleton SpotRoomDatabase object
    }

    // A RoomDatabase.Callback overriding onOpen() to delete all content and repopulate the database
    // whenever the app is started. (Because you cannot do Room database operations on the UI thread,
    // onOpen() creates and executes an AsyncTask to add content to the database).
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final SpotDao mDao;

        PopulateDbAsync(SpotRoomDatabase db) {
            mDao = db.getSpotDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mDao.deleteAll();

            // Create list of randomly located dummy Spots and populate it
//            for (int i = 0; i < 3; i++) {
//                Spot spot = new Spot("Spot " + i,
//                    new LatLng(new Random().nextDouble() * 200 % 180, new Random().nextDouble() * 100 % 90),
////                        new Random().nextDouble() * 200 % 180,
////                        new Random().nextDouble() * 100 % 90,
////                    Spot.Type.GAP,
//                        "Amazing gap spot" + i,
//                        (i % 2 == 0) ? R.drawable.dangsan_spot_landscape : R.drawable.paju_spot_landscape
////                        mContext.getResources().getDrawable(R.drawable.dangsan_spot_landscape)
//                );
//                mDao.insert(spot);
//            }

            // Prepopulate database with a few real spots
            Spot bulgwangLedge_Spot = new Spot(
                    "Bulgwang Downledge Spot",
                    new LatLng(37.61595, 126.92478),
//                Spot.Type.LEDGE,
                    "Small marble downledge.",
                    R.drawable.bulgwang_spot_landscape);

            Spot pajuLedge_Spot = new Spot(
                    "Paju Ledge Spot",
                    new LatLng(37.707672, 126.747231),
//                Spot.Type.LEDGE,
                    "3 perfect marble ledges in a row. Nice flat ground.",
                    R.drawable.paju_spot_landscape);

            Spot dangsan_Spot = new Spot(
                    "Dangsan Ledge Spot",
                    new LatLng(37.532908, 126.903882),
                    "A few curved ledges. One skinny ledge that looks fun.",
                    R.drawable.dangsan_spot_landscape);

            mDao.insert(bulgwangLedge_Spot);
            mDao.insert(pajuLedge_Spot);
            mDao.insert(dangsan_Spot);

            return null;
        }
    }

    // When using the database debug library, this method must be overriden; otherwise
    // error: SpotRoomDatabase_Impl is not abstract and does not override abstract method clearAllTables() in RoomDatabase
    @Override
    public void clearAllTables() {
        // TODO: 8/14/18 Implement method
    }
}