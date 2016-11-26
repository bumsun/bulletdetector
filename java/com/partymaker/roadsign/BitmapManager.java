package com.partymaker.roadsign;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by vladimir on 08.05.16.
 */
public class BitmapManager {
    public static final Integer MAX_IMAGE_DIMENSION = 4000;

    public static Bitmap getCorrectlyOrientedImage(File imgFile) throws IOException {
        Bitmap myBitmap = getBitmap(imgFile.getAbsolutePath());

        try {
            ExifInterface exif = new ExifInterface(imgFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true); // rotating bitmap
        } catch (Exception e) {

        }
        return myBitmap;
    }

    public static Bitmap getCorrectlyOrientedImageLandscape(File imgFile, boolean isFrontCamera) throws IOException {
        Bitmap myBitmap = getBitmap(imgFile.getAbsolutePath());

        try {
            Matrix matrix = new Matrix();
            if (!isFrontCamera) {

                matrix.postRotate(90);
            } else {
                matrix.postRotate(270);
            }

            myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true); // rotating bitmap
        } catch (Exception e) {

        }
        return myBitmap;
    }
    public static Bitmap getCorrectlyOrientedImage(Bitmap myBitmap, ExifInterface exif) throws IOException {

        try {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true); // rotating bitmap
        } catch (Exception e) {

        }
        return myBitmap;
    }

    public static Bitmap getBitmap(String photoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);

        return bitmap;
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());
        return Bitmap.createScaledBitmap(realImage, width,
                height, filter);
    }

    public static void deletePhotosFullMini(String pathFull) {
        String[] splits = pathFull.split("/full/");
        String pathMini = splits[0] + "/mini/" + splits[1];
        File fileFull = new File(pathFull);
        File fileMini = new File(pathMini);
        if (fileFull.exists()) {
            fileFull.delete();
        }
        if (fileMini.exists()) {
            fileMini.delete();
        }
    }
    public static Bitmap createSingleImageFromMultipleImages(Bitmap inputImage){
        Bitmap result = null;
        if (inputImage.getHeight() > inputImage.getWidth()) {
            result = Bitmap.createBitmap(inputImage.getHeight(), inputImage.getWidth(), inputImage.getConfig());
        } else {
            result = Bitmap.createBitmap(inputImage.getWidth(), inputImage.getHeight(), inputImage.getConfig());
        }
        Bitmap littlePicture = Bitmap.createScaledBitmap(inputImage, 10 , 10, true);
        Bitmap first = Bitmap.createScaledBitmap(littlePicture, result.getWidth(), result.getHeight(), true);
        Bitmap second = BitmapManager.scaleDown(inputImage, first.getHeight(), true);
        //Bitmap second = Bitmap.createScaledBitmap(inputImage, inputImage.getWidth() * (first.getHeight() /inputImage.getHeight()), first.getHeight(), true);

       /* Allocation input = Allocation.createFromBitmap(rs, bitmapOriginal); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(8f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmapOriginal);*/

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(first, 0f, 0f, null);
        canvas.drawBitmap(second, result.getWidth() / 2 - second.getWidth() / 2, 0f, null);




        return result;
    }

}
