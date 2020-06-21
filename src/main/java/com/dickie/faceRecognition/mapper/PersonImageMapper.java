package com.dickie.faceRecognition.mapper;

import com.dickie.faceRecognition.entity.PersonImage;
import java.util.List;

public interface PersonImageMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PersonImage record);

    int insertSelective(PersonImage record);

    PersonImage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PersonImage record);

    int updateByPrimaryKey(PersonImage record);

    List<PersonImage> findAll();
}