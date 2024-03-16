package com.xuecheng.content.service;


import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {


    /**
     * 课程教师查询
     * @param courseId
     * @return
     */
    List<CourseTeacher> getCourseTeacher(Long courseId);

    /**
     * 修改，新增课程教师
     * @param courseTeacher
     * @return
     */
    CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher);


}
