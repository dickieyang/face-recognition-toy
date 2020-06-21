package com.dickie.faceRecognition;

import org.mybatis.spring.annotation.MapperScan;
import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dickie.faceRecognition.mapper")
public class FaceRecognitionApplication {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.setProperty("java.awt.headless", "false");
		SpringApplication.run(FaceRecognitionApplication.class, args);
	}

}
