package com.dickie.faceRecognition.common;

import org.opencv.core.CvException;
import org.opencv.objdetect.CascadeClassifier;

/**
 * This is Description
 *
 * @author yang
 * @date 2020/06/17
 */
public class ContextHolder {

  public static CascadeClassifier getFaceClassifier(){
    CascadeClassifier faceClassifier = new CascadeClassifier();
    if (!faceClassifier.load(Constant.FACE_CASCADE_MODEL_PATH))
      throw new CvException("Load face xml failed!");
    return faceClassifier;
  }

  public static CascadeClassifier getTreeEyesClassifier(){
    CascadeClassifier eyesClassifier = new CascadeClassifier();
    if (!eyesClassifier.load(Constant.TREE_EYES_CASCADE_MODEL_PATH))
      throw new CvException("Load tree eyes xml failed!");
    return eyesClassifier;
  }

  public static CascadeClassifier getLeftEyeClassifier(){
    CascadeClassifier eyesClassifier = new CascadeClassifier();
    if (!eyesClassifier.load(Constant.LEFT_EYE_CASCADE_MODEL_PATH))
      throw new CvException("Load left eye xml failed!");
    return eyesClassifier;
  }

  public static CascadeClassifier getRightEyeClassifier(){
    CascadeClassifier eyesClassifier = new CascadeClassifier();
    if (!eyesClassifier.load(Constant.RIGHT_EYE_CASCADE_MODEL_PATH))
      throw new CvException("Load right eye xml failed!");
    return eyesClassifier;
  }

}
