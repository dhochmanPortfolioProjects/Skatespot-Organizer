package com.dhochmanrquick.skatespotorganizer;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        SpotMasterFragment.OnListFragmentInteractionListener,
        MapFragment.OnFragmentInteractionListener,
        RatedSpotsFragment.OnFragmentInteractionListener {

//    private SpotViewModel mSpotViewModel;

    private FragmentManager mFragmentManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.bottom_navigation_map:
                    selectedFragment = MapFragment.newInstance();
                    break;
                case R.id.bottom_navigation_list:
                    selectedFragment = SpotMasterFragment.newInstance(1);
                    break;
                case R.id.navigation_notifications:
                    selectedFragment = RatedSpotsFragment.newInstance();
                    break;
            }
            mFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get BottomNavigationView widget nested inside activity_main.xml
        BottomNavigationView navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, SpotMasterFragment.newInstance(1))
                // Load MapFragment as starting fragment instead of SpotMasterFragment
                .replace(R.id.fragment_container, MapFragment.newInstance())
                .commit();


        // Use ViewModelProviders to associate your ViewModel with your UI controller.
        // When the app first starts, the ViewModelProviders will create the ViewModel.
        // When the activity is destroyed, for example through a configuration change,
        // the ViewModel persists. When the activity is re-created,
        // the ViewModelProviders return the existing ViewModel.
        //
        // This creates the ViewModel and stores it in the local variable mSpotViewModel.
//        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);

        // An observer for the LiveData returned by getAllWords().
        // The onChanged() method fires when the observed data changes and the activity is in the foreground.
        // Whenever the data changes, the onChanged() callback is invoked, which calls the adapter's setWord()
        // method to update the adapter's cached data and refresh the displayed list.
//        mSpotViewModel.getAllSpots().observe(this, new Observer<List<Spot>>() {
//            @Override
//            public void onChanged(@Nullable final List<Spot> words) {
//                // Update the cached copy of the words in the adapter.
//                adapter.setWords(words);
//            }
//        });
    }

    /**
     * The callback for the list items displayed by the SpotMasterFragment.
     *
     * This is called from SpotMasterRecyclerViewAdapter when a list item is clicked. This method
     * receives the Spot that has been clicked on.
     *
     * Is this also the callback for the MapFragment and RatedSpotsFragment?
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
        getMenuInflater().inflate(R.menu.overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_new_spot_menu:
                Intent intent = new Intent(getBaseContext(), NewSpotActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}