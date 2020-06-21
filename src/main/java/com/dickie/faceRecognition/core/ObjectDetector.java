package com.dickie.faceRecognition.core;

import com.dickie.faceRecognition.common.ContextHolder;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;

/**
 * This is Description
 *
 * @author yang
 * @date 2020/06/17
 */
public class ObjectDetector {

  @Deprecated
  public static Mat splitFace(Mat image, Rect face, boolean left){
    int faceX = face.x + face.width / 2;
    Mat submat;
//    System.out.println(image.rows() + ";" + image.cols() + ";" + faceX);
    if (left) {
      submat = image.submat(0, image.rows(), 0, faceX);
    } else {
      submat = image.submat(0, image.rows(), faceX, image.cols());
    }
    return submat;
  }

  @Deprecated
  public static Rect detectEye(Mat image){
    MatOfRect eyes = new MatOfRect();
    ContextHolder.getLeftEyeClassifier().detectMultiScale(image,eyes);
    List<Rect> rects = eyes.toList();
    System.out.println("Detect " + rects.size() + " eyes.");
    return rects.get(0);
  }


  public static List<Rect> detectEyes(Mat image,Rect face){
    Mat faceROI = image.submat(face);
    MatOfRect eyes = new MatOfRect();
    ContextHolder.getRightEyeClassifier().detectMultiScale(faceROI,eyes);
    List<Rect> rects = eyes.toList();
    if (rects.size() != 2){
      return new ArrayList<>();
    }
    return rects;
  }

  public static MatOfRect detectFaces(Mat image){
    MatOfRect faces = new MatOfRect();
    ContextHolder.getFaceClassifier().detectMultiScale(image,faces);
    List<Rect> rects = faces.toList();
    for (Rect rect : rects) {
      if (rect.x < 0) rect.x = 0;
      if (rect.y < 0) rect.y = 0;
      if (rect.x + rect.width > image.cols()) rect.x = image.cols() - rect.width;
      if (rect.y + rect.height > image.rows()) rect.y = image.rows() - rect.height;
    }
    return faces;
  }

  public static Rect detectLargestFace(Mat image){
    List<Rect> rects = detectFaces(image).toList();
    Rect rect = null;
    if (rects.isEmpty()) {
      System.out.println("No face detected!");
    } else {
      rect = rects.get(0);
    }
    return rect;
  }

}
