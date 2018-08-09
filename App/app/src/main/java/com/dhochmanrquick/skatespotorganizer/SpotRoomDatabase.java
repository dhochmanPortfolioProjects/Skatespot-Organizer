package com.dhochmanrquick.skatespotorganizer;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Spot.class}, version = 1)
public abstract class SpotRoomDatabase extends RoomDatabase {

    // Define the DAOs that work with the database. Provide an abstract "getter" method for each @Dao.
    public abstract SpotDao getSpotDao();

    private static SpotRoomDatabase INSTANCE;

    // Make the SpotRoomDatabase a singleton to prevent having multiple instances of the database opened at the same time.
    static SpotRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SpotRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SpotRoomDatabase.class, "spot_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
