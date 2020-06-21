package com.dickie.faceRecognition.common;

/**
 * This is Description
 *
 * @author yang
 * @date 2020/06/17
 */
public class Constant {

  public static final String FACE_CASCADE_MODEL_PATH = "src/main/resources/model/haarcascade_frontalface_alt.xml";

  public static final String TREE_EYES_CASCADE_MODEL_PATH = "src/main/resources/model/haarcascade_eye_tree_eyeglasses.xml";

  public static final String LEFT_EYE_CASCADE_MODEL_PATH = "src/main/resources/model/haarcascade_lefteye_2splits.xml";

  public static final String RIGHT_EYE_CASCADE_MODEL_PATH = "src/main/resources/model/haarcascade_righteye_2splits.xml";

  public static final String MODEL_PATH = "src/main/resources/model/model.xml";

  public static final String IMAGE_TEMP_SAVE_DIR = "/Users/public/Learn/machineLearning/train/upload/tmp/";

  public static final String IMAGE_SAVE_DIR = "/Users/public/Learn/machineLearning/train/upload/origin/";

  public static final String IMAGE_RESIZED_SAVE_DIR = "/Users/public/Learn/machineLearning/train/upload/faces/";

  public static final int IMAGE_WIDTH = 92;

  public static final int IMAGE_HEIGHT = 112;

  public static final int IMAGE_PADDING = 18;
}
