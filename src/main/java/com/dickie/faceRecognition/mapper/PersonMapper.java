package com.dickie.faceRecognition.mapper;

import com.dickie.faceRecognition.entity.Person;

public interface PersonMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Person record);

    int insertSelective(Person record);

    Person selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Person record);

    int updateByPrimaryKey(Person record);

    int findLabelByNameOrLatestLabel(String name);

    Person findByName(String name);

    Person findByLabel(int label);
}