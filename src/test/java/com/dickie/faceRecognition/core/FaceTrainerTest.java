package com.dickie.faceRecognition.core;

import com.dickie.faceRecognition.util.FileUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.face.FaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;

class FaceTrainerTest {

  @Test
  void trainModelAndSave() {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

//    FileUtil.cleanDir("src/main/resources/static/out/");
//    processImageBeforeTrain("src/main/resources/static/source/","src/main/resources/static/out/","yang");
//    List<String> cvsList = new ArrayList<>();
//    FileUtil.readImageFromPathAndMakeCSV("src/main/resources/static/out/",cvsList,40);
//    FileUtil.writeCVSFile("src/main/resources/model/cvs.txt",cvsList);
//    FileUtil.writeCVSFile("src/main/resources/model/cvs.txt",cvsList);
    List<Mat> images = new ArrayList<>();
    List<Integer> labels = new ArrayList<>();
    FileUtil.loadImagesFromCVS("train/cvs.txt",images,labels);
    FaceRecognizer recognizer = FaceTrainer.trainModelAndSave(images, labels);
    Mat mat = Imgcodecs.imread("./train/faces/s31/5.pgm", Imgcodecs.IMREAD_GRAYSCALE);
//    FaceRecognizer recognizer = getRecognizer();
    int predictLabel = recognizer.predict_label(mat);
    System.out.println("ppp " + predictLabel);
//    FaceRecognizer faceRecognizer = FaceTrainer.getRecognizer();
//    int label = faceRecognizer.predict_label(mat);
//    int recognition = FaceRecognier.recognition(mat);
//    System.out.println("uuu "+ label + "; " + recognition);
  }
}