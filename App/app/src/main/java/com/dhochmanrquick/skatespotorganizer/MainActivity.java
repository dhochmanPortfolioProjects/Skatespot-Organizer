package com.dhochmanrquick.skatespotorganizer;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        SpotMasterFragment.OnListFragmentInteractionListener,
        MapFragment.OnFragmentInteractionListener,
        RatedSpotsFragment.OnFragmentInteractionListener {

    // Constants
    private static final int CURRENT_FRAGMENT_MAP = 1;
    private static final int CURRENT_FRAGMENT_LIST = 2;
    private static final int CURRENT_FRAGMENT_RATED = 3;
    private static final String PREVIOUS_FRAGMENT = "PREVIOUS_FRAGMENT";

    private SpotViewModel mSpotViewModel;
    private FragmentManager mFragmentManager;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private Fragment mCurrentFragment;
    private int mCurrentFragmentType;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            Fragment selectedFragment = null;
            // Match and start fragment which corresponds to bottom navigation touch event
            switch (item.getItemId()) {
                case R.id.bottom_navigation_map:
                    // Todo: Is a new Fragment created every time? Are the old Fragments properly destroyed?
                    mCurrentFragment = MapFragment.newInstance();
                    mCurrentFragmentType = CURRENT_FRAGMENT_MAP;
                    break;
                case R.id.bottom_navigation_list:
                    mCurrentFragment = SpotMasterFragment.newInstance(1);
                    mCurrentFragmentType = CURRENT_FRAGMENT_LIST;
                    break;
                case R.id.navigation_notifications:
                    mCurrentFragment = RatedSpotsFragment.newInstance();
                    mCurrentFragmentType = CURRENT_FRAGMENT_RATED;
                    break;
            }
            // Sanity check; this should never be null because there should always be a fragment loaded
            if (mCurrentFragment != null) {
                mFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, mCurrentFragment)
//                        .addToBackStack(null)
                        .commit();
            }
            return true;
        }
    };

    /**
     * When you open an activity from an intent, the bundle of extras is delivered to the activity
     * both when the configuration changes and when the system restores the activity. If a piece of
     * UI state data, such as a search query, were passed in as an intent extra when the activity was
     * launched, you could use the extras bundle instead of the onSaveInstanceState() bundle.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Use ViewModelProviders to associate your ViewModel with your UI controller.
        // When the app first starts, the ViewModelProviders will create the ViewModel.
        // When the activity is destroyed, for example through a configuration change,
        // the ViewModel persists. When the activity is re-created,
        // the ViewModelProviders return the existing ViewModel.
        //
        // ViewModel objects are scoped to the Lifecycle passed to the ViewModelProvider when getting
        // the ViewModel. The ViewModel remains in memory until the Lifecycle it's scoped to goes away
        // permanently: in the case of an activity, when it finishes, while in the case of a fragment,
        // when it's detached.
        //
        // You usually request a ViewModel the first time the system calls an activity object's onCreate()
        // method. The system may call onCreate() several times throughout the life of an activity,
        // such as when a device screen is rotated. The ViewModel exists from when you first request
        // a ViewModel until the activity is finished and destroyed.
        //
        // This creates the ViewModel and stores it in the local variable mSpotViewModel.
        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);


        if (savedInstanceState != null) {
            switch (savedInstanceState.getInt(PREVIOUS_FRAGMENT)) {
                case CURRENT_FRAGMENT_LIST:
                    mCurrentFragment = SpotMasterFragment.newInstance(1);
                    mCurrentFragmentType = CURRENT_FRAGMENT_LIST;
                    break;
                case CURRENT_FRAGMENT_RATED:
                    mCurrentFragment = RatedSpotsFragment.newInstance();
                    mCurrentFragmentType = CURRENT_FRAGMENT_RATED;
                    break;
                default:
                    mCurrentFragmentType = CURRENT_FRAGMENT_MAP;
                    mCurrentFragment = MapFragment.newInstance();
                    break;
            }
        } else {
            // Load MapFragment as starting fragment
            mCurrentFragmentType = CURRENT_FRAGMENT_MAP;
            mCurrentFragment = MapFragment.newInstance();
        }
//        mCurrentFragment = MapFragment.newInstance();
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mCurrentFragment) // Replace whatever fragment (if any) is currently in the layout container
                // Before you call commit(), however, you might want to call addToBackStack(), in
                // order to add the transaction to a back stack of fragment transactions. This back
                // stack is managed by the activity and allows the user to return to the previous
                // fragment state, by pressing the Back button.
                //
                // By calling addToBackStack(), the replace
                // transaction is saved to the back stack so the user can reverse the transaction and
                // bring back the previous fragment by pressing the Back button.
                //
                // If you don't call addToBackStack() when you perform a transaction that removes a
                // fragment, then that fragment is destroyed when the transaction is committed and
                // the user cannot navigate back to it. Whereas, if you do call addToBackStack()
                // when removing a fragment, then the fragment is stopped and is later resumed if
                // the user navigates back.
//                .addToBackStack(null)
                .commit();

        // An observer for the LiveData returned by getAllWords().
        // The onChanged() method fires when the observed data changes and the activity is in the foreground.
        // Whenever the data changes, the onChanged() callback is invoked, which calls the adapter's setWord()
        // method to update the adapter's cached data and refresh the displayed list.
//        mSpotViewModel.getAllSpots().observe(this, new Observer<List<Spot>>() {
//            @Override
//            public void onChanged(@Nullable /*final*/ List<Spot> spots) {
//                if (mCurrentFragment instanceof MapFragment) {
//                    ((MapFragment) mCurrentFragment).updateUI(spots);
//                } else if (mCurrentFragment instanceof SpotMasterFragment) {
//                    ((SpotMasterFragment) mCurrentFragment).updateUI(spots);
//                }
//            }
//        });

        // Get BottomNavigationView widget nested inside activity_main.xml
        BottomNavigationView navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Get DrawerLayout widget nested inside activity_main.xml
        mDrawerLayout = findViewById(R.id.drawer_layout);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Display back/up button in action bar
    }

    /**
     * If the current activity is not the searchable activity, then the normal activity lifecycle
     * events are triggered once the user executes a handleSearchQuery (the current activity receives onPause()
     * and so forth, as described in the Activities document). If, however, the current activity is
     * the searchable activity, then one of two things happens:
     * <p>
     * 1). By default, the searchable activity receives the ACTION_SEARCH intent with a call to
     * onCreate() and a new instance of the activity is brought to the top of the activity stack.
     * There are now two instances of your searchable activity in the activity stack (so pressing
     * the Back button goes back to the previous instance of the searchable activity, rather than
     * exiting the searchable activity).
     * <p>
     * 2). If you set android:launchMode to "singleTop", then the searchable activity receives the
     * ACTION_SEARCH intent with a call to onNewIntent(Intent), passing the new ACTION_SEARCH intent here.
     * <p>
     * Case 2 applies to our app; this activity is declared as the Searchable Activity and set to
     * launch in singleTop mode.
     *
     * @param intent The Intent that triggered this activity to be relaunched in singleTop mode.
     *               Generally, this should be the ACTION_SEARCH Intent.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        // Handle a search query
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (mCurrentFragment instanceof MapFragment) {
                // Your activity can call methods in the fragment by acquiring a reference to the
                // Fragment from FragmentManager, using findFragmentById() or findFragmentByTag().
                ((MapFragment) mCurrentFragment).handleSearchQuery(query);
            } else if (mCurrentFragment instanceof SpotMasterFragment) {
                Toast.makeText(this, "Displaying query results for " + query, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current state
        savedInstanceState.putInt(PREVIOUS_FRAGMENT, mCurrentFragmentType); // Save which fragment type was loaded

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * The callback for the list items displayed by the SpotMasterFragment.
     * <p>
     * This is called from SpotMasterRecyclerViewAdapter when a list item is clicked. This method
     * receives the Spot that has been clicked on.
     * <p>
     * Is this also the callback for the MapFragment and RatedSpotsFragment?
     *
     * @param item
     */
    @Override
    public void onListFragmentInteraction(Spot item) {
//        Toast.makeText(this,
//                "Spot " + item.getName() + " ID " + item.getId() + " has been selected",
//                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getBaseContext(), SpotDetailActivity.class);
        intent.putExtra("com.dhochmanrquick.skatespotorganizer", item.getId());
        startActivity(intent);
    }

    @Override
//    public void onFragmentInteraction(Uri uri) {
    public void onFragmentInteraction(int spotId) {
        Intent intent = new Intent(getBaseContext(), SpotDetailActivity.class);
        intent.putExtra("com.dhochmanrquick.skatespotorganizer", spotId);
        startActivity(intent);
    }

//    public void onFragmentInteraction(LatLng spotPosition) {
//        Intent intent = new Intent(getBaseContext(), SpotDetailActivity.class);
//        intent.putExtra("com.dhochmanrquick.skatespotorganizer", spotPosition);
//        startActivity(intent);
//        // not sure if we'll need this interface, but let's keep it for now.
//    }

    @Override
    public void onFragmentInteraction(Uri uri) {
//    public void onFragmentInteraction(int spotId) {
        // not sure if we'll need this interface, but let's keep it for now.
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.options_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // The call to getSearchableInfo() obtains a SearchableInfo object that is created from the
        // searchable configuration XML file. When the searchable configuration is correctly associated
        // with your SearchView, the SearchView starts an activity with the ACTION_SEARCH intent when
        // a user submits a query. Assumes current activity is the searchable activity.
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true);

        return true;
    }

    /**
     * Called when any item from the action bar is clicked.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId()) {
            case R.id.create_new_spot_menu:
                Intent intent = new Intent(getBaseContext(), NewSpotActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}