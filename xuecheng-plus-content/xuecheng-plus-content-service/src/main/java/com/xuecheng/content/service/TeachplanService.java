package com.xuecheng.content.service;


import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

public interface TeachplanService {
    /**
     * 课程计划查询
     * @param courseId
     * @return
     */
    List<TeachplanDto> findTeachplanTree(Long courseId);
}
