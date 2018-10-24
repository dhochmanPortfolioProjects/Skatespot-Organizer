package com.dhochmanrquick.skatespotorganizer;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.dhochmanrquick.skatespotorganizer.data.Spot;
import com.dhochmanrquick.skatespotorganizer.data.SpotViewModel;
import com.dhochmanrquick.skatespotorganizer.utils.PictureUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SpotPhotoViewPagerAdapter extends PagerAdapter {

    private final Context mContext;
    private String[] mSpotImages_Array;
    private List<String> mSpotImages_List;
    private Spot mSpot;
    private SpotViewModel mSpotViewModel;
    private LayoutInflater mLayoutInflater;

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

    @Override
    public int getCount() {
        if (mContext instanceof EditSpotActivity) {
            return mSpot.getPhotoCount();
        } else {
            return mSpotImages_List.size();
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//        return false;
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) { // Have to make final so we can see it inside of onClick()
        ImageView imageView = new ImageView(mContext);

        if (mContext instanceof EditSpotActivity) {
            final Bitmap bitmap = PictureUtils.getScaledBitmap(mSpot.getPhotoFilepath(position + 1), 1000, 1000);
            imageView.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));;
            imageView.setImageBitmap(bitmap);
//            final Bitmap bitmap = PictureUtils.getScaledBitmap(mSpot.getPhotoFilepath(position + 1), 1000, 1000);

            final String[] items = new String[]{"Remove photo", "Edit photo"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item, items);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Edit spot photo");
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {
                        mSpot.removePhotoFilepath(position + 1);
                        mSpotViewModel.updateSpots(mSpot);
//                        mSpotViewModel.deleteSpots(mSpotList.get(position));
                    }
                }
            });
            final AlertDialog dialog = builder.create();
            imageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.show();
                }
            });
        } else {
        // Get current Spot image path from mSpotImages_List and create bitmap
        final Bitmap bitmap = PictureUtils.getScaledBitmap(mSpotImages_List.get(position), 1000, 1000);
//            Bitmap bitmap = PictureUtils.getScaledBitmap("/data/user/0/com.dhochmanrquick.skatespotorganizer/files/IMG_0.jpg", 50, 50);
//        final ImageView spot_ImageView = (ImageView) findViewById(R.id.spot_detail_image_iv);
//        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));;

//        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        ImageView imageView = (ImageView) mLayoutInflater.inflate(R.layout.spot_image_viewpager_item, container, false);
//        ImageView imageView = (ImageView) container.findViewById(R.id.spot_detail_image_iv);
        imageView.setImageBitmap(bitmap);

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

            imageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //this will log the page number that was click
//                Log.i("TAG", "This page was clicked: " + position);
                    Dialog spotImage_Dialog = new Dialog(mContext);
//                            spotImage_Dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    ImageView imageView = new ImageView(mContext);
                    imageView.setImageBitmap(bitmap);
                    spotImage_Dialog.getWindow().setContentView(imageView);
//                            settingsDialog.getWindow().setContentView(spot_ImageView); // java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
//                            settingsDialog.getWindow().setBackgroundDrawableResource(R.drawable.paju_spot_landscape);
//                            settingsDialog.setContentView(getLayoutInflater().inflate(spot_ImageView/*R.layout.image_layout*/, null));
                    spotImage_Dialog.setCancelable(true);
                    spotImage_Dialog.show();
                }
            });
        }
        container.addView(imageView);

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }
}
