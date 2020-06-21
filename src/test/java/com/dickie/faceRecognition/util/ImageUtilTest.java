package com.dickie.faceRecognition.util;

import com.dickie.faceRecognition.core.ObjectDetector;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

class ImageUtilTest {

  @Test
  void processImageBeforeTrain() {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    Mat imread = Imgcodecs.imread("src/main/resources/static/me2.jpg");
    Rect rect = ObjectDetector.detectLargestFace(imread);
    List<Rect> rects = ObjectDetector.detectEyes(imread, rect);
    Imgcodecs.imwrite("src/main/resources/static/test-rotation.jpg",ImageUtil.rotation(imread,rects));
    Imgcodecs.imwrite("src/main/resources/static/test-norm.jpg",ImageUtil.norm0_255(imread));
    Mat gary = ImageUtil.cvtImg2Gary(imread);
    Imgcodecs.imwrite("src/main/resources/static/test-gary.jpg",gary);
    Mat resize = ImageUtil.resize(imread, 320);
    Imgcodecs.imwrite("src/main/resources/static/test-reszie.jpg",resize);
    CascadeClassifier faceDetector = new CascadeClassifier();
    boolean load = faceDetector.load("src/main/resources/model/haarcascade_frontalface_alt.xml");
    if (!load){
      System.out.println("load face xml failed!");
      System.exit(0);
    }
    MatOfRect faceDetections = new MatOfRect();
    faceDetector.detectMultiScale(imread, faceDetections);
    Mat rectangle = ImageUtil.rectangle(gary, faceDetections.toArray());
    Imgcodecs.imwrite("src/main/resources/static/test-rect.jpg",rectangle);

    MatOfRect matOfRect = ObjectDetector.detectFaces(imread);
    Mat rectangle2 = ImageUtil.rectangle(imread, matOfRect.toArray());
    Rect rect1 = matOfRect.toList().get(0);
    Mat rectangle3 = ImageUtil.rectangle(imread,ImageUtil.addPadding(rect1,20));
    Imgcodecs.imwrite("src/main/resources/static/test-rect2.jpg",rectangle2);
    Imgcodecs.imwrite("src/main/resources/static/test-rect3.jpg",rectangle3);
//    HighGui.imshow("bule",singleChannel(imread,2));
//    HighGui.waitKey(0);
  }
}