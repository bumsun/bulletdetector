package com.partymaker.roadsign;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.ByVal;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.indexer.UByteBufferIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.Loader.sizeof;
import static org.bytedeco.javacpp.helper.opencv_core.CV_RGB;
import static org.bytedeco.javacpp.helper.opencv_imgproc.cvDrawContours;
import static org.bytedeco.javacpp.helper.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvCreateMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvCreateSeq;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_AA;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_HOUGH_PROBABILISTIC;
import static org.bytedeco.javacpp.opencv_imgproc.CV_MEDIAN;
import static org.bytedeco.javacpp.opencv_imgproc.CV_POLY_APPROX_DP;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_EXTERNAL;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_LIST;
import static org.bytedeco.javacpp.opencv_imgproc.GaussianBlur;
import static org.bytedeco.javacpp.opencv_imgproc.approxPolyDP;
import static org.bytedeco.javacpp.opencv_imgproc.cvApproxPoly;
import static org.bytedeco.javacpp.opencv_imgproc.cvArcLength;
import static org.bytedeco.javacpp.opencv_imgproc.cvBoundingRect;
import static org.bytedeco.javacpp.opencv_imgproc.cvCanny;
import static org.bytedeco.javacpp.opencv_imgproc.cvContourPerimeter;
import static org.bytedeco.javacpp.opencv_imgproc.cvDrawCircle;
import static org.bytedeco.javacpp.opencv_imgproc.cvHoughCircles;
import static org.bytedeco.javacpp.opencv_imgproc.cvLine;
import static org.bytedeco.javacpp.opencv_imgproc.cvMatchShapes;
import static org.bytedeco.javacpp.opencv_imgproc.cvMinEnclosingCircle;
import static org.bytedeco.javacpp.opencv_imgproc.cvResize;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import static org.bytedeco.javacpp.opencv_imgproc.cvHoughLines2;
import static org.bytedeco.javacpp.opencv_imgproc.resize;
import static org.bytedeco.javacpp.opencv_imgproc.threshold;

/**
 * Created by X550V on 21.10.2016.
 */

public class RoadSigns {


    public static Bitmap convetToHSV(Activity activity) throws IOException {

        OpenCVFrameConverter.ToMat.ToMat toMat = new OpenCVFrameConverter.ToMat();
        OpenCVFrameConverter.ToIplImage toIpl = new OpenCVFrameConverter.ToIplImage();

        AndroidFrameConverter frameConverter = new AndroidFrameConverter();

        // opencv_core.Mat mat = new opencv_core.Mat();
        // cvtColor(mat, mat, opencv_imgproc.CV_RGB2GRAY);

        InputStream is1 = activity.getAssets().open("fingerprint_train.png");
        Bitmap fingerprintTrain = BitmapFactory.decodeStream(is1);

        opencv_core.IplImage orgImg = cvLoadImage(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/test.jpg");
        //create binary image of original size

        opencv_core.IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);

        cvSmooth(orgImg, orgImg, 2, 1, 1, 1, 1);

        opencv_core.CvScalar min = cvScalar(50, 50, 50, 0);
        opencv_core.CvScalar max = cvScalar(255, 255, 255, 0);

        cvInRangeS(orgImg, min, max, imgThreshold);

//        if(true){
//            Frame frame1 = toMat.convert(imgThreshold);
//            Bitmap bitmap1 = frameConverter.convert(frame1);
//            return bitmap1;
//        }

        opencv_core.Mat mat = toMat.convert(toMat.convert(imgThreshold));

        Point[][] points = new Point[mat.rows()][mat.cols()];
        List<List<Point>> segmentList = new ArrayList<>();



        UByteBufferIndexer sI = mat.createIndexer();
        //((MainActivity)activity).runJNI(sI);

        List<Pair<Integer,Integer>> selectAreaList = new ArrayList<>();

        for (int y = 0; y < sI.height(); y++) {
            for (int x = 0; x < sI.width(); x++) {
                try {

                    if (sI.get(y, x) == 255) {

                    } else if (x != 0 && sI.get(y, x - 1) == 0) {

                        List<Point> segment = points[y][x - 1].getSegment();
                        Point point = new Point(x, y, segment);
                        segment.add(point);

                        points[y][x] = point;

                        if (y != 0 && sI.get(y - 1, x) == 0) {
                            List<Point> segment2 = points[y - 1][x].getSegment();
                            if (!segment.equals(segment2)) {
                                segment.addAll(segment2);

                                for (Point point2 : segment2) {
                                    point2.setSegment(segment);
                                }

                                segmentList.remove(segment2);
                            }
                        }
                    } else if (y != 0 && sI.get(y - 1, x) == 0) {

                        List<Point> segment = points[y - 1][x].getSegment();
                        Point point = new Point(x, y, segment);
                        segment.add(point);

                        points[y][x] = point;
                    } else {
                        List<Point> segment = new ArrayList<>();
                        Point point = new Point(x, y, segment);
                        segment.add(point);

                        segmentList.add(segment);
                        points[y][x] = point;
                    }
                } catch (Exception e) {
                    break;
                }
                //System.out.println( "sI = " + sI.get(y, x) );
            }
        }



//        for (int y = 0; y < sI.height(); y++) {
//            for (int x = 0; x < sI.width(); x++) {
//                try {
//                    if (sI.get(y, x) == 0) {
//
//                    } else if (x != 0 && sI.get(y, x - 1) == 255) {
//                        List<Point> segment = points[y][x - 1].getSegment();
//                        Point point = new Point(x, y, segment);
//                        segment.add(point);
//
//                        points[y][x] = point;
//
//                        if (y != 0 && sI.get(y - 1, x) == 255) {
//                            List<Point> segment2 = points[y - 1][x].getSegment();
//                            if (!segment.equals(segment2)) {
//                                segment.addAll(segment2);
//
//                                for (Point point2 : segment2) {
//                                    point2.setSegment(segment);
//                                }
//
//                                segmentList.remove(segment2);
//                            }
//                        }
//                    } else if (y != 0 && sI.get(y - 1, x) == 255) {
//
//                        List<Point> segment = points[y - 1][x].getSegment();
//                        Point point = new Point(x, y, segment);
//                        segment.add(point);
//
//                        points[y][x] = point;
//                    } else {
//                        List<Point> segment = new ArrayList<>();
//                        Point point = new Point(x, y, segment);
//                        segment.add(point);
//
//                        segmentList.add(segment);
//                        points[y][x] = point;
//                    }
//                } catch (Exception e) {
//                    break;
//                }
//                //System.out.println( "sI = " + sI.get(y, x) );
//            }
//        }

        int i = 0;
        FingerPrintChecker fingerPrintChecker = new FingerPrintChecker();
        fingerPrintChecker.setFingerprintTrain(fingerprintTrain);

        Integer countBullet = 0;


        for (List<Point> segment : segmentList) {
            Log.d("myLogs", "segment size = " + segment.size());
            if (segment.size() > 220 && segment.size() < 7000) {//1050 6341
                if(segmentWith0X(segment,sI.width())){
                    continue;
                }
                countBullet++;
                //Integer result = fingerPrintChecker.check(segment);
                for (Point point : segment) {
                    opencv_core.CvPoint v = new opencv_core.CvPoint(point.getX(), point.getY());
                    cvDrawCircle(orgImg, v, 1, opencv_core.CvScalar.BLUE, -1, 8, 0);
                }
//                if(result != 0){
//                    for (Point point : segment) {
//                        opencv_core.CvPoint v = new opencv_core.CvPoint(point.getX(), point.getY());
//                        cvDrawCircle(orgImg, v, 1, opencv_core.CvScalar.BLUE, -1, 8, 0);
//                    }
//                }
            }
            i++;
        }
        if(countBullet == 0){
            Toast.makeText(MainActivity.activity,"Выстрелов не найдено попробуйте подойти или поменять угол.",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivity.activity,"Найдено " + countBullet + " выстрелов",Toast.LENGTH_LONG).show();
        }

        Log.d("myLogs", "segmentList size = " + countBullet);


        //cvCanny(imgThreshold,imgThreshold,35,80, 3);

//        opencv_core.CvMemStorage storage = cvCreateMemStorage(0);
//        opencv_core.CvSeq lines = cvHoughLines2(imgThreshold, storage, CV_HOUGH_PROBABILISTIC, 1, Math.PI / 180, 100);
//
//        opencv_core.IplImage colorDst = cvCreateImage(cvGetSize(imgThreshold), imgThreshold.depth(), 3);

//        if(!lines.isNull()){
//            drawPoly(colorDst, lines);
//        }


        Frame frame1 = toMat.convert(orgImg);
        Bitmap bitmap1 = frameConverter.convert(frame1);
        return bitmap1;
    }
    private static boolean segmentWith0X(List<Point> segment, long width){
        for(Point point:segment){
            if(point.getX() == 0){
                return true;
            }
            if(point.getX() == width){
                return true;
            }
        }
        return false;
    }

    public static void drawPoly(opencv_core.IplImage image, final opencv_core.CvSeq Poly) {
        System.out.println(Poly.total());
        for (int i = 0; i < Poly.total(); i++) {
            opencv_core.CvPoint v = new opencv_core.CvPoint(cvGetSeqElem(Poly, i));
            cvDrawCircle(image, v, 2, opencv_core.CvScalar.BLUE, -1, 8, 0);
            System.out.println(" X value = " + v.x() + " ; Y value =" + v.y());
        }
    }


    public void colorDetect() {

    }
}



