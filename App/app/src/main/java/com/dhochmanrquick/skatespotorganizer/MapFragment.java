package com.dhochmanrquick.skatespotorganizer;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;
import com.dhochmanrquick.skatespotorganizer.utils.PermissionUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        /*GoogleMap.OnPoiClickListener,*/
        GoogleMap.OnMarkerClickListener {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String EXTRA_CURRENT_LOCATION = "com.dhochmanrquick.skatespotorganizer.current_location";

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    private OnFragmentInteractionListener mListener;
    private SpotViewModel mSpotViewModel; // The ViewModel (the app's data)
    private GoogleMap mMap;
    private List<Spot> mSpotList;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Use ViewModelProviders to associate your ViewModel with your UI controller.
        // When the app first starts, the ViewModelProviders will create the ViewModel.
        // When the activity is destroyed, for example through a configuration change,
        // the ViewModel persists. When the activity is re-created,
        // the ViewModelProviders return the existing ViewModel.
        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);

        // Retrieve and inflate the content view that renders the map for this fragment
        return inflater.inflate(R.layout.fragment_partial_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        // 10.3.2018: Had to make view final in order to use it in the below OnClickListener

        // Within your UI, a map will be represented by either a MapFragment or MapView object.
        // Here, we use a MapView object. MapView, a subclass of the Android View class, allows you
        // to place a map in an Android View. A View represents a rectangular region of the screen,
        // and is a fundamental building block for Android applications and widgets. Much like a
        // MapFragment, the MapView acts as a container for the map, exposing core map functionality
        // through the GoogleMap object.
        // So, what is the real difference? Here, a MapView is used in a Fragment.
        MapView mapView = view.findViewById(R.id.map); // Get Map View from the inflated content view
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this); // when you already implement OnMapReadyCallback in your fragment

        super.onViewCreated(view, savedInstanceState);

        // Set OnClickListener for the search button: Get text from EditText, query the ViewModel
        // to for the Spot, if found, zoom in on the spot, if not, pop a toast.
        ((ImageButton) view.findViewById(R.id.search_ic)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText searchString_EditText = (EditText) view.findViewById(R.id.spot_search_bar);
                String searchString = searchString_EditText.getText().toString();
//                Toast.makeText(getContext(), "Searching for " + searchString, Toast.LENGTH_LONG).show();

                mSpotViewModel.getSpot(searchString).observe(getActivity(), new Observer<Spot>() {
                    @Override
                    public void onChanged(@Nullable Spot spot) {
//                        mMap.addMarker(new MarkerOptions()
////                            .position(new LatLng(spot.getLatitude(), spot.getLongitude()))
//                                .position(new LatLng(spot.getLatLng().latitude, spot.getLatLng().longitude))
//                                .title(spot.getName())
//                                .snippet(spot.getDescription()));
                        if (spot != null) {
//                            mMap.clear();
//                            mMap.addMarker(new MarkerOptions()
////                            .position(new LatLng(spot.getLatitude(), spot.getLongitude()))
//                                    .position(new LatLng(spot.getLatLng().latitude, spot.getLatLng().longitude))
//                                    .title(spot.getName())
//                                    .snippet(spot.getDescription()));

                            // Adding the circle to the GoogleMap
                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(spot.getLatLng().latitude, spot.getLatLng().longitude))
                                    .radius(10)
                                    .strokeColor(Color.BLACK) // Border color of the circle
                                    // Fill color of the circle.
                                    // 0x represents, this is an hexadecimal code
                                    // 55 represents percentage of transparency. For 100% transparency, specify 00.
                                    // For 0% transparency ( ie, opaque ) , specify ff
                                    // The remaining 6 characters(00ff00) specify the fill color
                                    .fillColor(0x8800ff00)
                                    // Border width of the circle
                                    .strokeWidth(2)); // Todo: Make this transparent blue?

                            // To change the position of the camera, you must specify where you want
                            // to move the camera, using a CameraUpdate. The Maps API allows you to
                            // create many different types of CameraUpdate using CameraUpdateFactory.
                            // Animate the move of the camera position to spot's coordinates and zoom in
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(spot.getLatLng(), 18)),
                                    2000, null);
                        } else {
                            Toast.makeText(getContext(), "Spot not found", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                // An observer for the LiveData returned by getAllWords().
                // The onChanged() method fires when the observed data changes and the activity is in the foreground.
//                mSpotViewModel.getSpot().observe(this, new Observer<List<Spot>>() {
//                    @Override
//                    public void onChanged(@Nullable final List<Spot> spots) {
//                        // Update the cached copy of the words in the adapter.
////                adapter.setWords(words);
//                        // Set markers:
//                        for (Spot spot: spots) {
////                    googleMap.addMarker(new MarkerOptions()
//                            mMap.addMarker(new MarkerOptions()
////                            .position(new LatLng(spot.getLatitude(), spot.getLongitude()))
//                                    .position(new LatLng(spot.getLatLng().latitude, spot.getLatLng().longitude))
//                                    .title(spot.getName())
//                                    .snippet(spot.getDescription()));
//                        }
//                    }
//                });
            }
        });
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
     * <p><
     * Within your UI, a map will be represented by either a MapFragment or MapView object.
     */
    @Override
//    public void onMapReady(GoogleMap googleMap) {
    public void onMapReady(GoogleMap googleMap) { // Declared final because LiveData Observer onChanged()
        mMap = googleMap;
        // Sets the map type to be "hybrid"
//        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // If you have added a MapFragment (or MapView) programmatically, then you can configure its
        // initial state by passing in a GoogleMapOptions object with your options specified.
        // The options available to you are exactly the same as those available via XML.
        // To apply these options when you are creating a map, do one of the following:
        //
        //If you are using a MapFragment, use the MapFragment.newInstance(GoogleMapOptions options)
        // static factory method to construct the fragment and pass in your custom configured options.
        //If you are using a MapView, use the MapView(Context, GoogleMapOptions) constructor and pass
        // in your custom configured options.
        // Todo: Should the GoogleMapOptions object be passed to the MapView constructor?
        GoogleMapOptions options = new GoogleMapOptions();
        options.ambientEnabled(true);

        // Enable the My Location layer on the map
        //mMap.setMyLocationEnabled(true); // Works with pre-API 23 permissions model

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
//            Toast.makeText(this, "You must grant permission in order to see spots near your current location.", Toast.LENGTH_LONG).show();
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(((MainActivity) getActivity()), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Access to the location has been granted to the app.
                mMap.setMyLocationEnabled(true);
                Toast.makeText(getContext(), "Location permission has been granted.", Toast.LENGTH_LONG).show();
            } else {
                // Permission was denied. Display an error message.
                Toast.makeText(getContext(), "You must grant permission in order to see spots near your current location.", Toast.LENGTH_LONG).show();
            }
        }

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Permission to access the location is missing.
//            PermissionUtils.requestPermission(getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
//                    Manifest.permission.ACCESS_FINE_LOCATION, true);
//        } else if (mMap != null) {
//            // Access to the location has been granted to the app.
//            mMap.setMyLocationEnabled(true);
//        }

        // public final void setPadding (int left, int top, int right, int bottom)
        googleMap.setPadding(0, 50, 0, 50);

        // Respond to a user tapping on a POI
//        mMap.setOnPoiClickListener(this);

        // Set a listener for marker click.
        googleMap.setOnMarkerClickListener(this);

        // Create dummy spots
//        Spot bulgwangLedge_Spot = new Spot(
//                "Bulgwang Downledge Spot",
////                new LatLng(37.61595, 126.92478),
//                37.61595,
//                126.92478,
////                Spot.Type.LEDGE,
//                "Small marble downledge.");
//
//        Spot pajuLedge_Spot = new Spot(
//                "Paju Ledge Spot",
////                new LatLng(37.707672, 126.747231),
//                126.747231,
//                37.707672,
////                Spot.Type.LEDGE,
//                "3 perfect marble ledges in a row. Nice flat ground.");

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

//        DummyContent dummyContent = DummyContent.get(getActivity());
//        List<Spot> spots = dummyContent.getSpots();
//        Spot spot1 = spots.get(0);
//        Spot spot2 = spots.get(1);

        // Add a marker in Sydney and move the camera
//        Marker marker1 = googleMap.addMarker(new MarkerOptions()
//                .position(bulgwangLedge_Spot.getLatLng())
//                .title(bulgwangLedge_Spot.getName())
//                .snippet(bulgwangLedge_Spot.getDescription()));
//        marker1.setTag(0);
//
//        Marker marker2 = googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(pajuLedge_Spot.getLatitude(), pajuLedge_Spot.getLongitude()))
//                .title(pajuLedge_Spot.getName())
//                .snippet(pajuLedge_Spot.getDescription()));
//        marker2.setTag(1);
//
//        Marker marker3 = googleMap.addMarker(new MarkerOptions()
//                .position(spot1.getLatLng())
//                .title(spot1.getName())
//                .snippet(spot1.getDescription()));

//        marker1.showInfoWindow();
//        marker2.showInfoWindow();

        // Use ViewModelProviders to associate your ViewModel with your UI controller.
        // When the app first starts, the ViewModelProviders will create the ViewModel.
        // When the activity is destroyed, for example through a configuration change,
        // the ViewModel persists. When the activity is re-created,
        // the ViewModelProviders return the existing ViewModel.
//        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);

        // An observer for the LiveData<List<Spot>> returned by getAllSpots().
        // The onChanged() method fires when the observed data changes and the activity is in the foreground.
        mSpotViewModel.getAllSpots().observe(this, new Observer<List<Spot>>() {
            @Override
            public void onChanged(@Nullable final List<Spot> spots) { // Must this be final? Seems to work without final.
                mSpotList = spots;
                // Update the cached copy of the words in the adapter.
//                adapter.setWords(words);
                // Set markers:
                for (Spot spot : spots) {
//                    googleMap.addMarker(new MarkerOptions()
                    mMap.addMarker(new MarkerOptions()
//                            .position(new LatLng(spot.getLatitude(), spot.getLongitude()))
                            .position(new LatLng(spot.getLatLng().latitude, spot.getLatLng().longitude))
                            .title(spot.getName())
                            .snippet(spot.getDescription()));
                }
            }
        });
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(pajuLedge_Spot.getLatLng())); // Set camera position to Marker
    }


    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

//        Intent spot_Intent = new Intent(this, NewSpotActivity.class);
//        Intent spot_Intent = new Intent(getContext(), NewSpotActivity.class);

//        spot_Intent.setData(marker.getTag());
//        spot_Intent.putExtra("Spot", (Integer) (marker.getTag()));
//        startActivity(spot_Intent);

        // Retrieve the data from the marker.
//        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
//        if (clickCount != null) {
//            clickCount = clickCount + 1;
//            marker.setTag(clickCount);
//            Toast.makeText(getContext(),
//                    marker.getTitle() +
//                            " has been clicked " + clickCount + " times.",
//                    Toast.LENGTH_SHORT).show();
//        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Access to the location has been granted to the app.
                    mMap.setMyLocationEnabled(true);
                    Toast.makeText(getContext(), "Location permission has been granted.", Toast.LENGTH_LONG).show();
                } else {
                    // Permission was denied. Display an error message.
                    Toast.makeText(getContext(), "You must grant permission in order to see spots near your current location.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
//        Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getContext(), NewSpotActivity.class);
        intent.putExtra(EXTRA_CURRENT_LOCATION, location);
        startActivity(intent);
//        location.getLongitude();
    }


    @Override
    public boolean onMyLocationButtonClick() {
//        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
}