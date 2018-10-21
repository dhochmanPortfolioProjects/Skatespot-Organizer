package com.dhochmanrquick.skatespotorganizer.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.io.File;
import java.util.List;

/**
 * ViewModel: Provides data to the UI. Acts as a communication center between the Repository and the UI.
 * Hides where the data originates from the UI. ViewModel instances survive configuration changes.
 *
 * The ViewModel's role is to provide data to the UI and survive configuration changes.
 * A ViewModel acts as a communication center between the Repository and the UI. You can also use a
 * ViewModel to share data between fragments. The ViewModel is part of the lifecycle library.
 *
 * A ViewModel holds your app's UI data in a lifecycle-conscious way that survives configuration changes.
 * Separating your app's UI data from your Activity and Fragment classes lets you better follow the single
 * responsibility principle: Your activities and fragments are responsible for drawing data to the screen,
 * while your ViewModel can take care of holding and processing all the data needed for the UI.
 * The Repository and the UI are completely separated by the ViewModel. There are no database calls
 * from the ViewModel, making the code more testable.
 *
 * LiveData: A data holder class that can be observed. Always holds/caches latest version of data.
 * Notifies its observers when the data has changed. LiveData is lifecycle aware.
 * UI components just observe relevant data and don't stop or resume observation.
 * LiveData automatically manages all of this since it's aware of the relevant lifecycle status
 * changes while observing.
 *
 * If the ViewModel needs the Application context, for example to find a system service, it can
 * extend the AndroidViewModel class and have a constructor that receives the Application in the
 * constructor, since Application class extends Context.
 *
 * Caution: A ViewModel must never reference a view, Lifecycle, or any class that may a reference to
 * the activity context
 *
 * A ViewModel is associated with an activity (or some other lifecycle owner) - it stays in memory
 * during a configuration change and the system automatically associates the ViewModel with the new
 * activity instance that results from the configuration change. ViewModels are automatically
 * destroyed by the system when your user backs out of your activity or fragment or if you call
 * finish(), which means the state will be cleared as the user expects in these scenarios.
 *
 * @author Daniel Hochman
 * @author Rob Quick
 */
public class SpotViewModel extends AndroidViewModel {
    /**
     * Private member variable to hold a reference to the repository
     */
    private SpotRepository mRepository;

    Application mApplication;

    /**
     * Private LiveData member variable to cache the list of spots, which it gets from the SpotRepository
     * in the constructor
     */
    private LiveData<List<Spot>> mAllSpots;

    /**
     * Constructor that gets a reference to the repository and gets the list of spots from the repository.
     * This is called via ViewModelProviders in order to associate the ViewModel with the UI controller.
     * When the app first starts, the ViewModelProviders will create the ViewModel. When the activity is
     * destroyed, for example through a configuration change, the ViewModel persists. When the activity
     * is re-created, the ViewModelProviders return the existing ViewModel.
     *
     * @param application
     */
    public SpotViewModel (Application application) {
        super(application);
        mApplication = application;
        mRepository = new SpotRepository(application); // Construct a repository
        mAllSpots = mRepository.getAllSpots(); // Get the list of Spots from the repository
    }

    public LiveData<Spot> getSpot(int id) {
        return mRepository.getSpot(id);
    }

    public LiveData<Spot> getSpot(String name) {
        return mRepository.getSpot(name);
    }

    /**
     * A "getter" method for all the spots. Indirectly, this wraps the repository's getAllSpots() method
     * (which in turn wraps the DAO's getAll(). This completely hides the implementation from the UI.
     *
     * @return  The List of all the Spots, wrapped in LiveData
     */
    public LiveData<List<Spot>> getAllSpots() { return mAllSpots; }

    /**
     * A wrapper insert() method that calls the Repository's insert() method (which in turn calls the
     * DAO's insert() method). In this way, the implementation of insert() is completely hidden from the UI.
     *
     * @param spot  The Spot to be inserted into the database
     */
    public void insert(Spot spot) { mRepository.insert(spot); }

//    public File getPhotoFile(Spot spot) {
//        File filesDir = mApplication.getFilesDir();
//        return new File(filesDir, spot.getPhotoFilename());
//    }

    /**
     * A wrapper deleteSpots() method that calls the Repository's deleteSpots() method (which in turn calls the
     * DAO's deleteSpots() method). In this way, the implementation of deleteSpots() is completely hidden from the UI.
     *
     * @param spots The Spot(s) to be deleted from the database
     */
    public void deleteSpots(Spot... spots) { mRepository.deleteSpots(spots); }

    /**
     * A wrapper updateSpots() method that calls the Repository's updateSpots() method (which in turn calls the
     * DAO's updateSpots() method). In this way, the implementation of updateSpots() is completely hidden from the UI.
     *
     * @param spots The Spot(s) to be updated in the database
     */
    public void updateSpots(Spot... spots) { mRepository.updateSpots(spots); }
}
