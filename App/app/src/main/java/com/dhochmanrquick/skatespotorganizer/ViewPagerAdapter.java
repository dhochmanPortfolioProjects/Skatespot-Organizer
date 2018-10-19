package com.dhochmanrquick.skatespotorganizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dhochmanrquick.skatespotorganizer.utils.PictureUtils;
import com.squareup.picasso.Picasso;

public class ViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private String[] mSpotImages;
    private LayoutInflater mLayoutInflater;

    public ViewPagerAdapter(Context context, String[] spotImages) {
//        super();
        mContext = context;
        mSpotImages = spotImages;
    }

    @Override
    public int getCount() {
        return mSpotImages.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//        return false;
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        final Bitmap bitmap = PictureUtils.getScaledBitmap(mSpotImages[position], 1000, 1000);
//            Bitmap bitmap = PictureUtils.getScaledBitmap("/data/user/0/com.dhochmanrquick.skatespotorganizer/files/IMG_0.jpg", 50, 50);
//        final ImageView spot_ImageView = (ImageView) findViewById(R.id.spot_detail_image_iv);
        ImageView imageView = new ImageView(mContext);
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
        container.addView(imageView);

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }
}
