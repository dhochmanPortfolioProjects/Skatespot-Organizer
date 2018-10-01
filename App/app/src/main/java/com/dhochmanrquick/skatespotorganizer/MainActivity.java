package com.dhochmanrquick.skatespotorganizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dhochmanrquick.skatespotorganizer.data.Spot;

public class MainActivity extends AppCompatActivity implements
        SpotMasterFragment.OnListFragmentInteractionListener,
        MapFragment.OnFragmentInteractionListener,
        RatedSpotsFragment.OnFragmentInteractionListener {

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
    }

    @Override
    public void onListFragmentInteraction(Spot item) {
        Toast.makeText(this,
                "Spot number " + item + " has been selected",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
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