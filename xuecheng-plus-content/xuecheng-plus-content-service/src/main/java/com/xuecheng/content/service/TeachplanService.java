package com.xuecheng.content.service;


import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

public interface TeachplanService {
    /**
     * 课程计划查询
     * @param courseId
     * @return
     */
    List<TeachplanDto> findTeachplanTree(Long courseId);

    /**
     * 新增，修改课程计划
     * @param saveTeachplanDto
     */
    void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * 根据id删除课程计划
     * @param id
     */
    void deleteTeachplan(Long id);

    /**
     * 上移课程计划
     * @param id
     */
    void moveUpTeachplan(Long id);

    /**
     * 下移课程计划
     * @param id
     */
    void moveDownTeachplan(Long id);
}
