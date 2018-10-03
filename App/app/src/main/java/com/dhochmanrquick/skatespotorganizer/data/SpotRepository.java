package com.dhochmanrquick.skatespotorganizer.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

/**
 * A class that you create, for example using the SpotRepository class.
 * You use the Repository for managing multiple data sources.
 * This class mainly wraps the SQL-mapped Java methods defined in the DAO; it is effectively an
 * abstract DAO.
 *
 * A Repository is a class that abstracts access to multiple data sources. The Repository is not part
 * of the Architecture Components libraries, but is a suggested best practice for code separation and
 * architecture. A Repository class handles data operations. It provides a clean API to the rest of
 * the app for app data.
 *
 *
 * @author Daniel Hochman
 * @author Rob Quick
 */
public class SpotRepository {

    private SpotDao mSpotDao; // Member variable for the DAO
    private LiveData<List<Spot>> mAllSpots; // Member variable for the list of Spots. This caches the list of Spots.

    // Constructor that gets a handle to the database and initializes the member variables.
    public SpotRepository(Application application) {
        SpotRoomDatabase db = SpotRoomDatabase.getDatabase(application); // Get singleton instance of the database
        mSpotDao = db.getSpotDao(); // Get the Dao for the database
        mAllSpots = mSpotDao.getAll(); // Use the Dao to query the SQLite database and return the LiveData
    }

    // A wrapper for getSpot() defined in the DAO
    public LiveData<Spot> getSpot(int id) {
        return mSpotDao.getSpot(id);
    }

    // A wrapper for getSpot() defined in the DAO
    public LiveData<Spot> getSpot(String name) {
        return mSpotDao.getSpot(name);
    }

    // A wrapper for getAllSpots() defined in the DAO; effectively a getter method for the mAllSpots member variable.
    public LiveData<List<Spot>> getAllSpots() {
        return mAllSpots;
    }

    // A wrapper for the insert() method defined in the DAO. Must call this on a non-UI thread or your app will crash;
    // Room ensures that you don't do any long-running operations on the main thread, blocking the UI.
    public void insert (Spot spot) {
        new insertAsyncTask(mSpotDao).execute(spot);
    }

    // Private inner class
    private static class insertAsyncTask extends AsyncTask<Spot, Void, Void> {

        private SpotDao mAsyncTaskDao; // Member variable to hold the DAO (passed into the constructor)

        // Constructor: Stores the DAO passed in in the local mAsyncTaskDao
        insertAsyncTask(SpotDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Spot... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
