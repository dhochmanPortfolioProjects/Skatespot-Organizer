package com.dhochmanrquick.skatespotorganizer.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;

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
}
