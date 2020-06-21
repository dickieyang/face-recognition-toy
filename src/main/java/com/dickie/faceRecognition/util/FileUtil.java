package com.dickie.faceRecognition.util;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * This is Description
 *
 * @author yang
 * @date 2020/06/14
 */
public class FileUtil {

  public static List<Mat> readImages(String imageDir){
    File file = new File(imageDir);
    if (!file.isDirectory()) {
      throw new CvException("The input path is not a directory!");
    }
    File[] files = file.listFiles(new ImageFileFilter());
    return Stream.of(files)
        .map(img -> Imgcodecs.imread(img.getAbsolutePath(),Imgcodecs.IMREAD_GRAYSCALE))
        .collect(Collectors.toList());
  }

  public static Stream<String> readCVSFile(String filepath) {
    Path path = Paths.get(filepath);
    if (!Files.exists(path)) {
      throw new CvException("CVS file not exists!");
    }
    try {
      return Files.newBufferedReader(path).lines();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Stream.empty();
  }

  public static void loadImage(String imageDirPath, List<Mat> images, List<Integer> labels) {
    List<String> cvsList = new ArrayList<>();
    readImageFromPathAndMakeCSV(imageDirPath, cvsList);
    if (cvsList.isEmpty()) {
      throw new CvException("No images found!");
    }
    cvsList.forEach(cvs -> {
      String[] tmp = cvs.split(";");
      images.add(Imgcodecs.imread(tmp[0], Imgcodecs.IMREAD_GRAYSCALE));
      labels.add(Integer.valueOf(tmp[1]));
    });
  }

  public static void loadImagesFromCVS(String cvsFilepath, List<Mat> images, List<Integer> labels) {
    readCVSFile(cvsFilepath).forEach(cvs -> {
      String[] tmp = cvs.split(";");
      images.add(Imgcodecs.imread(tmp[0], Imgcodecs.IMREAD_GRAYSCALE));
      labels.add(Integer.valueOf(tmp[1]));
    });
  }

  public static Mat loadImagesFromCVS(String cvsFilepath, List<Mat> images) {
    List<String> cvsList = readCVSFile(cvsFilepath).collect(Collectors.toList());
    Mat labels = new Mat(images.size(),1, CvType.CV_32SC1, new Scalar(0));
    for (int i = 0; i < cvsList.size(); i++) {
      String[] tmp = cvsList.get(i).split(";");
      images.add(Imgcodecs.imread(tmp[0], Imgcodecs.IMREAD_GRAYSCALE));
      labels.put(i,0, new int[]{ Integer.valueOf(tmp[1]) });
    }
    return labels;
  }

  public static void writeCVSFile(String savePath, List<String> cvsList) {
    Path path = Paths.get(savePath);
    try {
      if (!Files.exists(path)) {
        Files.createDirectories(path.getParent());
      }
      BufferedWriter writer = Files.newBufferedWriter(path);
      writer.write(cvsList.stream().collect(Collectors.joining("\n")));
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void readImageFromPathAndMakeCSV(String dirPath, List<String> cvsList) {
    File file = new File(dirPath);
    if (!file.exists()) {
      return;
    }
    if (file.isDirectory()) {
      String[] names = file.list(new ImageFileFilter());
      Stream.of(names).forEach(name -> {
        cvsList.add(file.getAbsolutePath() + File.separator + name + ";" + name.split("\\.")[0]);
      });
    }
    File[] files = file.listFiles(new DirFilter());
    Stream.of(files).forEach(dir -> readImageFromPathAndMakeCSV(dir.getAbsolutePath(), cvsList));
  }

  public static void readImageFromPathAndMakeCSV(String dirPath, List<String> cvsList,int label) {
    File file = new File(dirPath);
    if (!file.exists()) {
      return;
    }
    if (file.isDirectory()) {
      String[] names = file.list(new ImageFileFilter());
      Stream.of(names).forEach(name -> {
        cvsList.add(file.getAbsolutePath() + File.separator + name + ";" + label);
      });
    }
    File[] files = file.listFiles(new DirFilter());
    Stream.of(files).forEach(dir -> readImageFromPathAndMakeCSV(dir.getAbsolutePath(), cvsList));
  }

  public static Path makeDirs(String dirPath){
    Path path = Paths.get(dirPath);
    if (!Files.exists(path)){
      try {
        Files.createDirectories(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return path;
  }

  public static void moveFiles(String fromPath,String toPath){
    Path from = makeDirs(fromPath);
    Path to = makeDirs(toPath);
    try {
      Files.list(from).forEach(file -> {
        try {
//          System.out.println("move file from " + fromPath + " to " + toPath);
          Files.move(file, to.resolve(file.getFileName()), REPLACE_EXISTING);
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void cleanDir(String dirPath){
    Path path = Paths.get(dirPath);
    if (Files.isDirectory(path)) {
      try {
        Files.list(path).forEach(file -> {
          try {
//            System.out.println(file.getFileName());
            Files.deleteIfExists(file);
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static class DirFilter implements FileFilter {

    @Override
    public boolean accept(File pathname) {
      return pathname.isDirectory();
    }
  }

  public static class ImageFileFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
      name = name.toLowerCase();
      return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".pgm") || name
          .endsWith(".png");
    }
  }
}
