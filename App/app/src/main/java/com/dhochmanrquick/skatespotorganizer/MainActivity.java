package com.dhochmanrquick.skatespotorganizer;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.dhochmanrquick.skatespotorganizer.data.SpotSuggestion;
import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        SpotMasterFragment.OnListFragmentInteractionListener,
        MapFragment.OnFragmentInteractionListener,
        RatedSpotsFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        AdvancedSearchDialogFragment.OnAdvancedSearchResult {

    // Constants
    private static final int CURRENT_FRAGMENT_MAP = 1;
    private static final int CURRENT_FRAGMENT_LIST = 2;
    private static final int CURRENT_FRAGMENT_RATED = 3;
    private static final String PREVIOUS_FRAGMENT = "PREVIOUS_FRAGMENT";
    private static final String PREVIOUS_CAMERA_POSITION = "PREVIOUS_CAMERA_POSITION";
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private SpotViewModel mSpotViewModel;
    private List<Spot> mSpots;
    private FragmentManager mFragmentManager;
    private DrawerLayout mDrawerLayout;
    //    private NavigationView mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private FloatingActionButton mFAB_add_spot;
    private Fragment mCurrentFragment;
    private MapFragment mMapFragment;
    private int mCurrentFragmentType;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
//    private GoogleApiClient mGoogleApiClient;
    private GeoDataClient mGeoDataClient;
    private AutoCompleteTextView mAutoCompleteTextView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            Fragment selectedFragment = null;
            // Match and start fragment which corresponds to bottom navigation touch event
            switch (item.getItemId()) {
                case R.id.bottom_navigation_map:
                    // Todo: Is a new Fragment created every time? Are the old Fragments properly destroyed?
//                    mCurrentFragment = MapFragment.newInstance();
                    mCurrentFragment = mMapFragment;
                    mMapFragment.setCameraPosition(mMapFragment.getCameraPosition());
                    mCurrentFragmentType = CURRENT_FRAGMENT_MAP;
                    break;
                case R.id.bottom_navigation_list:
                    mCurrentFragment = SpotMasterFragment.newInstance();
//                    mCurrentFragment = SpotMasterFragment.newInstance(1); // The argument is the columnCount
//                    mCurrentFragment = SpotMasterFragment.newInstance(2);
                    mCurrentFragmentType = CURRENT_FRAGMENT_LIST;
                    break;
//                case R.id.navigation_notifications:
//                    mCurrentFragment = RatedSpotsFragment.newInstance();
//                    mCurrentFragmentType = CURRENT_FRAGMENT_RATED;
//                    break;
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

//        mAutoCompleteTextView = findViewById(R.id.advanced_search_AutoCompleteTextView);
//        getResources().getLayout(R.layout.dialogfragment_advanced_search).get;

//        final LayoutInflater factory = getLayoutInflater();
//        final View textEntryView = factory.inflate(R.layout.dialogfragment_advanced_search, null);
//        mAutoCompleteTextView = textEntryView.findViewById(R.id.advanced_search_AutoCompleteTextView);
//
//////        mGoogleApiClient = new GoogleApiClient
//////                .Builder(this)
//////                .addApi(Places.GEO_DATA_API)
//////                .addApi(Places.PLACE_DETECTION_API)
//////                .enableAutoManage(this, this)
//////                .build();
//
//        // Construct a GeoDataClient.
//        mGeoDataClient = Places.getGeoDataClient(this, null);
//        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, LAT_LNG_BOUNDS, null);
//        mAutoCompleteTextView.setAdapter(mPlaceAutocompleteAdapter);

        // Get BottomNavigationView widget nested inside activity_main.xml
        CurvedBottomNavigation navigation = findViewById(R.id.navigation);
        navigation.inflateMenu(R.menu.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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

        // If this Activity is being recreated (after being killed), restore the previously loaded fragment
        if (savedInstanceState != null) {
            switch (savedInstanceState.getInt(PREVIOUS_FRAGMENT)) {
                case CURRENT_FRAGMENT_LIST:
                    mCurrentFragment = SpotMasterFragment.newInstance();
//                    mCurrentFragment = SpotMasterFragment.newInstance(1);
//                    mCurrentFragment = SpotMasterFragment.newInstance(2);
                    mCurrentFragmentType = CURRENT_FRAGMENT_LIST;
                    break;
//                case CURRENT_FRAGMENT_RATED:
//                    mCurrentFragment = RatedSpotsFragment.newInstance();
//                    mCurrentFragmentType = CURRENT_FRAGMENT_RATED;
//                    break;
                default: // case CURRENT_FRAGMENT_MAP
                    mCurrentFragmentType = CURRENT_FRAGMENT_MAP;
                    mMapFragment = MapFragment.newInstance();
                    mCurrentFragment = mMapFragment;
//                    mCurrentFragment.setArguments(PREVIOUS_CAMERA_POSITION, savedInstanceState);
                    ((MapFragment) mCurrentFragment).setCameraPosition((CameraPosition) savedInstanceState.getParcelable(PREVIOUS_CAMERA_POSITION));
                    break;
            }
        } else { // Activity is being launched for first time
            // Load MapFragment as starting fragment
            mCurrentFragmentType = CURRENT_FRAGMENT_MAP;
            mMapFragment = MapFragment.newInstance();
            mCurrentFragment = mMapFragment;
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

//        mFAB_add_spot = findViewById(R.id.add_spot_fab);
//        mFAB_add_spot.setTransitionName("reveal");
//        mFAB_add_spot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getBaseContext(), NewSpotActivity.class);
//                startActivity(intent);
//            }
//        });

        // Get DrawerLayout widget nested inside activity_main.xml
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Display back/up button in action bar

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

        mSpotViewModel.getAllSpots().observe(this, new Observer<List<Spot>>() {
            @Override
            public void onChanged(@Nullable List<Spot> spots) {
                mSpots = spots; // Cache spot list locally for use in other methods; whenever the
                // underlying data changes, the spot list will be refreshed
            }
        });

        // Configure the FloatingSearchView
        final FloatingSearchView mSearchView = findViewById(R.id.floating_search_view);
        mSearchView.attachNavigationDrawerToMenuButton(mDrawerLayout);
//        mSearchView.menu

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {
                    //get suggestions based on newQuery
                    List<SpotSuggestion> spotSuggestions = new ArrayList<>();

                    // Iterate through the spot list; add any spot whose name contains the query
                    // string to the spotSuggestions list to be swapped into the SearchView
                    for (Spot spot : mSpots) {
                        if (spot.getName().toLowerCase().contains(newQuery.toLowerCase())) {
                            spotSuggestions.add(new SpotSuggestion(spot.getName()));
                        }
                    }

                    //pass them on to the search view
                    mSearchView.swapSuggestions(spotSuggestions);
                }
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
//                Toast.makeText(getBaseContext(), "You've clicked: " + searchSuggestion.getBody(), Toast.LENGTH_LONG).show();
                if (mCurrentFragment instanceof SpotMasterFragment) {

                } else if (mCurrentFragment instanceof MapFragment) {
                    ((MapFragment) mCurrentFragment).handleSearchQuery(searchSuggestion.getBody());
                }

            }

            @Override
            public void onSearchAction(String currentQuery) {
                Toast.makeText(getBaseContext(), "Displaying query results for " + currentQuery, Toast.LENGTH_LONG).show();

                if (mCurrentFragment instanceof MapFragment) {
                    // Your activity can call methods in the fragment by acquiring a reference to the
                    // Fragment from FragmentManager, using findFragmentById() or findFragmentByTag().
                    ((MapFragment) mCurrentFragment).handleSearchQuery(currentQuery);
                } else if (mCurrentFragment instanceof SpotMasterFragment) {
//                    Toast.makeText(getBaseContext(), "Displaying query results for " + currentQuery, Toast.LENGTH_LONG).show();
                }
            }
        });

        mSearchView.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                Toast.makeText(getBaseContext(), "onMenuOpened has been called", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMenuClosed() {
                Toast.makeText(getBaseContext(), "onMenuClosed has been called", Toast.LENGTH_SHORT).show();
            }
        });

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                if (mActionBarDrawerToggle.onOptionsItemSelected(item)) ; // What is this? Remove?
                switch (item.getItemId()) {
                    case R.id.options_menu_tune_ic:

//                        Intent intent = new Intent(getBaseContext(), AdvancedSearchDialogFragment.class);
//                        startActivity(intent);
                        AdvancedSearchDialogFragment advancedSearchActivity = new AdvancedSearchDialogFragment();
                        advancedSearchActivity.show(getSupportFragmentManager(), "Advanced search dialog");

//                        final LayoutInflater factory = getLayoutInflater();
//                        final View textEntryView = factory.inflate(R.layout.dialogfragment_advanced_search, null);
//                        mAutoCompleteTextView = textEntryView.findViewById(R.id.advanced_search_AutoCompleteTextView);

////        mGoogleApiClient = new GoogleApiClient
////                .Builder(this)
////                .addApi(Places.GEO_DATA_API)
////                .addApi(Places.PLACE_DETECTION_API)
////                .enableAutoManage(this, this)
////                .build();

                        // Construct a GeoDataClient.
//                        mGeoDataClient = Places.getGeoDataClient(MainActivity.this, null);
//                        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(MainActivity.this, mGeoDataClient, LAT_LNG_BOUNDS, null);
//                        mAutoCompleteTextView.setAdapter(mPlaceAutocompleteAdapter);
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                        builder.setTitle("Advanced search");
//                        builder.setView(R.layout.dialogfragment_advanced_search);
//                        AlertDialog dialog = builder.create();
//                        dialog.show();
                        break;

//                        case R.id.create_new_spot_menu:
//                            Intent intent = new Intent(getBaseContext(), NewSpotActivity.class);
//                            startActivity(intent);
                }
            }
        });
    }
//    /**
//     * If the current activity is not the searchable activity, then the normal activity lifecycle
//     * events are triggered once the user executes a handleSearchQuery (the current activity receives onPause()
//     * and so forth, as described in the Activities document). If, however, the current activity is
//     * the searchable activity, then one of two things happens:
//     * <p>
//     * 1). By default, the searchable activity receives the ACTION_SEARCH intent with a call to
//     * onCreate() and a new instance of the activity is brought to the top of the activity stack.
//     * There are now two instances of your searchable activity in the activity stack (so pressing
//     * the Back button goes back to the previous instance of the searchable activity, rather than
//     * exiting the searchable activity).
//     * <p>
//     * 2). If you set android:launchMode to "singleTop", then the searchable activity receives the
//     * ACTION_SEARCH intent with a call to onNewIntent(Intent), passing the new ACTION_SEARCH intent here.
//     * <p>
//     * Case 2 applies to our app; this activity is declared as the Searchable Activity and set to
//     * launch in singleTop mode.
//     *
//     * @param intent The Intent that triggered this activity to be relaunched in singleTop mode.
//     *               Generally, this should be the ACTION_SEARCH Intent.
//     */
//    @Override
//    protected void onNewIntent(Intent intent) {
//        // Handle a search query
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            if (mCurrentFragment instanceof MapFragment) {
//                // Your activity can call methods in the fragment by acquiring a reference to the
//                // Fragment from FragmentManager, using findFragmentById() or findFragmentByTag().
//                ((MapFragment) mCurrentFragment).handleSearchQuery(query);
//            } else if (mCurrentFragment instanceof SpotMasterFragment) {
//                Toast.makeText(this, "Displaying query results for " + query, Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current state
        if (mCurrentFragment instanceof MapFragment) {
            // Your activity can call methods in the fragment by acquiring a reference to the
            // Fragment from FragmentManager, using findFragmentById() or findFragmentByTag().
            savedInstanceState.putInt(PREVIOUS_FRAGMENT, mCurrentFragmentType); // Save which fragment type was loaded
            // Get and save the MapFragment's current CameraPosition (this is necessary to restore its state)
            savedInstanceState.putParcelable(PREVIOUS_CAMERA_POSITION, ((MapFragment) mCurrentFragment).getCameraPosition());
        } else if (mCurrentFragment instanceof SpotMasterFragment) {

        }
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


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu options from the res/menu/menu_catalog.xml file.
//        // This adds menu items to the app bar.
//        getMenuInflater().inflate(R.menu.options_menu, menu);
//
//        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        // The call to getSearchableInfo() obtains a SearchableInfo object that is created from the
//        // searchable configuration XML file. When the searchable configuration is correctly associated
//        // with your SearchView, the SearchView starts an activity with the ACTION_SEARCH intent when
//        // a user submits a query. Assumes current activity is the searchable activity.
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
//        searchView.setSubmitButtonEnabled(true);
//
//        return true;
//    }

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
//            case R.id.options_menu_tune_ic:
//                Toast.makeText(this, "Tune clicked", Toast.LENGTH_SHORT).show();
//            case R.id.create_new_spot_menu:
//                Intent intent = new Intent(getBaseContext(), NewSpotActivity.class);
//                startActivity(intent);
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.drawer_view_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
//                Toast.makeText(this, "Settings has been selected", Toast.LENGTH_LONG).show();
                break;
        }
        // Close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START); // What is GravityCompat.START?
        return false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // This is an interface method, but it seems we don't need it in the end
    }

    /**
     * An interface method declared in AdvancedSearchDialogFragment. MainActivity implements
     * this interface, AdvancedSearchDialogFragment.OnAdvancedSearchResult, so that
     * AdvancedSearchDialogFragment can send its results to MainActivity.
     *
     * @param searchResult_LatLng
     */
    @Override
    public void sendAdvancedSearchResult(LatLng searchResult_LatLng, int radius) {
        if (mCurrentFragment instanceof MapFragment) {
            ((MapFragment) mCurrentFragment).displayAdvancedSearchResult(searchResult_LatLng, radius);

//                    CameraPosition.fromLatLngZoom(searchResult, 18));
//            ((MapFragment) mCurrentFragment).setCameraPosition(CameraPosition.fromLatLngZoom(searchResult, 18));
        } else if (mCurrentFragment instanceof SpotMasterFragment) {

        }
    }
}