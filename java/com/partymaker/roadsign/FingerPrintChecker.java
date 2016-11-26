package com.partymaker.roadsign;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import java.util.List;

/**
 * Created by vladimir on 22.10.16.
 */

public class FingerPrintChecker {
    public static Integer TRAIN_NUMBER = 1;
    public static Integer ONE_DIRECT_NUMBER = 2;

    private Bitmap fingerprintTrain;

    public void setFingerprintTrain(Bitmap fingerprintTrain) {
        this.fingerprintTrain = getResizedBitmap(fingerprintTrain, 50);
    }


    public Bitmap getFingerprintTrain() {
        return fingerprintTrain;
    }

    public Integer check(List<Point> segment){
        Bitmap bitmapSegment = getBitmapBySegment(segment);


        Integer height = fingerprintTrain.getHeight();
        Integer width = fingerprintTrain.getWidth();

        Integer likeCountTrain = 0;
        Integer likeCountOneDirect = 0;

//        Integer skosX = null;
//        Integer skosY =null;
//        if(bitmapSegment.getPixel(3,height) == Color.BLACK && fingerprintTrain.getPixel(width-3,height) == Color.BLACK) {
//
//        }else if(bitmapSegment.getPixel(3,height) == Color.WHITE && fingerprintTrain.getPixel(width-3,height) == Color.WHITE){
//
//        }else{
//
//            if(bitmapSegment.getPixel(3,height) == Color.BLACK){
//                skosX = 3;
//            }else{
//                skosX = width-3;
//            }
//
//            for(int y = height; y > 0; y--){
//                if(y < height - 12){
//                    break;
//                }
//                if(bitmapSegment.getPixel(skosX,y) == Color.WHITE){
//                    skosY = y;
//                    break;
//                }
//            }
//        }



        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                if(bitmapSegment.getPixel(x,y) == Color.BLACK && fingerprintTrain.getPixel(x,y) == Color.BLACK){
                    likeCountTrain++;
                }
                if(bitmapSegment.getPixel(x,y) == Color.WHITE && fingerprintTrain.getPixel(x,y) == Color.WHITE){
                    likeCountTrain++;
                }
            }
        }
        Log.d("myLogs", "likeCountOneDirect = " + likeCountOneDirect + " likeCountTrain = " + likeCountTrain);

        if(likeCountOneDirect > likeCountTrain){
            if(likeCountOneDirect > 1500){
                return ONE_DIRECT_NUMBER;
            }
        }


        return 0;
    }

    private Bitmap getResizedBitmap(Bitmap bm, int maxSize) {
        int outWidth;
        int outHeight;
        int inWidth = bm.getWidth();
        int inHeight = bm.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, outWidth, outHeight, false);
        return resizedBitmap;
    }

    private Bitmap getResizedToFingerBitmap(Bitmap bm) {
        int outWidth = fingerprintTrain.getWidth();
        int outHeight = fingerprintTrain.getHeight();
//        int inWidth = fingerprintTrain.getWidth();
//        int inHeight = fingerprintTrain.getHeight();
//        if(inWidth > inHeight){
//            outWidth = inWidth;
//            outHeight = inWidth;
//        } else {
//            outHeight = inHeight;
//            outWidth = inHeight;
//        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, outWidth, outHeight, false);
        return resizedBitmap;
    }


    private Bitmap getBitmapBySegment(List<Point> segment){
        Integer minX = null;
        Integer maxX = null;

        Integer minY = null;
        Integer maxY = null;

        for(Point point:segment){
            if(minX == null || minX > point.getX()){
                minX = point.getX();
            }
            if(maxX == null || maxX < point.getX()){
                maxX = point.getX();
            }
            if(minY == null || minY > point.getY()){
                minY = point.getY();
            }
            if(maxY == null || maxY < point.getY()){
                maxY = point.getY();
            }
        }
        Integer width = maxX - minX;
        Integer height = maxY - minY;

        Bitmap.Config conf = Bitmap.Config.RGB_565; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(width+1, height+1, conf); // this creates a MUTABLE bitmap

//        Log.d("myLogs","width = " + width);
//        Log.d("myLogs","minX = " + minX);
//        Log.d("myLogs","maxX = " + maxX);

        for(Point point:segment){
//            Log.d("myLogs","point.getX() = " + point.getX());
            bmp.setPixel(point.getX() - minX,point.getY() - minY,0xffffff);
        }

        bmp = getResizedToFingerBitmap(bmp);

        return bmp;
    }

}
