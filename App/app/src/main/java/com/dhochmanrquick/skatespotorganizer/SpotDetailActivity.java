package com.dhochmanrquick.skatespotorganizer;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;
import com.dhochmanrquick.skatespotorganizer.utils.PictureUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.List;

/**
 * The UI controller for the Spot Detail view. This class is responsible for displaying the
 * detailed spot information to the user. The Spot Detail view consists of:
 * <p>
 * 1). A photo carousel (to display the photos of the Spot), including a dot slider to show the
 * user their location within the carousel.
 * 2). The Spot title
 * 3). The Spot description
 * 4). A map showing the Spot's location
 * 5). An edit Spot FAB (this has since been removed)
 */
public class SpotDetailActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    // Constants
    private static final String KEY_ACTIVE_DOT = "active_dot";  // Key used in onSaveInstanceState
                                                                // to write active dot to bundle
    // Request codes used in onActivityResult()
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int EDIT_PHOTO = 3;

    private static final String EXTRA_CURRENT_LOCATION = "com.dhochmanrquick.skatespotorganizer.extra_current_location";
    private static final String EXTRA_NEW_SPOT = "com.dhochmanrquick.skatespotorganizer.extra_new_spot";
    private static final String TEMP_SPOT_NAME = "No name";

    // Member variables
    private Spot mSpot;
    private int mSpotId;
    private File mPhotoFile;
    private SpotViewModel mSpotViewModel;
    private GoogleMap mMap;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private int mActivePosition;    // Keeps track of which photo and dot is currently active and
                                    // should be displayed. This is necessary if the UI state of
                                    // this Activity needs to be restored (i.e., after a rotation)
    private boolean mIsNewSpot = false;
    private boolean mIsBlankSpot = true;
    private AlertDialog upButton_dialog = null;
    private String mNewSpotName = TEMP_SPOT_NAME;

    // Member UI Views (in order of appearance)
    private TextView mSpotTitle_TextView;
    private ViewPager mSpotImage_ViewPager;
    private LinearLayout mDotSlider_LinearLayout;
    private TextView mSpotDescription_TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_detail);

        // Get the ViewModel
        mSpotViewModel = ViewModelProviders.of(this).get(SpotViewModel.class);

        // Get frequently used UI Views and set Listeners on them
        mSpotTitle_TextView = findViewById(R.id.spot_detail_title_tv);
        setUpSpotTitleTextView();
        mSpotDescription_TextView = findViewById(R.id.spot_detail_description_tv);
        setUpSpotDescriptionTextView();
        mSpotImage_ViewPager = findViewById(R.id.spot_detail_image_viewpager);
        mDotSlider_LinearLayout = findViewById(R.id.spot_detail_dot_slider);

        // Set the active position (which dictates the photo and dot to display).
        // In Observer.onChanged(), the ViewPager (photo to display) and the dot slider (dot to
        // activate) will be set according to mActivePosition
        if (savedInstanceState != null) {
            // Restore previously active position (if this Activity is being recreated)
            mActivePosition = savedInstanceState.getInt(KEY_ACTIVE_DOT, 0);
        } else { // This Activity is being launched for the first time; display 1st photo and activate 1st dot
            mActivePosition = 0;
        }

        // Within your UI, a map will be represented by either a MapFragment or MapView object.
        MapView mapView = findViewById(R.id.spot_detail_spotmap);
        // When using the API in fully interactive mode, users of the MapView class must forward the
        // following activity lifecycle methods to the corresponding methods in the MapView class:
        // onCreate(), onStart(), onResume(), onPause(), onStop(), onDestroy(), onSaveInstanceState(),
        // and onLowMemory().
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        // Use getMapAsync() to set the callback on the fragment.
        // Note: getMapAsync() must be called from the main thread, and the callback will be executed
        // in the main thread. If Google Play services is not installed on the user's device, the
        // callback will not be triggered until the user installs Play services.
        mapView.getMapAsync(this);

        // Check if this Activity is being launched to create a new spot
        mIsNewSpot = getIntent().getBooleanExtra(EXTRA_NEW_SPOT, false);
        // Check if this Activity is being launched to create a new spot with known coordinates
//        Location location = getIntent().getParcelableExtra(EXTRA_CURRENT_LOCATION);
//        if (location != null) {
////            ((EditText) findViewById(R.id.new_spot_latitude)).setText("" + location.getLatitude());
////            ((EditText) findViewById(R.id.new_spot_longtitude)).setText("" + location.getLongitude());
//        }

        // Retrieve intent extra
        if (!mIsNewSpot) {
            mSpotId = getIntent().getIntExtra("com.dhochmanrquick.skatespotorganizer", 0);
        }

        setUpAddPhotoButton();
        setUpDeleteSpot_Button();

        // Get the Spot (by ID) to display from the ViewModel and set an Observer on the LiveData (which
        // wraps the current Spot)
        LiveData<Spot> spot_LiveData;
        spot_LiveData = mIsNewSpot ? mSpotViewModel.getSpot(TEMP_SPOT_NAME) : mSpotViewModel.getSpot(mSpotId);
//        mSpotViewModel.getSpot(id).observe(this, new Observer<Spot>() {
        spot_LiveData.observe(this, new Observer<Spot>() {
            @Override
            public void onChanged(@Nullable Spot spot) { // Should this be final?
                if (spot != null) { // spot may be null if no Spot was found in the db
                    mSpot = spot; // Cache the Spot locally to access it outside of this Observer
                    mDotSlider_LinearLayout.removeAllViews(); // Clear any previous dots from the slider

                    // Set up an appropriate ViewPagerAdapter (which will supply the Spot photo Views
                    // to the ViewPager when it requests them)
                    SpotPhotoViewPagerAdapter spotPhotoViewPagerAdapter = setUpSpotPhotoViewPagerAdapter(spot);
                    mSpotImage_ViewPager.setAdapter(spotPhotoViewPagerAdapter); // Pass Adapter to ViewPager
                    mSpotImage_ViewPager.setCurrentItem(mActivePosition, true);
                    mSpotImage_ViewPager.addOnPageChangeListener(mOnPageChangeListener);

                    // Update Spot title and description TextViews
//                    mSpotTitle_TextView.setText(spot.getName());
                    if (mIsNewSpot) {
                        mSpotTitle_TextView.setText(mNewSpotName);
                    } else {
                        mSpotTitle_TextView.setText(spot.getName());
                    }

                    mSpotDescription_TextView.setText(spot.getDescription());

                    // Update map
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mSpot.getLatLng().latitude, mSpot.getLatLng().longitude)));
//                        .title(mSpot.getName())
//                        .snippet(mSpot.getDescription()));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mSpot.getLatLng()));
                } else {
                    finish();
                }
            }
        });

        // Setup FAB to open EditSpotActivity
//        FloatingActionButton fab = findViewById(R.id.spot_detail_edit_fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SpotDetailActivity.this, EditSpotActivity.class);
//                intent.putExtra("EDIT_SPOT", mSpot.getId());
//                startActivity(intent);
//            }
//        });

        // Build an alert dialog for when the user presses the up button before saving the Spot
        AlertDialog.Builder upButton_builder = new AlertDialog.Builder(this);
        upButton_builder.setTitle("Abort Spot")
//                .setMessage("You have not saved your spot. Leave anyways?")
                .setMessage("All spots must have a name. Please name this spot before leaving or abort it.")
                .setCancelable(false)
                .setPositiveButton("Abort", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSpotViewModel.deleteSpots(mSpot);
                        finish();
                    }
                })
                .setNegativeButton("Name spot", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        upButton_dialog = upButton_builder.create();
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        Intent spotStreetView_Intent = new Intent(this, SpotStreetViewActivity.class);
        spotStreetView_Intent.putExtra("Spot", mSpot.getLatLng());
        startActivity(spotStreetView_Intent);
        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    /**
     * Use the onMapReady(GoogleMap) callback method to get a handle to the GoogleMap object.
     * The callback is triggered when the map is ready to be used. It provides a non-null instance
     * of GoogleMap. You can use the GoogleMap object to set the view options for the map or add a
     * marker, for example. The key class when working with a map object is the GoogleMap class.
     * GoogleMap models the map object within your application. Within your UI, a map will be
     * represented by either a MapFragment or MapView object.
     *
     * @param googleMap a non-null instance of the GoogleMap object
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_ACTIVE_DOT, mActivePosition);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);

        // When SpotPhotoViewPagerAdapter "starts for result" the ACTION_EDIT Intent, this class is
        // declared as the launching class, so the onActivityResult belonging to this class is
        // called. ACTION_EDIT doesn't seem to be defined to actually return a result, however, this
        // method is still called anyways. The parameter "intent" is null, resultCode is 0
        // (unconventionally, -1 means success), but requestCode is still EDIT_PHOTO (defined as
        // the int value 3). This is odd but works. We just need to move the
        //
        // "if (resultCode != RESULT_OK) return;"
        //
        // to inside each case except for the EDIT_PHOTO case (because "resultCode != RESULT_OK"
        // will evaluate to true, even though it's working the way we want it to.

        // if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
//        if (resultCode != RESULT_OK) return;
        File filesDir = getFilesDir(); // Get handle to directory for private application files
        File photoFile;
        Bitmap bitmap;

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                if (resultCode != RESULT_OK) return;
                // Display the photo you just took (and its corresponding dot)
                mActivePosition = mSpot.getPhotoCount();
                mSpot.incrementPhotoCount();
                mSpot.setPhotoFilepath(mPhotoFile.getPath(), mSpot.getPhotoCount()); // Add new photo filepath to Spot
                mSpotViewModel.updateSpots(mSpot); // Update Spot to reflect newly added photo and trigger UI update
                break;
            case PICK_FROM_FILE:
                if (resultCode != RESULT_OK) return;
                Uri selectedImage_Uri = intent.getData(); // Get return contentURI
                if (PictureUtils.copyUriContentToFile(getApplication(), selectedImage_Uri, mPhotoFile)) {
                    mActivePosition = mSpot.getPhotoCount();
                    mSpot.incrementPhotoCount();
                    mSpot.setPhotoFilepath(mPhotoFile.getPath(), mSpot.getPhotoCount());
                    mSpotViewModel.updateSpots(mSpot);
                } else {
                    Toast.makeText(SpotDetailActivity.this,
                            "An error has occurred while saving the selected photo.",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case EDIT_PHOTO:
                mSpotViewModel.updateSpots(mSpot);
        }
    }

    // Below are a few private methods that have the simple purpose of setting up (i.e., setting
    // Listeners) a View in the UI. They are each called only once in onCreate(), but separating
    // their implementation down here leave onCreate() less cluttered. They could easily be put
    // back in to onCreate() in the future if needed.
    private void setUpSpotTitleTextView() {
        // Set up the AlertDialog to pop when the user clicks the Spot title TextView
        final AlertDialog editSpotTitle_Dialog = new AlertDialog.Builder(SpotDetailActivity.this).create();
        editSpotTitle_Dialog.setTitle("EDIT SPOT NAME");
        final EditText editSpotTitle_EditText = new EditText(SpotDetailActivity.this); // The EditText to appear in the AlertDialog
        editSpotTitle_Dialog.setView(editSpotTitle_EditText); // Pass EditText to the AlertDialog

        editSpotTitle_Dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                mSpot.setName(editSpotTitle_EditText.getText().toString()); // Update Spot name with user input
                // Instead of updating the Spot name, which causes problems for the Observer to
                // refetch the updated Spot from the database, let's try updating the name only
                // when the user leaves this Activity
                if (mIsNewSpot) {
                    mSpotTitle_TextView.setText(editSpotTitle_EditText.getText().toString());
                    mNewSpotName = editSpotTitle_EditText.getText().toString();
                } else {
                    mSpot.setName(editSpotTitle_EditText.getText().toString()); // Update Spot name with user input
                    mSpotViewModel.updateSpots(mSpot); // Trigger UI update
                }
            }
        });

        editSpotTitle_Dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editSpotTitle_Dialog.cancel(); // Essentially do nothing; just close the Dialog
            }
        });

        mSpotTitle_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load EditText with the text currently in mSpotTitle_TextView (which should be the
                // Spot name)
                editSpotTitle_EditText.setText(mSpotTitle_TextView.getText().toString());
//                editSpotTitle_EditText.setText(mSpot.getName()); // Load EditText with the current Spot name
                editSpotTitle_Dialog.show();
            }
        });
    }

    private void setUpSpotDescriptionTextView() {
        // Set up the AlertDialog to pop when the user clicks the Spot title TextView
        final EditText editSpotDescription_EditText = new EditText(SpotDetailActivity.this);
        final AlertDialog editSpotDescription_Dialog = new AlertDialog.Builder(SpotDetailActivity.this).create();
        editSpotDescription_Dialog.setTitle("EDIT SPOT DESCRIPTION");
        editSpotDescription_Dialog.setView(editSpotDescription_EditText);
        editSpotDescription_Dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSpot.setDescription(editSpotDescription_EditText.getText().toString());
                mSpotViewModel.updateSpots(mSpot);
            }
        });

        editSpotDescription_Dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editSpotDescription_Dialog.cancel();
            }
        });

        mSpotDescription_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSpotDescription_EditText.setText(mSpot.getDescription());
                editSpotDescription_Dialog.show();
            }
        });
    }

    private void setUpAddPhotoButton() {
        final String[] items = new String[]{"From camera", "From gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder imageSelection_builder = new AlertDialog.Builder(this);
        imageSelection_builder.setTitle("Add image");
        imageSelection_builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) { // Pick from camera
                // Create new File where photo will be saved
                mPhotoFile = new File(getFilesDir(), mSpot.generateNextPhotoFilename());
                if (item == 0) { // User pressed "From camera"
                    // Create camera/image capture implicit intent
                    final Intent captureImage_Intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Determine whether there is a camera app available
                    boolean canTakePhoto = captureImage_Intent.resolveActivity(getPackageManager()) != null;
                    // Translate local filepath stored in mPhotoFile into a Uri the camera app can see
                    Uri uri = FileProvider.getUriForFile(getApplicationContext(),
                            "com.dhochmanrquick.skatespotorganizer.fileprovider", mPhotoFile);

                    // If you pass the extra parameter MediaStore.EXTRA_OUTPUT with the camera intent
                    // then camera activity will write the captured image to that path and it will not
                    // return the bitmap in the onActivityResult method.
                    captureImage_Intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    // Query Package Manager for every activity cameraImage_Intent can resolve to
                    List<ResolveInfo> cameraActivity =
                            getPackageManager().queryIntentActivities(captureImage_Intent, PackageManager.MATCH_DEFAULT_ONLY);

                    // Grant write permission for this Uri to each activity
                    for (ResolveInfo activity : cameraActivity) {
                        grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    startActivityForResult(captureImage_Intent, PICK_FROM_CAMERA);
                } else { // Pick from file
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*"); // Set the primary MIME type
                    //intent.addCategory(Intent.CATEGORY_OPENABLE); // May want to add this

                    // Method signature: static Intent	createChooser(Intent target, CharSequence title, IntentSender sender)
                    // Convenience function for creating a ACTION_CHOOSER Intent.
                    // CharSequence title: You can specify the title that will appear in the activity chooser.
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                    }
                }
            }
        });

        final AlertDialog imageSelection_dialog = imageSelection_builder.create();

        findViewById(R.id.spot_detail_add_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSpot.getPhotoCount() >= 5) {
                    Toast.makeText(SpotDetailActivity.this,
                            "You may only save 5 photos for this spot",
                            Toast.LENGTH_LONG)
                            .show();
                } else {
                    imageSelection_dialog.show();
                }
            }
        });
    }

    // Build and return an appropriate SpotPhotoViewPagerAdapter according to whether
    // the Spot has 0, 1, or 1+ photos; the SpotPhotoViewPagerAdapter knows how to
    // build appropriate views in all three cases.
    private SpotPhotoViewPagerAdapter setUpSpotPhotoViewPagerAdapter(Spot spot) {
        // Declared final for use in ViewPager.OnPageChangeListener()
        SpotPhotoViewPagerAdapter spotPhotoViewPagerAdapter;
        if (spot.getPhotoCount() == 0) { // Spot has no photos; tell the ViewPagerAdapter
            // so it can set the View to the "no image" icon
            spotPhotoViewPagerAdapter = new SpotPhotoViewPagerAdapter(
                    SpotDetailActivity.this, true);
        } else { // Spot has at least one photo
            // Construct the appropriate SpotPhotoViewPagerAdapter
            spotPhotoViewPagerAdapter = new SpotPhotoViewPagerAdapter(
                    SpotDetailActivity.this, spot, mSpotViewModel);
            if (spot.getPhotoCount() > 1) { // Only set up dot slider for 1+ photos
                final int dotsCount = spotPhotoViewPagerAdapter.getCount();
                final ImageView[] dotImages_Array = new ImageView[dotsCount]; // (Declared final for use in OnPageChangeListener)
                // Dynamically create "non active" dot ImageViews for each item
                // (spot photo) in the adapter and add to mDotSlider_LinearLayout
                for (int i = 0; i < dotsCount; i++) {
                    dotImages_Array[i] = new ImageView(SpotDetailActivity.this); // Create new dot and add to array
                    // Set dot to non active
                    dotImages_Array[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                    // Set layout params
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(8, 0, 8, 0);
                    // Although each dot View is added to the mDotSlider_LinearLayout,
                    // its visual appearance is still manipulated by taking from
                    // dotImages_Array[] and calling methods on it.
                    mDotSlider_LinearLayout.addView(dotImages_Array[i], params);
                }
                // Set the active dot; mActivePosition is initially 0 but may be another value
                // if the Activity is being recreated.
                if (mActivePosition >= dotImages_Array.length) {
                    // When the user removes a photo, mActivePosition is not properly updated
                    // (decremented) because the code that handles the photo removal is in
                    // SpotPhotoViewPagerAdapter, which doesn't have access to mActivePosition.
                    // As a quick fix, here, we simply assume that if dotImages_Array[mActivePosition]
                    // would cause an out of bounds error, it means that a photo was removed and
                    // mActivePosition wasn't decremented; so we decrement it here.
                    mActivePosition = dotImages_Array.length - 1; // Display last photo
                }
                dotImages_Array[mActivePosition].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                mSpotImage_ViewPager.removeOnPageChangeListener(mOnPageChangeListener);
                mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
                    // The following methods must be overridden
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        // Deactivate previously active dot
                        dotImages_Array[mActivePosition].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                        mActivePosition = position; // Update mActivePosition to the new dot
                        // Instead of iterating through all the dots and deactivating them,
                        // more efficient to just deactivate the previous dot and then
                        // activate the new dot
//                                    for (int i = 0; i < spotPhotoViewPagerAdapter.getCount(); i++) {
//                                        dotImages_Array[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
//                                    }
                        // Set active dot
                        dotImages_Array[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                };
            }
        }
        return spotPhotoViewPagerAdapter;
    }

    private void setUpDeleteSpot_Button() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Spot")
                .setMessage("Are you sure you want to permanently delete this spot?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User has chosen to delete this Spot
                        // First, need to delete the photo files
                        PictureUtils.deleteAllSpotPhotos(mSpotViewModel, mSpot);
//                        while (mSpot.getPhotoCount() > 0) {
//                            // Open the file
//
//                            // Create a path where we will place our picture in the user's
//                            // public download directory and delete the file.  If external
//                            // storage is not currently mounted this will fail.
////                        File path = Environment.getExternalStoragePublicDirectory(
////                                Environment.DIRECTORY_DOWNLOADS);
////                        File file = new File(path, "DemoPicture.jpg");
////                        file.delete();
////                        File filesDir = getFilesDir();
////                        filesDir.
////                        deleteFile(mEditSpot.getPhotoFilepath(mEditSpot.getPhotoCount()));
//                            File fileToDelete = new File(mSpot.getPhotoFilepath(mSpot.getPhotoCount()));
////                            File fileToDelete = new File(getFilesDir(), mEditSpot.getAbbreviatedPhotoFilepath(mEditSpot.getPhotoCount()));
////                        if (deleteFile(mEditSpot.getPhotoFilepath(mEditSpot.getPhotoCount()))) {
////                        if (deleteFile(mEditSpot.getAbbreviatedPhotoFilepath(mEditSpot.getPhotoCount()))) {
////                        if (deleteFile(mEditSpot.getAbbreviatedPhotoFilepath(mEditSpot.getPhotoCount()))) {
//                            if (fileToDelete.delete()) {
////                            Toast.makeText(NewSpotActivity.this, mEditSpot.getPhotoFilepath(mEditSpot.getPhotoCount()) + " has been deleted.", Toast.LENGTH_LONG).show();
////                                Toast.makeText(EditSpotActivity.this, mEditSpot.getName() + " has been deleted.", Toast.LENGTH_LONG).show();
//                            } else {
//                                Toast.makeText(SpotDetailActivity.this, mSpot.getPhotoFilepath(mSpot.getPhotoCount()) + " has not been deleted.", Toast.LENGTH_LONG).show();
//                            }
////                        Files.delete(mEditSpot.getPhotoFilepath(mEditSpot.getPhotoCount()));
//                            mSpot.decrementPhotoCount();
//                        }
                        if (mSpot.getPhotoCount() == 0) {
                            mSpotViewModel.deleteSpots(mSpot);
                            Toast.makeText(SpotDetailActivity.this,
                                    mSpot.getName() + " has been deleted.",
                                    Toast.LENGTH_LONG)
                                    .show();

                        } else {
                            Toast.makeText(SpotDetailActivity.this,
                                    "An error has occurred while trying to delete Spot " + mSpot.getName() + ".",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog dialog = builder.create();

        findViewById(R.id.spot_detail_delete_spot_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

            }
        });
    }

    // We use this onBackPressed() method in a way similar to a "save" button if this Activity
    // has been launched to create a new Spot. Since updating the Spot name in the database
    // immediately when the user edits it in the UI causes the Observer to no longer be able to
    // retrieve the Spot from the database, instead, we only update the UI; the user thinks they
    // have updated the Spot name, but it's only the UI that has been updated until they press
    // back to leave this Activity. So, this is where we do the actual Spot name update in the
    // database.
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (mIsNewSpot) {
//            if(mSpot.getName().equals(TEMP_SPOT_NAME)) {
            if(mNewSpotName.equals(TEMP_SPOT_NAME)) {
                // Pop a dialog telling the user that they must either change the Spot name to
                // something other than "No name" or abort the Spot creation
                upButton_dialog.show();
            } else {
                // The user has changed the Spot name; do the actual Spot name update in the
                // database
                mSpot.setName(mSpotTitle_TextView.getText().toString());
                mSpotViewModel.updateSpots(mSpot); // Update database
                finish();
            }
        } else { // Not launched to create a new Spot; regular behaviour for the back button
            finish();
        }
    }
}
