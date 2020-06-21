package com.dickie.faceRecognition.core;

import com.dickie.faceRecognition.util.ImageUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

class ObjectDetectorTest {

  @Test
  void detectEyes() {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    Mat me = Imgcodecs.imread("src/main/resources/static/me2.jpg");
    Imgcodecs.imwrite("src/main/resources/static/test-norm.jpg",ImageUtil.norm0_255(me));
    Rect face1 = ObjectDetector.detectLargestFace(me);
//    Mat fa = splitFace(me, face1);
    List<Rect> eyesR = ObjectDetector.detectEyes(me, face1);
    Mat eyes = ImageUtil.borderEyes(me,face1, eyesR.get(0),eyesR.get(1));
    Imgcodecs.imwrite("src/main/resources/static/eyes.jpg",eyes);

  }

  @Test
  void detectFaces() {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    Mat image = Imgcodecs.imread("src/main/resources/static/faces.jpg");
    MatOfRect faces = ObjectDetector.detectFaces(image);
    Mat rectangle = ImageUtil.rectangle(image, faces.toArray());
    Imgcodecs.imwrite("src/main/resources/static/faces-1.jpg",rectangle);
  }

  @Test
  void detectLargestFace() {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    Mat image = Imgcodecs.imread("src/main/resources/static/faces.jpg");
    Rect rect = ObjectDetector.detectLargestFace(image);
    Mat largest = ImageUtil.rectangle(image, rect);
    Imgcodecs.imwrite("src/main/resources/static/faces-3.jpg",largest);
    Mat face = ImageUtil.preProcessImage(image);
    Imgcodecs.imwrite("src/main/resources/static/faces-4.jpg",face);
  }
}