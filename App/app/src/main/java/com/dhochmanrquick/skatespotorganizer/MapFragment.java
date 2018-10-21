package com.dhochmanrquick.skatespotorganizer;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.maps.model.MapStyleOptions;
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
        GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

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
    // Will hold a reference to activity's implementation of OnFragmentInteractionListener, so that
    // this fragment can share events with the activity by calling methods defined by the
    // OnFragmentInteractionListener interface.
    private OnFragmentInteractionListener mListener;
    private SpotViewModel mSpotViewModel; // The ViewModel (the app's data)
    private GoogleMap mMap;
    private MapView mMapView;
    private List<Spot> mSpotList;
    private Context mContext;
    private Activity mActivity;
    private CameraPosition mCameraPosition;
    private boolean mLoadFromCurrentLocation = true;
    private boolean mIsFirstInstantiation = true;

    /**
     * Default constructor. Every fragment must have an empty constructor, so it can be instantiated
     * when restoring its activity's state. It is strongly recommended that subclasses do not have
     * other constructors with parameters, since these constructors will not be called when the fragment
     * is re-instantiated; instead, arguments can be supplied by the caller with setArguments(Bundle)
     * and later retrieved by the Fragment with getArguments().
     * <p>
     * Applications should generally not implement a constructor. Prefer onAttach(Context) instead.
     * It is the first place application code can run where the fragment is ready to be used - the
     * point where the fragment is actually associated with its context. Some applications may also
     * want to implement onInflate(Activity, AttributeSet, Bundle) to retrieve attributes from a
     * layout resource, although note this happens when the fragment is attached.
     */
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

    public static MapFragment newInstance(List<Spot> spotList) {
//        mSpotList = spotList;
        return new MapFragment();
    }

    public static MapFragment newInstance(CameraPosition cameraPosition) {
        return new MapFragment();
    }

    /**
     * The system calls this when creating the fragment. Within your implementation, you should
     * initialize essential components of the fragment that you want to retain when the fragment
     * is paused or stopped, then resumed.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use ViewModelProviders to associate your ViewModel with your UI controller.
        // When the app first starts, the ViewModelProviders will create the ViewModel.
        // When the activity is destroyed, for example through a configuration change,
        // the ViewModel persists. When the activity is re-created,
        // the ViewModelProviders return the existing ViewModel.
//        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);
        mSpotViewModel = ViewModelProviders.of(getActivity()).get(SpotViewModel.class);
        // In the section Share data between fragments of the ViewModel documentation, 2 co-hosted
        // fragments get a shared ViewModel (in onCreate()) and send it getActivity() instead of "this":
        // Notice that both fragments use getActivity() when getting the ViewModelProvider.
        // As a result, both fragments receive the same SharedViewModel instance, which is scoped to
        // the activity.
    }

    /**
     * The system calls this when it's time for the fragment to draw its user interface for the first
     * time. To draw a UI for your fragment, you must return a View from this method that is the root
     * of your fragment's layout. You can return null if the fragment does not provide a UI.
     *
     * @param inflater           To return a layout from onCreateView(), you can inflate it from a layout
     *                           resource defined in XML. To help you do so, onCreateView() provides a
     *                           LayoutInflater object.
     * @param container          The parent ViewGroup (from the activity's layout) in which your fragment layout is inserted
     * @param savedInstanceState A Bundle that provides data about the previous instance of the
     *                           fragment, if the fragment is being resumed
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
        mMapView = view.findViewById(R.id.map); // Get Map View from the inflated content view
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(this); // when you already implement OnMapReadyCallback in your fragment

        FloatingActionButton map_style_fab = view.findViewById(R.id.map_style_button);
        FloatingActionButton gps_locater_fab = view.findViewById(R.id.current_location_button);

        super.onViewCreated(view, savedInstanceState);

        // Set OnClickListener for the handleSearchQuery button: Get text from EditText, query the ViewModel
        // to for the Spot, if found, zoom in on the spot, if not, pop a toast.
//        ((ImageButton) view.findViewById(R.id.search_ic)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                EditText searchString_EditText = (EditText) view.findViewById(R.id.spot_search_bar);
//                String searchString = searchString_EditText.getText().toString();
//                Toast.makeText(getContext(), "Searching for " + searchString, Toast.LENGTH_LONG).show();

//                mSpotViewModel.getSpot(searchString).observe(getActivity(), new Observer<Spot>() {
//                    @Override
//                    public void onChanged(@Nullable Spot spot) {
////                        mMap.addMarker(new MarkerOptions()
//////                            .position(new LatLng(spot.getLatitude(), spot.getLongitude()))
////                                .position(new LatLng(spot.getLatLng().latitude, spot.getLatLng().longitude))
////                                .title(spot.getName())
////                                .snippet(spot.getDescription()));
//                        if (spot != null) {
////                            mMap.clear();
////                            mMap.addMarker(new MarkerOptions()
//////                            .position(new LatLng(spot.getLatitude(), spot.getLongitude()))
////                                    .position(new LatLng(spot.getLatLng().latitude, spot.getLatLng().longitude))
////                                    .title(spot.getName())
////                                    .snippet(spot.getDescription()));
//
//                            // Adding the circle to the GoogleMap
//                            Circle circle = mMap.addCircle(new CircleOptions()
//                                    .center(new LatLng(spot.getLatLng().latitude, spot.getLatLng().longitude))
//                                    .radius(10)
//                                    .strokeColor(Color.BLACK) // Border color of the circle
//                                    // Fill color of the circle.
//                                    // 0x represents, this is an hexadecimal code
//                                    // 55 represents percentage of transparency. For 100% transparency, specify 00.
//                                    // For 0% transparency ( ie, opaque ) , specify ff
//                                    // The remaining 6 characters(00ff00) specify the fill color
//                                    .fillColor(0x8800ff00)
//                                    // Border width of the circle
//                                    .strokeWidth(2)); // Todo: Make this transparent blue?
//
//                            // To change the position of the camera, you must specify where you want
//                            // to move the camera, using a CameraUpdate. The Maps API allows you to
//                            // create many different types of CameraUpdate using CameraUpdateFactory.
//                            // Animate the move of the camera position to spot's coordinates and zoom in
//                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(spot.getLatLng(), 18)),
//                                    2000, null);
//                        } else {
//                            Toast.makeText(getContext(), "Spot not found", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });

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
//            }
//        });

        map_style_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "need to figure out how to get a dialog to open", Toast.LENGTH_SHORT).show();
            }
        });

        gps_locater_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location locationCt;
                LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: I put this in as a quick and dirty check
                    return;
                }
                locationCt = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                LatLng latLng = new LatLng(locationCt.getLatitude(),
                        locationCt.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            }
        });
    }

    /**
     * The system calls this method as the first indication that the user is leaving the fragment
     * though it doesn't always mean the fragment is being destroyed). This is usually where you
     * should commit any changes that should be persisted beyond the current user session (because
     * the user might not come back).
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * This interface must be implemented by activities that contain this fragment to allow an
     * interaction in this fragment to be communicated to the activity and potentially other
     * fragments contained in that activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name

        /**
         * This interface's single method. The hosting Activity must implement this interface and
         * provide an implementation of this method so that this Fragment can communicate with the
         * Activity.
         *
         * @param spotId
         */
//        void onFragmentInteraction(Uri uri);
        void onFragmentInteraction(int spotId);
//        void onFragmentInteraction(LatLng spotPosition);

    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    /**
     * This method is called when a fragment is attached to an activity, whether it was retained or not.
     * Remember, Activity is a subclass of Context, so onAttach passes a Context as a parameter,
     * which is more flexible. Ensure that you use the onAttach(Context) signature for onAttach and
     * not the deprecated onAttach(Activity) method, which may be removed in future versions of the API.
     *
     * @param context The hosting Activity (Activity is a subclass of Context)
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
        mActivity = getActivity();

        // If the hosting Activity has implemented the OnFragmentInteractionListener interface, then
        // it must provide an implementation of its single method onFragmentInteraction(), which will be
        // called by this Fragment in order to communicate with its hosting Activity.
        //
        // To ensure that the host activity implements the OnFragmentInteractionListener interface,
        // this fragments's onAttach() callback method (which the system calls when adding the fragment
        // to the activity) instantiates an instance of OnFragmentInteractionListener by casting the
        // Activity that is passed into onAttach()
        if (context instanceof OnFragmentInteractionListener) {
            // You can access the interface methods on the class by casting it to the interface.
            // On success, the mListener member holds a reference to activity's implementation of
            // OnFragmentInteractionListener, so that this fragment can share events with the activity
            // by calling methods defined by the OnFragmentInteractionListener interface.
            mListener = (OnFragmentInteractionListener) context; // We can now call the interface method on mListener and do so in onInfoWindowClick()
        } else { // The hosting Activity does not implement the interface
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * The waning Fragment lifecycle method which corresponds to onAttach().
     */
    @Override
    public void onDetach() {
        super.onDetach();
        // Set the variable to null here because afterward you cannot access the activity or count
        // on the activity continuing existing
        mListener = null;
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

        // From Fragment Documentation:
        // Caution: If you need a Context object within your Fragment, you can call getContext().
        // However, be careful to call getContext() only when the fragment is attached to an activity.
        // When the fragment is not yet attached, or was detached during the end of its lifecycle,
        // getContext() will return null.
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) // Triggers null pointer when fragment is recreated
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
//            Toast.makeText(mContext, "You must grant permission in order to see spots near your current location.", Toast.LENGTH_LONG).show();
            // Permission to access the location is missing.
//            PermissionUtils.requestPermission(((MainActivity) getActivity()), LOCATION_PERMISSION_REQUEST_CODE,
//                    Manifest.permission.ACCESS_FINE_LOCATION, true);
            PermissionUtils.requestPermission((MainActivity) mActivity, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);

            // Todo: I thought a check after requestPermission would be necessary to make changes take effect immediately, but we need to find another solution
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Access to the location has been granted to the app.
                mMap.setMyLocationEnabled(true);
            }
        }

        if (mIsFirstInstantiation) {
            // Update Google Maps to display current location when first loaded
            if (mMap.isMyLocationEnabled()) {
//                mLoadFromCurrentLocation = false;
                mIsFirstInstantiation = false;
                Location locationCt;
                LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
                locationCt = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                LatLng latLng = new LatLng(locationCt.getLatitude(),
                        locationCt.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        } else {
            // Restore CameraPosition from how it was before
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        }
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);


        // public final void setPadding (int left, int top, int right, int bottom)
        googleMap.setPadding(0, 50, 0, 130);

        // Set a listener for marker click.
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);

        // An observer for the LiveData<List<Spot>> returned by getAllSpots().
        // The onChanged() method fires when the observed data changes and the activity is in the foreground.
        mSpotViewModel.getAllSpots().observe(this, new Observer<List<Spot>>() {
            @Override
            public void onChanged(@Nullable final List<Spot> spots) { // Must this be final? Seems to work without final.
                if (spots != null) {
                    if (spots.size() > 0) {

//                        mMapView.onCreate(savedInstanceState);
//                        mMapView.onResume();
//                        mMapView.getMapAsync(MapFragment.this);

                        mSpotList = spots; // Locally cache the SpotList
                        // Update the cached copy of the words in the adapter.
//                adapter.setWords(words);
                        // Set markers on the map
                        for (Spot spot : spots) {
//                    googleMap.addMarker(new MarkerOptions()
                            mMap.addMarker(new MarkerOptions()
//                            .position(new LatLng(spot.getLatitude(), spot.getLongitude()))
                                    .position(new LatLng(spot.getLatLng().latitude, spot.getLatLng().longitude))
                                    .title(spot.getName())
                                    .snippet(spot.getDescription()));
//                            .snippet("" + spot.getId()));
                        }
                    } else {
                            mMap.clear();
                        }
                    }
//                } else {
//                    mMap.clear();
//                }
            }
        });
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(pajuLedge_Spot.getLatLng())); // Set camera position to Marker
//        updateUI(mSpotList); // This causes:  java.lang.NullPointerException: Attempt to invoke interface method 'java.util.Iterator java.util.List.iterator()' on a null object reference
//        at com.dhochmanrquick.skatespotorganizer.MapFragment.updateUI(MapFragment.java:642)
//        at com.dhochmanrquick.skatespotorganizer.MapFragment.onMapReady(MapFragment.java:491)

        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(mContext, R.raw.map_style_night);
        mMap.setMapStyle(style);
    }


    /**
     * Called when the user clicks a marker (the default appears to be popping the info window).
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        LatLng markerPosition = marker.getPosition();
//        int spotId = Integer.parseInt(marker.getSnippet());
        Spot clickedSpot;
        // Search the local SpotList for the Spot located at the clicked marker's position
        for (Spot spot : mSpotList) {
            if (spot.getLatLng().equals((markerPosition))) {
                clickedSpot = spot;
                // Send the event and spot ID to the host activity
                mListener.onFragmentInteraction(clickedSpot.getId());
                break;
//                Toast.makeText(getContext(), "Found spot " + spot.getName(), Toast.LENGTH_LONG).show();
            }
        }

//        mListener.onFragmentInteraction(markerPosition);
//        Toast.makeText(getContext(), latLng.toString(), Toast.LENGTH_LONG).show();
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

    /**
     * A method to respond to the action of a user submitting a search via the app bar. MainActivity
     * receives the ACTION_SEARCH Intent in onNewIntent() and calls this method if the MapFragment
     * is the currently loaded fragment, passing in the search String that the user queried for.
     *
     * @param query The search String that the user queried for
     */
    public void handleSearchQuery(String query) {
//        Toast.makeText(getContext(), "Querying for " + query, Toast.LENGTH_LONG).show();
        mSpotViewModel.getSpot(query).observe(getActivity(), new Observer<Spot>() {
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
    }

    public CameraPosition getCameraPosition() {
        return mMap.getCameraPosition();
    }

    public void setCameraPosition(CameraPosition cameraPosition) {
        mCameraPosition = cameraPosition;
//        mLoadFromCurrentLocation = false;
        mIsFirstInstantiation = false;
    }

//    public void updateUI(List<Spot> spots) {
//        mSpotList = spots; // Locally cache the SpotList
//        // Update the cached copy of the words in the adapter.
////                adapter.setWords(words);
//        // Set markers on the map
//        for (Spot spot : spots) {
////                    googleMap.addMarker(new MarkerOptions()
//            mMap.addMarker(new MarkerOptions()
////                            .position(new LatLng(spot.getLatitude(), spot.getLongitude()))
//                    .position(new LatLng(spot.getLatLng().latitude, spot.getLatLng().longitude))
//                    .title(spot.getName())
//                    .snippet(spot.getDescription()));
////                            .snippet("" + spot.getId()));
//        }
//    }
}
