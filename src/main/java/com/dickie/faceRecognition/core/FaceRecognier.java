package com.dickie.faceRecognition.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import org.opencv.core.Mat;

/**
 * This is Description
 *
 * @author yang
 * @date 2020/06/20
 */
public class FaceRecognier {

  public static int recognition(Mat... images){
    Map<Integer,Integer> map = new HashMap<>();
    Stream.of(images)
        .forEach(image -> {
          int predictLabel = FaceTrainer.getRecognizer().predict_label(image);
          System.out.println("Predict " + predictLabel);
          Integer cnt = map.get(predictLabel);
          if (cnt == null) {
            map.put(predictLabel, 1);
          } else {
            map.put(predictLabel,cnt + 1);
          }
        });
    int label = 0, cnt = 0;
    for (Entry<Integer, Integer> entry : map.entrySet()) {
      if (cnt < entry.getValue()){
        cnt = entry.getValue();
        label = entry.getKey();
      }
    }
    return label;
  }

}
