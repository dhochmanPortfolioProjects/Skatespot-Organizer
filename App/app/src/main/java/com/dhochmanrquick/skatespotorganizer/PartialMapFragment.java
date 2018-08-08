package com.dhochmanrquick.skatespotorganizer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dhochmanrquick.skatespotorganizer.dummy.DummyContent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PartialMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PartialMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PartialMapFragment extends Fragment implements
        OnMapReadyCallback,
        /*GoogleMap.OnPoiClickListener,*/
        GoogleMap.OnMarkerClickListener {

    private final static int MY_LOCATION_REQUEST_CODE = 0;

    private OnFragmentInteractionListener mListener;

    public PartialMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PartialMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PartialMapFragment newInstance() {
        return new PartialMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_partial_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MapView mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this); // when you already implement OnMapReadyCallback in your fragment
        super.onViewCreated(view, savedInstanceState);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used. (This seems like the
     * constructor/onCreate() for the map).
     * <p>
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Within your UI, a map will be represented by either a MapFragment or MapView object.

        // Sets the map type to be "hybrid"
//        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        GoogleMapOptions options = new GoogleMapOptions();
        options.ambientEnabled(true);

        // Enable the My Location layer on the map
        //mMap.setMyLocationEnabled(true); // Works with pre-API 23 permissions model

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);
//        } else {
//            // Show rationale and request permission.
//            Toast.makeText(this, "You must grant permission in order to see spots near your current location.", Toast.LENGTH_LONG).show();
//        }

        googleMap.setPadding(0, 0, 0, 100);

        // Respond to a user tapping on a POI
//        mMap.setOnPoiClickListener(this);

        // Set a listener for marker click.
        googleMap.setOnMarkerClickListener(this);

        // Create dummy spots
        Spot bulgwangLedge_Spot = new Spot(
                "Bulgwang Downledge Spot",
                new LatLng(37.61595, 126.92478),
                Spot.Type.LEDGE,
                "Small marble downledge.");

        Spot pajuLedge_Spot = new Spot(
                "Paju Ledge Spot",
                new LatLng(37.707672, 126.747231),
                Spot.Type.LEDGE,
                "3 perfect marble ledges in a row. Nice flat ground.");

        // Save spots to the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference();
//
//        DatabaseReference messagesRef = myRef.child("spots").child("" + bulgwangLedge_Spot.getId());
//        messagesRef.setValue(bulgwangLedge_Spot);


//        messagesRef = myRef.child("spots").child("" + pajuLedge_Spot.getId());
//        messagesRef.setValue(pajuLedge_Spot);

        // Read from the database
//        messagesRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                Log.d(/*TAG,*/"", "Value is: " + value);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(/*TAG,*/ "", "Failed to read value.", error.toException());
//            }
//        });

        DummyContent dummyContent = DummyContent.get(getActivity());
        List<Spot> spots = dummyContent.getSpots();
        Spot spot1 = spots.get(0);
        Spot spot2 = spots.get(1);

        // Add a marker in Sydney and move the camera
        Marker marker1 = googleMap.addMarker(new MarkerOptions()
                .position(bulgwangLedge_Spot.getLatLng())
                .title(bulgwangLedge_Spot.getName())
                .snippet(bulgwangLedge_Spot.getDescription()));
        marker1.setTag(0);

        Marker marker2 = googleMap.addMarker(new MarkerOptions()
                .position(pajuLedge_Spot.getLatLng())
                .title(pajuLedge_Spot.getName())
                .snippet(pajuLedge_Spot.getDescription()));
        marker2.setTag(1);

        Marker marker3 = googleMap.addMarker(new MarkerOptions()
                .position(spot1.getLatLng())
                .title(spot1.getName())
                .snippet(spot1.getDescription()));

//        marker1.showInfoWindow();
//        marker2.showInfoWindow();
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(bulgwangLedge_Spot.getLatLng())); // Set camera position to Marker
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

//        Intent spot_Intent = new Intent(this, SpotDetailActivity.class);
//        spot_Intent.setData(marker.getTag());
//        spot_Intent.putExtra("Spot", (Integer) (marker.getTag()));
//        startActivity(spot_Intent);

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(getContext(),
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
}