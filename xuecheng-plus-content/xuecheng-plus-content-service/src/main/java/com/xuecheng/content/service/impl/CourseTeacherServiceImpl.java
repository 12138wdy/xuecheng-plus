package com.xuecheng.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;


    /**
     * 课程教师查询
     * @param courseId
     * @return
     */
    public List<CourseTeacher> getCourseTeacher(Long courseId) {

        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId,courseId);
        queryWrapper.orderByAsc(CourseTeacher::getCreateDate);

        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);

        return courseTeachers;
    }


    /**
     * 新增课程教师
     * @param courseTeacher
     * @return
     */
    public CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher) {
        //参数校验
        if (courseTeacher.getTeacherName() == null){
            throw new XueChengPlusException("教师姓名为空");
        }

        if (courseTeacher.getPosition() == null){
            throw new XueChengPlusException("教师职位为空");
        }

        //插入数据
        courseTeacher.setCreateDate(LocalDateTime.now());
        int insert = courseTeacherMapper.insert(courseTeacher);
        if (insert <= 0){
            throw new XueChengPlusException("新增失败");
        }

        //返回数据
        CourseTeacher teacher = courseTeacherMapper.selectById(courseTeacher.getId());

        return teacher;
    }
}
