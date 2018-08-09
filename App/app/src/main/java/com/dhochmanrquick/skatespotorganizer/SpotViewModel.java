package com.dhochmanrquick.skatespotorganizer;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class SpotViewModel extends AndroidViewModel {

    private SpotRepository mRepository; // Private member variable to hold a reference to the repository

    // Private LiveData member variable to cache the list of spots, which it gets from the SpotRepository
    // in the constructor
    private LiveData<List<Spot>> mAllSpots;

    // Constructor that gets a reference to the repository and gets the list of spots from the repository
    public SpotViewModel (Application application) {
        super(application);
        mRepository = new SpotRepository(application);
        mAllSpots = mRepository.getAllSpots();
    }

    // A "getter" method for all the words. This completely hides the implementation from the UI.
    LiveData<List<Spot>> getAllSpots() { return mAllSpots; }

    // A wrapper insert() method that calls the Repository's insert() method.
    // In this way, the implementation of insert() is completely hidden from the UI.
    public void insert(Spot spot) { mRepository.insert(spot); }
}
