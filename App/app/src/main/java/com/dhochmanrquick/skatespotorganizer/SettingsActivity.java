package com.dhochmanrquick.skatespotorganizer;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;

import java.util.List;
import java.util.Observer;

public class SettingsActivity extends AppCompatActivity {

    private SpotViewModel mSpotViewModel;
    private List<Spot> mSpots;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get the ViewModel
        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);

        mSpotViewModel.getAllSpots().observe(this, new android.arch.lifecycle.Observer<List<Spot>>() {
            @Override
            public void onChanged(@Nullable List<Spot> spots) {
                mSpots = spots;
            }
        });

        // Build an alert dialog for when the user presses the up button before saving the Spot
        AlertDialog.Builder upButton_builder = new AlertDialog.Builder(this);
        upButton_builder.setTitle("Delete All Spots")
//                .setMessage("You have not saved your spot. Leave anyways?")
                .setMessage("Are you sure you want to delete all your spots?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        mSpotViewModel.deleteAll
                        for (Spot spot : mSpots) {
                            mSpotViewModel.deleteSpots(spot);
                        }
                        // Todo: Implement a better check for successful spot deletion
                        // Todo: Make a utility method for Spot deletion that also deletes the Spot's photos, etc.
                        if (mSpotViewModel.getAllSpots() == null) {
                        }
                        Toast.makeText(SettingsActivity.this, "All of your spots have been deleted.", Toast.LENGTH_LONG).show();
//                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        mAlertDialog = upButton_builder.create();

        findViewById(R.id.activity_settings_delete_all_spots_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.show();
            }
        });
    }
}
