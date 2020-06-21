package com.dickie.faceRecognition.util;

import com.dickie.faceRecognition.common.Constant;
import com.dickie.faceRecognition.core.ObjectDetector;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * This is Description
 *
 * @author yang
 * @date 2020/06/16
 */
public class ImageUtil {

  private static final double DESIRED_LEFT_EYE_X = 0.16;
  private static final double DESIRED_LEFT_EYE_Y = 0.14;

  public static Mat norm0_255(Mat image){
    Mat mat = new Mat();
    if (image.channels() == 1){
      Core.normalize(image,mat,0,255, Core.NORM_MINMAX, CvType.CV_8UC1);
    } else if (image.channels() == 3) {
      Core.normalize(image,mat,0,255,Core.NORM_MINMAX,CvType.CV_8UC3);
    } else {
      image.copyTo(mat);
    }
    return mat;
  }

  /**
   * Not useful because of it can't distinguish left-eye and right-eye.
   * @param image
   * @param eyes
   * @return
   */
  public static Mat rotation(Mat image,List<Rect> eyes){
    Rect leftEye = eyes.get(0);
    Rect rightEye = eyes.get(1);
//    Point EyeCenter = new Point((leftEye.x + rightEye.x) / 2, (leftEye.y + rightEye.y) / 2);
    double dy = Math.abs(leftEye.y - rightEye.y);
    double dx = Math.abs(leftEye.x - rightEye.x);
    double len = Math.sqrt(dy * dy + dx * dx);

    double angle =  Math.atan2(dy,dx) * 180 / Math.PI;
    System.out.println("angle="+angle);
    // Hand measurements shown that the left eye center should ideally be at roughly (0.19, 0.14) of a scaled face image.
    final double DESIRED_RIGHT_EYE_X = (1.0f - DESIRED_LEFT_EYE_X);
    // Get the amount we need to scale the image to be the desired fixed size we want.
    double desiredLen = (DESIRED_RIGHT_EYE_X - DESIRED_LEFT_EYE_X) * 100;
    double scale = desiredLen / len;
    System.out.println("scale="+scale);
    Mat matrix2D = Imgproc.getRotationMatrix2D(new Point(image.cols()/2,image.rows()/2), angle, 1);
    Mat dest = new Mat();
    Imgproc.warpAffine(image,dest,matrix2D,new Size(image.cols(),image.rows()));
    return dest;
  }

  public static Mat cvtImg2Gary(Mat image) {
    Mat gary = new Mat();
    if (image.channels() == 3) {
      Imgproc.cvtColor(image, gary, Imgproc.COLOR_BGR2GRAY);
    } else if (image.channels() == 4) {
      Imgproc.cvtColor(image, gary, Imgproc.COLOR_BGRA2GRAY);
    } else {
      gary = image;
    }
    Imgproc.equalizeHist(gary, gary);
    return gary;
  }

  public static Mat resize(Mat image,int scaledWidth){
    Mat dest = new Mat();
    if (image.cols() > scaledWidth) {
      float scale = image.cols() / scaledWidth;
      int height = Math.round(image.rows() / scale);
      Imgproc.resize(image, dest, new Size(scaledWidth, height));
    } else {
      dest = image;
    }
    return dest;
  }

  public static Mat resize(Mat image,int width, int height){
    Mat dest = new Mat();
    Imgproc.resize(image,dest,new Size(width,height),0,0, Imgproc.INTER_LINEAR);
    return dest;
  }

  public static Mat rectangle(Mat image, Rect... listOfFaces){
    if (listOfFaces == null || listOfFaces.length == 0){
      return image;
    }
    Mat dest = new Mat();
    image.copyTo(dest);
    for (Rect rect : listOfFaces) {
      Imgproc.rectangle(dest,new Point(rect.x,rect.y),new Point(rect.x + rect.width,rect.y + rect.height),new Scalar(0,0,255));
    }
    return dest;
  }

  public static Mat borderEyes(Mat image,Rect face, Rect ... listOfEyes){
    Mat dst = new Mat();
    image.copyTo(dst);
    for (Rect eye : listOfEyes) {
      Point eyeCenter = new Point(face.x + eye.x + eye.width / 2, face.y + eye.y + eye.height / 2);
      int radius = (int) Math.round((eye.width + eye.height) * 0.25);
      Imgproc.circle(dst, eyeCenter, radius, new Scalar(255, 0, 0), 4);
    }
    return dst;
  }

  public static Mat preProcessImage(Mat image){
//    System.out.println("Start process face...");
    Mat norm0255 = norm0_255(image);
    Rect rect = ObjectDetector.detectLargestFace(norm0255);
    if (rect != null && rect.width > 0) {
      if (norm0255.cols() > rect.width + 36) {
        rect = addPadding(rect, Constant.IMAGE_PADDING);
      }
      Mat face = new Mat(norm0255, rect);
      return cvtImg2Gary(face);
    } else {
      System.out.println("No face found,will be return a null mat");
      return null;
    }
  }

  public static Rect addPadding(Rect rect,int padding){
//    System.out.println(rect.x + ";" + rect.y + ";" + rect.width + ";" +rect.height);
    return new Rect(rect.x - padding + 1,rect.y - padding + 1,rect.width + padding * 2,rect.height + padding * 2);
  }

  public static void processImageBeforeTrain(String imgDir,String postImgDir,String label){
    List<Mat> images = FileUtil.readImages(imgDir);
    images.forEach(image -> {
      Mat face = ImageUtil.preProcessImage(image);
      Mat resizedFace = ImageUtil.resize(face, Constant.IMAGE_WIDTH,Constant.IMAGE_HEIGHT);
      Imgcodecs.imwrite(String.format("%s%s_%s.pgm", postImgDir, System.nanoTime(),label), resizedFace);
    });
  }

}
