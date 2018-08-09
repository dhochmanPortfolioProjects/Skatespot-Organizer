package com.dhochmanrquick.skatespotorganizer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        SpotMasterFragment.OnListFragmentInteractionListener,
        PartialMapFragment.OnFragmentInteractionListener,
        RatedSpotsFragment.OnFragmentInteractionListener {

    private FragmentManager mFragmentManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = SpotMasterFragment.newInstance(1);
                    break;
                case R.id.navigation_dashboard:
                    selectedFragment = PartialMapFragment.newInstance();
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

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SpotMasterFragment.newInstance(1))
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
}
