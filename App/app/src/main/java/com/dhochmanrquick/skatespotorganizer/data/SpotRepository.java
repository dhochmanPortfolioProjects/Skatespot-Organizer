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
 * <p>
 * A Repository is a class that abstracts access to multiple data sources. The Repository is not part
 * of the Architecture Components libraries, but is a suggested best practice for code separation and
 * architecture. A Repository class handles data operations. It provides a clean API to the rest of
 * the app for app data.
 *
 * @author Daniel Hochman
 * @author Rob Quick
 */
public class SpotRepository {

    private SpotDao mSpotDao; // Member variable for the DAO
    private LiveData<List<Spot>> mAllSpots; // Member variable for the list of Spots. This caches the list of Spots.

    /**
     * Constructor that gets a handle to the database and initializes the member variables.
     *
     * @param application   The Application object passed in by the ViewModel when the SpotRepository
     *                      is instantiated. This object is needed in SpotRoomDatabase
     *                      (Room.databaseBuilder(context.getApplicationContext()) to create the
     *                      database in this Application Context.
     */
    public SpotRepository(Application application) {
        // Get singleton instance of the database and through it get the DAO for the Spot database
        mSpotDao = SpotRoomDatabase.getDatabase(application).getSpotDao();
        mAllSpots = mSpotDao.getAll(); // Use the DAO to query the SQLite database and return the LiveData
        // This could be called in the method getAllSpots() when the spot list is actually needed
    }

    /**
     * A overloaded wrapper for getSpot() defined in the DAO.
     *
     * @param id ID of the Spot to be retrieved from the database
     * @return The requested Spot or null if the Spot is not found
     */
    public LiveData<Spot> getSpot(int id) {
        return mSpotDao.getSpot(id);
    }

    /**
     * An overloaded wrapper for getSpot() defined in the DAO.
     *
     * @param name Name of the Spot to be retrieved from the database
     * @return The requested Spot or null if the Spot is not found
     */
    public LiveData<Spot> getSpot(String name) {
        return mSpotDao.getSpot(name);
    }

    /**
     * A wrapper for getAllSpots() defined in the DAO; effectively a getter method for the mAllSpots
     * member variable.
     *
     * @return The List of all Spots in the database, wrapped in LiveData
     */
    public LiveData<List<Spot>> getAllSpots() {
        return mAllSpots;
    }

    /**
     * A wrapper for the insert() method defined in the DAO. Must call this on a non-UI thread or your
     * app will crash; Room ensures that you don't do any long-running operations on the main thread,
     * blocking the UI.
     *
     * @param spot The Spot to be inserted into the database
     */
    public void insert(Spot spot) {
        // Create new insertAsyncTask and pass in the DAO to its constructor
        new insertAsyncTask(mSpotDao).execute(spot);
    }

    /**
     * Private inner AsyncTask class to insert a Spot into the database, which must be done on a
     * non-UI thread or your app will crash; Room ensures that you don't do any long-running
     * operations on the main thread, blocking the UI.
     */
    private static class insertAsyncTask extends AsyncTask<Spot, Void, Void> {

        private SpotDao mAsyncTaskDao; // Member variable to hold the DAO (passed into the constructor)

        // Constructor: Stores the DAO passed in into the local variable mAsyncTaskDao
        insertAsyncTask(SpotDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Spot... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    public void deleteSpots(Spot... spots) { new deleteSpotsAsyncTask(mSpotDao).execute(spots); }

    /**
     * Private inner AsyncTask class to insert a Spot into the database, which must be done on a
     * non-UI thread or your app will crash; Room ensures that you don't do any long-running
     * operations on the main thread, blocking the UI.
     */
    private static class deleteSpotsAsyncTask extends AsyncTask<Spot, Void, Void> {

        private SpotDao mAsyncTaskDao; // Member variable to hold the DAO (passed into the constructor)

        // Constructor: Stores the DAO passed in into the local variable mAsyncTaskDao
        deleteSpotsAsyncTask(SpotDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Spot... params) {
            mAsyncTaskDao.deleteSpots(params[0]);
            return null;
        }
    }

    public void updateSpots(Spot... spots) { new updateSpotsAsyncTask(mSpotDao).execute(spots); }

    /**
     * Private inner AsyncTask class to insert a Spot into the database, which must be done on a
     * non-UI thread or your app will crash; Room ensures that you don't do any long-running
     * operations on the main thread, blocking the UI.
     */
    private static class updateSpotsAsyncTask extends AsyncTask<Spot, Void, Void> {

        private SpotDao mAsyncTaskDao; // Member variable to hold the DAO (passed into the constructor)

        // Constructor: Stores the DAO passed in into the local variable mAsyncTaskDao
        updateSpotsAsyncTask(SpotDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Spot... params) {
            mAsyncTaskDao.updateSpots(params[0]);
            return null;
        }
    }

}
