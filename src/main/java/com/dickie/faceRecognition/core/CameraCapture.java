package com.dickie.faceRecognition.core;

import com.dickie.faceRecognition.util.ImageUtil;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;
import org.springframework.util.StringUtils;

/**
 * This is Description
 *
 * @author yang
 * @date 2020/06/20
 */
public class CameraCapture {

  public static List<Mat> capture(Integer device, String name){
    if (device == null){
      device = 0;
    }
    VideoCapture capture = new VideoCapture();
    capture.open(device);
    if (!capture.isOpened()) {
      System.err.println("--(!)Error opening video capture");
      System.exit(0);
    }
    Mat frame = new Mat();
    int cnt = 102;
    List<Mat> images = new ArrayList<>();
    while (capture.read(frame) && cnt >= 0){
      if (frame.empty()) {
        System.err.println("--(!) No captured frame -- Break!");
        break;
      }
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (cnt % 5 == 0){
        Mat img = new Mat();
        frame.copyTo(img);
        images.add(img);
      }
      MatOfRect faces = ObjectDetector.detectFaces(frame);
      Mat processedFace = ImageUtil.rectangle(frame, faces.toArray());
      if (StringUtils.isEmpty(name))
        name = "Customer picture";
      HighGui.imshow(name,processedFace);
      if (HighGui.waitKey(10) == 27) {
        break;// escape
      }
      cnt--;
    }
    HighGui.destroyWindow(name);
    HighGui.destroyAllWindows();
    capture.release();
    return images;
  }
}
