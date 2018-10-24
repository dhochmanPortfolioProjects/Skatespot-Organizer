package com.dhochmanrquick.skatespotorganizer.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dhochmanrquick.skatespotorganizer.EditSpotActivity;
import com.dhochmanrquick.skatespotorganizer.R;
import com.dhochmanrquick.skatespotorganizer.data.Spot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class PictureUtils {
    /**
     * This method checks to see how big the screen is and then scales the image down to that size.
     * The image view you load into will always be smaller than this size, so this is a very
     * conservative estimate.
     *
     * @param path
     * @param activity
     * @return  a bitmap for a particular Activity's size
     */
    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        // Read in the dimensions of the image on dish
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out how much to scale down by:
        // inSampleSize determines how big each "sample" should be for each pixel - a sample size
        // of 1 has one final horizontal pixel for each horizontal pixel in the original file, and
        // a sample size of 2 has one horizontal pixel for every two horizontal pixels in the
        // original file. So when inSampleSize is 2, the image has a quarter of the number of pixels
        // of the original
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            float heightScale = srcHeight / destHeight;
            float widthScale = srcWidth / destWidth;

            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        // Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }

    public static File getPhotoFile(Application application,  Spot spot) {
        File filesDir = application.getFilesDir();
        return new File(filesDir, spot.generateNextPhotoFilename());
    }

    public static boolean copyUriContentToFile(Application application, Uri srcURI, File destFile) {
//        if (mImageCaptureUri != null) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
//            File spotPhotoFile;
        try {
            ContentResolver content = application.getContentResolver();
            inputStream = content.openInputStream(srcURI);

            File root = Environment.getExternalStorageDirectory();
            if (root == null) {
//                                Log.d(TAG, "Failed to get root");
            }
            // create a directory
//                            File saveDirectory = new File(Environment.getExternalStorageDirectory()+File.separator+ "directory_name" +File.separator);
            // create direcotory if it doesn't exists
//                spotPhotoFile = new File(getFilesDir(), mNewSpot.generateNextPhotoFilename());
//                            saveDirectory.mkdirs();
            outputStream = new FileOutputStream(destFile);
//                            outputStream = new FileOutputStream( saveDirectory + "filename.extension"); // filename.png, .mp3, .mp4 ...
            if (outputStream != null) {
//                                Log.e( TAG, "Output Stream Opened successfully");
            }
            byte[] buffer = new byte[1000];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) >= 0) {
                outputStream.write(buffer, 0, buffer.length);
            }
//                mNewSpot.incrementPhotoCount(); // On photo success, update current photo count
//                mNewSpot.setPhotoFilepath(spotPhotoFile.getPath(), mNewSpot.getPhotoCount());
        } catch (Exception e) {
            return false;
//                            Log.e( TAG, "Exception occurred " + e.getMessage());
        } finally {
        }
        return true; // Success
    }

    public static void configureDotSlider(LinearLayout sliderDotsPanel, final int dotsCount,
                                          final Context context,/* ViewPager viewPager,*/ int photoIndexToDisplay){
//        LinearLayout sliderDotsPanel = sliderDotsView; //findViewById(R.id.SliderDots);
//        final int dotsCount = numOfDots; // viewPagerAdapter.getCount();
        final ImageView[] dotImages_Array = new ImageView[dotsCount];

        // Dynamically create dot ImageViews for each item (spot photo) in the adapter and add to
        // SliderDotsPanel View
        sliderDotsPanel.removeAllViews();
        for (int i = 0; i < dotsCount; i++) {
            dotImages_Array[i] = new ImageView(context/*EditSpotActivity.this*/);
            dotImages_Array[i].setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotsPanel.addView(dotImages_Array[i], params);
        }

        // Set the active dot
        if (photoIndexToDisplay == -1) {
            // Set first dot to active
            dotImages_Array[0].setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.active_dot));
        } else {
            dotImages_Array[photoIndexToDisplay].setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.active_dot));
        }

//        viewPager.removeOnPageChangeListener(mOnPageChangeListener);
//        mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
////                            int dotsCount_local = dotsCount;
////                            ImageView[] dotImages_Array_local = new ImageView[dotsCount_local];
//                // Set all dots to inactive
//                for (int i = 0; i < dotsCount; i++) {
//                    dotImages_Array[i].setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.non_active_dot));
//                }
//                // Set the dot corresponding to the current page to active
//                dotImages_Array[position].setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.active_dot));
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        };
    }
}
