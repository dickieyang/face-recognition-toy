package com.dickie.faceRecognition.web;

import com.dickie.faceRecognition.common.Gender;
import com.dickie.faceRecognition.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * This is Description
 *
 * @author yang
 * @date 2020/06/20
 */
@RestController
@RequestMapping("api")
public class PersonController {

  @Autowired
  private PersonService personService;

  @PostMapping("update/model")
  public String updateModel(MultipartFile[] images,
      @RequestParam String name,
      @RequestParam(defaultValue = "MALE") Gender gender){
    personService.uploadModel(images, name, gender);
    return "success!";
  }

  @GetMapping("retrain/model")
  public String retrainModel(){
    personService.uploadModelUseDatabase();
    return personService.uploadModelUseDatabase() ? "success!" : "failed!";
  }

  @PostMapping("recognition")
  public String recognition(MultipartFile image){
    return personService.recognition(image);
  }

  @PostMapping("cvt/cvs")
  public String readCVS2Database(String cvs){
    personService.cvtCVS2Database(cvs);
    return "success!";
  }
}
