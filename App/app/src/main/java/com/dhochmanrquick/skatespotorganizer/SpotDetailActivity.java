package com.dhochmanrquick.skatespotorganizer;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;

import java.util.List;

public class SpotDetailActivity extends AppCompatActivity {

    private SpotViewModel mSpotViewModel;
//    private LiveData<Spot> spot; // Since the Spot is only accessed and manipulated in onChanged,
    // do we need this member variable?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_detail);

        int id = getIntent().getIntExtra("com.dhochmanrquick.skatespotorganizer", 0);

        // Get the ViewModel
        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);
        mSpotViewModel.getSpot(id).observe(this, new Observer<Spot>() {
            @Override
            public void onChanged(@Nullable Spot spot) { // Should this be final?
                ((TextView) findViewById(R.id.spot_detail_title_tv)).setText("Spot name: " + spot.getName()
                + "\nSpot id: " + spot.getId());
            }
        });

//        mSpotViewModel.getAllSpots().observe(this, new Observer<List<Spot>>() {
//            @Override
//            public void onChanged(@Nullable final List<Spot> words) {
//                // Update the cached copy of the words in the adapter.
//                adapter.setWords(words);
//            }
//        });
//
//        Toast.makeText(this, "Spot ID number " + spot.getName() + " has been selected", Toast.LENGTH_SHORT)
//                .show();

//        ((TextView) findViewById(R.id.spot_detail_title_tv)).setText("Spot ID number " + id);
    }
}
