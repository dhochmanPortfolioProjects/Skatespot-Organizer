package com.dhochmanrquick.skatespotorganizer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;
import com.dhochmanrquick.skatespotorganizer.utils.PictureUtils;

import java.io.File;
import java.util.List;

public class SpotPhotoViewPagerAdapter extends PagerAdapter {

    private static final int EDIT_PHOTO = 3;

    private final Context mContext;
    private String[] mSpotImages_Array;
    private List<String> mSpotImages_List;
    private Spot mSpot;
    private SpotViewModel mSpotViewModel;
    private LayoutInflater mLayoutInflater;
//    private ImageView noImageIcon = null;
    private boolean mSpotHasNoPhoto;
    private int mPhotoIndexToDisplay;

    public SpotPhotoViewPagerAdapter(Context context, String[] spotImages) {
//        super();
        mContext = context;
        mSpotImages_Array = spotImages;
    }

    public SpotPhotoViewPagerAdapter(Context context, List<String> spotImages) {
//        super();
        mContext = context;
        mSpotImages_List = spotImages;
    }

    public SpotPhotoViewPagerAdapter(Context context, Spot spot, SpotViewModel spotViewModel) {
        mContext = context;
        mSpot = spot;
        mSpotViewModel = spotViewModel;
    }

    public SpotPhotoViewPagerAdapter(Context context, Spot spot, SpotViewModel spotViewModel, int photoIndexToDisplay) {
        mContext = context;
        mSpot = spot;
        mSpotViewModel = spotViewModel;
        mPhotoIndexToDisplay = photoIndexToDisplay;
    }
//
//    public SpotPhotoViewPagerAdapter(Context context, ImageView imageView) {
//        mContext = context;
//        noImageIcon = imageView;
//    }

    public SpotPhotoViewPagerAdapter(Context context, boolean spotHasNoPhoto) {
        mContext = context;
        mSpotHasNoPhoto = spotHasNoPhoto;
    }

    @Override
    public int getCount() {
//        if (mContext instanceof EditSpotActivity) {
            return mSpotHasNoPhoto ? 1 : mSpot.getPhotoCount();
//        } else {
//            return mSpotImages_List.size();
//        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//        return false;
        return view == object;
    }

    // This method must do 2 things:
    // 1). Create a View and add it to ViewGroup container
    // 2). Return the View
    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) { // Have to make final so we can see it inside of onClick()

        ImageView item_ImageView = new ImageView(mContext);

        // If the current Spot has no photos, then set the View to the no image icon, add it to
        // ViewGroup container, and return it
        if (mSpotHasNoPhoto) {
            item_ImageView.setImageResource(R.drawable.ic_no_image);
            container.addView(item_ImageView);
            return item_ImageView;
        }
        if (mContext instanceof EditSpotActivity || mContext instanceof SpotDetailActivity) {
            final Bitmap bitmap = PictureUtils.getScaledBitmap(mSpot.getPhotoFilepath(position + 1), 1000, 1000);
            item_ImageView.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));;
            item_ImageView.setImageBitmap(bitmap);
//            final Bitmap bitmap = PictureUtils.getScaledBitmap(mSpot.getPhotoFilepath(position + 1), 1000, 1000);

            final String[] items = new String[]{"Remove photo", "Edit photo"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item, items);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("EDIT SPOT PHOTO");
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) { // Remove photo
                        File fileToDelete = new File(mSpot.getPhotoFilepath(position + 1));
                        if (fileToDelete.delete()) {
//                            Toast.makeText(NewSpotActivity.this, mEditSpot.getPhotoFilepath(mEditSpot.getPhotoCount()) + " has been deleted.", Toast.LENGTH_LONG).show();
//                                Toast.makeText(EditSpotActivity.this, mEditSpot.getName() + " has been deleted.", Toast.LENGTH_LONG).show();
                        } else {
//                            Toast.makeText(SpotDetailActivity.this, mSpot.getPhotoFilepath(mSpot.getPhotoCount()) + " has not been deleted.", Toast.LENGTH_LONG).show();
                        }
                        mSpot.removePhotoFilepathAndShiftDown(position + 1);
                        mSpotViewModel.updateSpots(mSpot);
//                        mSpotViewModel.deleteSpots(mSpotList.get(position));
                    } else if (item == 1) {
                        File outputFile = new File(mContext.getFilesDir(), mSpot.generateNextPhotoFilename());
//                        Uri outputUri = FileProvider.getUriForFile(mContext.getApplicationContext(),
//                                "com.dhochmanrquick.skatespotorganizer.fileprovider", outputFile);

                        Intent editIntent = new Intent(Intent.ACTION_EDIT);
//                        editIntent.setClass(mContext, EditSpotActivity.class);
//                        Intent editIntent = new Intent(mContext, EditSpotActivity.class);
//                        editIntent.setAction(Intent.ACTION_EDIT);
                        File mPhotoFile = new File(mSpot.getPhotoFilepath(position + 1));
//                        Uri uri = Uri.fromFile(mPhotoFile); // Causes error
                        Uri uri = FileProvider.getUriForFile(mContext.getApplicationContext(),
                            "com.dhochmanrquick.skatespotorganizer.fileprovider", mPhotoFile);
//                        editIntent.setDataAndType(uri, "image/*");
                        editIntent.setDataAndType(uri, "image/*");
                        editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                        editIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

//                        mContext.startActivity(Intent.createChooser(editIntent, null));
//                        ((EditSpotActivity) mContext).startActivityForResult(Intent.createChooser(editIntent, null), EDIT_PHOTO);
                        ((SpotDetailActivity) mContext).startActivityForResult(Intent.createChooser(editIntent, null), EDIT_PHOTO);
//                        mSpot.removePhotoFilepath(position + 1);
//                        mSpotViewModel.updateSpots(mSpot);
//                        mSpot.setPhotoFilepath(mPhotoFile.getPath() ,position + 1);
//                        mSpotViewModel.updateSpots(mSpot);
//                        container.getContext().startActivityFor
                    }
                }
            });
            final AlertDialog dialog = builder.create();
            item_ImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.show();
                }
            });
        } else {
        // Get current Spot image path from mSpotImages_List and create bitmap
//        final Bitmap bitmap = PictureUtils.getScaledBitmap(mSpotImages_List.get(position), 1000, 1000);
            final Bitmap bitmap = PictureUtils.getScaledBitmap(mSpot.getPhotoFilepath(position + 1), 1000, 1000);
//            Bitmap bitmap = PictureUtils.getScaledBitmap("/data/user/0/com.dhochmanrquick.skatespotorganizer/files/IMG_0.jpg", 50, 50);
//        final ImageView spot_ImageView = (ImageView) findViewById(R.id.spot_detail_image_iv);
//        ImageView imageView = new ImageView(mContext);
        item_ImageView.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));;

//        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        ImageView imageView = (ImageView) mLayoutInflater.inflate(R.layout.spot_image_viewpager_item, container, false);
//        ImageView imageView = (ImageView) container.findViewById(R.id.spot_detail_image_iv);
        item_ImageView.setImageBitmap(bitmap);

//        return super.instantiateItem(container, position);
//        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View itemView = mLayoutInflater.inflate(R.layout.spot_image_viewpager_item, container, false);

//        ImageView imageView = itemView.findViewById(R.id.spot_detail_image_iv);
//        ImageView imageView = new ImageView(mContext);
//        Picasso.get()
//                .load(mSpotImages[position])
////                .placeholder()
//                .into(imageView);
////        ImageView imageView = new ImageView(mContext);

            item_ImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ImageView imageView = new ImageView(mContext);
                    imageView.setImageBitmap(bitmap);
                    Dialog spotImage_Dialog = new Dialog(mContext);
                    spotImage_Dialog.getWindow().setContentView(imageView);

//                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//                    lp.copyFrom(spotImage_Dialog.getWindow().getAttributes());
//                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;

//                            settingsDialog.getWindow().setContentView(spot_ImageView); // java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
//                            settingsDialog.getWindow().setBackgroundDrawableResource(R.drawable.paju_spot_landscape);
//                            settingsDialog.setContentView(getLayoutInflater().inflate(spot_ImageView/*R.layout.image_layout*/, null));
                    spotImage_Dialog.setCancelable(true);
                    spotImage_Dialog.show();
                }
            });
        }
        container.addView(item_ImageView);
        return item_ImageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

}
