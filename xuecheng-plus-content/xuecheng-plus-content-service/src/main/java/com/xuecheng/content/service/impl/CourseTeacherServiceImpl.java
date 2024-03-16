package com.xuecheng.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
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
     *
     * @param courseId
     * @return
     */
    public List<CourseTeacher> getCourseTeacher(Long courseId) {

        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        queryWrapper.orderByAsc(CourseTeacher::getCreateDate);

        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);

        return courseTeachers;
    }


    /**
     * 修改，新增课程教师
     *
     * @param courseTeacher
     * @return
     */
    public CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher) {
        //参数校验
        if (courseTeacher.getTeacherName() == null) {
            throw new XueChengPlusException("教师姓名为空");
        }

        if (courseTeacher.getPosition() == null) {
            throw new XueChengPlusException("教师职位为空");
        }

        if (courseTeacher.getId() == null) {
            //插入数据
            courseTeacher.setCreateDate(LocalDateTime.now());
            int insert = courseTeacherMapper.insert(courseTeacher);
            if (insert <= 0) {
                throw new XueChengPlusException("新增失败");
            }

            //返回数据
            CourseTeacher teacher = courseTeacherMapper.selectById(courseTeacher.getId());
            return teacher;
        } else {
            //修改数据

            int update = courseTeacherMapper.updateById(courseTeacher);
            if (update <= 0) {
                throw new XueChengPlusException("新增失败");
            }

            CourseTeacher teacher = courseTeacherMapper.selectById(courseTeacher.getId());
            return teacher;
        }


    }

    /**
     * 删除课程教师
     *
     * @param id
     * @param courseId
     */
    public void deleteCourseTeacher(Long courseId, Long id) {

        //参数校验
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getId, id);
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);

        CourseTeacher teacher = courseTeacherMapper.selectOne(queryWrapper);

        if (teacher != null) {
            int delete = courseTeacherMapper.deleteById(id);
            if (delete <= 0){
                throw new XueChengPlusException("删除失败");
            }
        }
    }
}
