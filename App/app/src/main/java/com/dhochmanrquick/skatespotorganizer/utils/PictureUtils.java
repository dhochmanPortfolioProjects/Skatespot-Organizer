package com.dhochmanrquick.skatespotorganizer.utils;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.dhochmanrquick.skatespotorganizer.data.Spot;

import java.io.File;

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
}
