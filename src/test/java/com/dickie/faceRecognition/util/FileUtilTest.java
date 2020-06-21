package com.dickie.faceRecognition.util;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class FileUtilTest {

  @Test
  void getFileName() {
  }

  @Test
  void readImageFromPathAndMakeCSV() {
    String path = "src/main/resources/train/faces";
    List<String> list = new ArrayList<>();
    FileUtil.readImageFromPathAndMakeCSV(path,list);
    System.out.println(list);
    System.out.println(list.size());
    FileUtil.writeCVSFile("src/main/resources/train/faces/cvs.txt",list);
  }
}