package com.dickie.faceRecognition.core;

import com.dickie.faceRecognition.common.Constant;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.FisherFaceRecognizer;
import org.opencv.utils.Converters;

/**
 * This is Description
 *
 * @author yang
 * @date 2020/06/17
 */
public class FaceTrainer {

  public static boolean updateModel(List<Mat> images,List<Integer> labels){
    if (images.size() == labels.size()){
      FaceRecognizer recognizer = getRecognizer();
      Mat label = Converters.vector_int_to_Mat(labels);
      recognizer.train(images,label);
      recognizer.save(Constant.MODEL_PATH);
      return true;
    }
    return false;
  }

  public static boolean updateModel(List<Mat> images,Integer label){
    if (images.size() > 0){
      FaceRecognizer recognizer = getRecognizer();
      Mat labels = convertLabel(label,images.size());
      recognizer.train(images,labels);
      recognizer.save(Constant.MODEL_PATH);
      return true;
    }
    return false;
  }

  public static FaceRecognizer trainModelAndSave(List<Mat> images,List<Integer> labels){
    Mat testSimple = images.remove(images.size() - 1);
    Integer testLabel = labels.remove(labels.size() - 1);
    FaceRecognizer recognizer = getRecognizer();
//    EigenFaceRecognizer recognizer = EigenFaceRecognizer.create();
    recognizer.train(images, Converters.vector_int_to_Mat(labels));
    recognizer.save(Constant.MODEL_PATH);
    int predictLabel = recognizer.predict_label(testSimple);
    System.out.println("Predict label:" + predictLabel);
    System.out.println("Actual label:" + testLabel);
    return recognizer;
  }


  public static Mat convertLabel(Integer label, int size) {
    Mat lbs = new Mat(size,1, CvType.CV_32SC1, new Scalar(0));
    for (int i = 0; i < size; i++) {
      lbs.put(i,0, new int[]{label});
    }
    return lbs;
  }

  public static FaceRecognizer getRecognizer(){
    FisherFaceRecognizer recognizer = FisherFaceRecognizer.create();
    if (Files.exists(Paths.get(Constant.MODEL_PATH))) {
      recognizer.read(Constant.MODEL_PATH);
    }
    return recognizer;
  }

}
