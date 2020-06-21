package com.dickie.faceRecognition.service;

import static com.dickie.faceRecognition.common.Constant.IMAGE_HEIGHT;
import static com.dickie.faceRecognition.common.Constant.IMAGE_WIDTH;

import com.dickie.faceRecognition.common.Constant;
import com.dickie.faceRecognition.common.Gender;
import com.dickie.faceRecognition.core.CameraCapture;
import com.dickie.faceRecognition.core.FaceRecognier;
import com.dickie.faceRecognition.core.FaceTrainer;
import com.dickie.faceRecognition.entity.Person;
import com.dickie.faceRecognition.entity.PersonImage;
import com.dickie.faceRecognition.mapper.PersonImageMapper;
import com.dickie.faceRecognition.mapper.PersonMapper;
import com.dickie.faceRecognition.util.FileUtil;
import com.dickie.faceRecognition.util.ImageUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * This is Description
 *
 * @author yang
 * @date 2020/06/20
 */
@Service
public class PersonService {

  @Autowired
  private PersonMapper personMapper;

  @Autowired
  private PersonImageMapper imageMapper;

  public void uploadModel(MultipartFile[] images, String name, Gender gender) {
    // save origin images.
    List<Mat> pictures = captureAndSaveOriginImages(images, name);
    // save processed images to database.
    int label = saveProcessedImages(name, gender, pictures);
    // training.
//    FaceTrainer.updateModel(pictures,label);
    uploadModelUseDatabase();
  }

  public boolean uploadModelUseDatabase() {
    List<PersonImage> images = imageMapper.findAll();
    List<Mat> pictures = new ArrayList<>();
    List<Integer> labels = new ArrayList<>();
    images.forEach(img -> {
      pictures.add(Imgcodecs.imread(img.getImagePath(),Imgcodecs.IMREAD_GRAYSCALE));
      labels.add(img.getLabel());
    });
    // training.
    return FaceTrainer.updateModel(pictures,labels);
  }

  public String recognition(MultipartFile... images){
    String basePath = Constant.IMAGE_TEMP_SAVE_DIR.concat("unknown").concat(File.separator);
    FileUtil.makeDirs(basePath);
    FileUtil.cleanDir(basePath);
    if (images == null || images.length == 0 || images[0] == null) {
      List<Mat> pictures = CameraCapture.capture(0, "recognition");
      pictures.forEach(pic -> {
        Imgcodecs.imwrite(basePath.concat(UUID.randomUUID().toString()).concat(".jpg"), pic);
      });
    } else {
      for (MultipartFile image : images) {
        try {
          image.transferTo(new File(basePath.concat(UUID.randomUUID().toString()).concat(image.getOriginalFilename().substring(image.getOriginalFilename().indexOf(".")))));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    List<Mat> readImages = FileUtil.readImages(basePath);
    List<Mat> processedImgs = new ArrayList<>();
    System.out.println("Start process face...");
    for (Mat pic : readImages) {
      if (pic.width() > IMAGE_WIDTH || pic.height() > IMAGE_HEIGHT) {
        Mat face = ImageUtil.preProcessImage(pic);
        if (face == null){
          continue;
        }
        Mat resizedFace = ImageUtil.resize(face, IMAGE_WIDTH, IMAGE_HEIGHT);
        processedImgs.add(resizedFace);
      } else {
        processedImgs.add(pic);
      }
    }
    if (processedImgs.size() < 1){
      return "No face detected! Please adjust your position and try again.";
    }
    int label = FaceRecognier.recognition(processedImgs.toArray(new Mat[]{}));
    System.out.println("Predict label:" + label);
    Person person = personMapper.findByLabel(label);
    if (person == null) {
      return "unknown people!";
    }
    FileUtil.moveFiles(basePath,Constant.IMAGE_SAVE_DIR.concat(person.getName()));
    String facesPath = Constant.IMAGE_RESIZED_SAVE_DIR.concat(person.getName()).concat(File.separator);
    FileUtil.makeDirs(facesPath);
    processedImgs.forEach(img -> {
      saveProcessedImage(label, person, facesPath, img);
    });
    return "Hello! ".concat(person.getName());
  }

  private void saveProcessedImage(int label, Person person, String facesPath, Mat img) {
    String faceFile = String.format("%s%s_%s.pgm", facesPath, UUID.randomUUID().toString(), label);
    Imgcodecs.imwrite(faceFile, img);
    PersonImage image = new PersonImage();
    image.setLabel(label);
    image.setPersonId(person.getId());
    image.setImagePath(faceFile);
    imageMapper.insertSelective(image);
  }

  private int saveProcessedImages(String name, Gender gender, List<Mat> pictures) {
    int label = personMapper.findLabelByNameOrLatestLabel(name);
    Person person = personMapper.findByName(name);
    if (person == null) {
      person = new Person();
      person.setName(name);
      person.setGender(gender.ordinal());
      person.setLabel(label);
      personMapper.insertSelective(person);
    }
    String facesPath = Constant.IMAGE_RESIZED_SAVE_DIR.concat(name).concat(File.separator);
    FileUtil.makeDirs(facesPath);
    for (Mat pic : pictures) {
      Mat face = ImageUtil.preProcessImage(pic);
      if (face == null){
        continue;
      }
      Mat resizedFace = ImageUtil.resize(face, IMAGE_WIDTH, IMAGE_HEIGHT);
      saveProcessedImage(label, person, facesPath, resizedFace);
    }
    return label;
  }

  private List<Mat> captureAndSaveOriginImages(MultipartFile[] images, String name) {
    String basePath = Constant.IMAGE_TEMP_SAVE_DIR.concat(name).concat(File.separator);
    FileUtil.makeDirs(basePath);
    FileUtil.cleanDir(basePath);
    if (images == null || images.length == 0) {
      List<Mat> pictures = CameraCapture.capture(0, name);
      pictures.forEach(pic -> {
        Imgcodecs.imwrite(basePath.concat(UUID.randomUUID().toString()).concat(".jpg"), pic);
      });
    } else {
      for (MultipartFile image : images) {
        try {
          image.transferTo(new File(basePath.concat(UUID.randomUUID().toString()).concat(image.getName().substring(image.getName().indexOf("\\.")))));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    List<Mat> pictures = FileUtil.readImages(basePath);
    FileUtil.moveFiles(basePath,Constant.IMAGE_SAVE_DIR.concat(name));
    return pictures;
  }

  public void cvtCVS2Database(String cvs) {
    List<String> images = new ArrayList<>();
    List<Integer> labels = new ArrayList<>();
    FileUtil.readCVSFile(cvs).forEach(line -> {
      String[] split = line.split(";");
      images.add(split[0]);
      labels.add(Integer.valueOf(split[1]));
    });
    if (!images.isEmpty() && images.size() == labels.size()) {
      Map<Integer, Person> personMap = distinct(labels);
      for (int i = 0; i < images.size(); i++) {
        PersonImage image = new PersonImage();
        image.setPersonId(personMap.get(labels.get(i)).getId());
        image.setImagePath(images.get(i));
        image.setLabel(labels.get(i));
        imageMapper.insertSelective(image);
      }
    } else {
      System.out.println("CVS file incorrect,Please check and try again.");
    }
  }

  private Map<Integer,Person> distinct(List<Integer> labels){
    Map<Integer,Person> map = new HashMap<>();
    for (int i = 0; i < labels.size(); i++) {
      if (!map.containsKey(labels.get(i))){
        Person person = new Person();
        person.setName(String.valueOf(labels.get(i)));
        person.setLabel(labels.get(i));
        person.setGender(Gender.MALE.ordinal());
        personMapper.insertSelective(person);
        map.put(labels.get(i),person);
      }
    }
    return map;
  }
}
