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


@Database(entities = {Spot.class}, version = 1)
public abstract class SpotRoomDatabase extends RoomDatabase {

    // Define the DAOs that work with the database. Provide an abstract "getter" method for each @Dao.
    public abstract SpotDao getSpotDao();

    private static SpotRoomDatabase INSTANCE;

    private static Context mContext;

    // Make the SpotRoomDatabase a singleton to prevent having multiple instances of the database opened at the same time.
    public static SpotRoomDatabase getDatabase(final Context context) {
        mContext = context;
        if (INSTANCE == null) {
            synchronized (SpotRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SpotRoomDatabase.class, "spot_database")
                            .addCallback(sRoomDatabaseCallback) // add the callback to the database build sequence right before calling .build().
                            .build();
                }
            }
        }
        return INSTANCE;
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