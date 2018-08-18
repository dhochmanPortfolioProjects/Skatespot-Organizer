package com.dhochmanrquick.skatespotorganizer;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;
import com.google.android.gms.maps.model.LatLng;

public class NewSpotActivity extends AppCompatActivity {

    String spotName;
    double latitude;
    double longitude;
    String description;

    private SpotViewModel mSpotViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_spot);

        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);

        Button button = (Button) findViewById(R.id.new_spot_create_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spotName = ((EditText) findViewById(R.id.new_spot_name)).getText().toString();
                latitude = Double.parseDouble(((EditText) findViewById(R.id.new_spot_latitude)).getText().toString());
                longitude = Double.parseDouble(((EditText) findViewById(R.id.new_spot_longtitude)).getText().toString());
                description = ((EditText) findViewById(R.id.new_spot_description)).getText().toString();

                LatLng latLng = new LatLng(latitude, longitude);

                Spot newSpot = new Spot(spotName, latLng, description, R.drawable.dangsan_spot_landscape);

                mSpotViewModel.insert(newSpot);

                finish();
                }
        });
    }
}
