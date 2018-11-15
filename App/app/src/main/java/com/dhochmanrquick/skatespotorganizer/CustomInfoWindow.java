package com.dhochmanrquick.skatespotorganizer;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindow(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_info_window, null);
        ImageView spot_image_iv = view.findViewById(R.id.spot_image_infoWindow);
        TextView spot_name_tv = view.findViewById(R.id.spot_name_infoWindow);
        TextView spot_name_test_tv = view.findViewById(R.id.spot_name_infoWindow_test);
        TextView spot_name_test1_tv = view.findViewById(R.id.spot_name_infoWindow_test1);


        Spot spot = (Spot) marker.getTag();

        String spotImagePath = spot.getPhotoFilepath1();

        if(spotImagePath != null) {
            Picasso.get()
                    .load(spotImagePath)
                    .resize(10, 10)
                    .centerCrop()
                    .into(spot_image_iv);
        }

        spot_name_tv.setText(spot.getName());
        spot_name_test_tv.setText(spot.getName());
        spot_name_test1_tv.setText(spot.getName());

        return view;
    }
}
