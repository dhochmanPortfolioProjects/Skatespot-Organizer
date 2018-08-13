package com.dhochmanrquick.skatespotorganizer.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.dhochmanrquick.skatespotorganizer.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotDao;
import com.dhochmanrquick.skatespotorganizer.data.SpotRoomDatabase;

import java.util.List;

public class SpotRepository {
    // Member variables for the DAO and the list of Spots.
    private SpotDao mSpotDao;
    private LiveData<List<Spot>> mAllSpots; // This caches the list of Spots

    // Constructor that gets a handle to the database and initializes the member variables.
    public SpotRepository(Application application) {
        SpotRoomDatabase db = SpotRoomDatabase.getDatabase(application); // Get singleton instance of the database
        mSpotDao = db.getSpotDao(); // Get the Dao for the database
        mAllSpots = mSpotDao.getAll(); // Use the Dao to query the SQLite database and return the LiveData
    }

    // A wrapper for getAllSpots()
    public LiveData<List<Spot>> getAllSpots() {
        return mAllSpots;
    }

    // A wrapper for the insert() method. Must call this on a non-UI thread or your app will crash;
    // Room ensures that you don't do any long-running operations on the main thread, blocking the UI.
    public void insert (Spot word) {
        new insertAsyncTask(mSpotDao).execute(word);
    }

    private static class insertAsyncTask extends AsyncTask<Spot, Void, Void> {

        private SpotDao mAsyncTaskDao;

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
