package com.dhochmanrquick.skatespotorganizer.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.dhochmanrquick.skatespotorganizer.Spot;

@Database(entities = {Spot.class}, version = 1)
public abstract class SpotRoomDatabase extends RoomDatabase {

    // Define the DAOs that work with the database. Provide an abstract "getter" method for each @Dao.
    public abstract SpotDao getSpotDao();

    private static SpotRoomDatabase INSTANCE;

    // Make the SpotRoomDatabase a singleton to prevent having multiple instances of the database opened at the same time.
    public static SpotRoomDatabase getDatabase(final Context context) {
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
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
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

            // Create list of dummy Spots and populate it
            for (int i = 0; i < 20; i++) {
                Spot spot = new Spot("Spot " + i,
//                    new LatLng(new Random().nextDouble(), new Random().nextDouble()),
//                    Spot.Type.GAP,
                        "Amazing gap spot" + i);
                mDao.insert(spot);
            }

            return null;
        }
    }
}
