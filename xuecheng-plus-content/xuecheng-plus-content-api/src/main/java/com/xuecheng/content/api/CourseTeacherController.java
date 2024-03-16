package com.xuecheng.content.api;


import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "课程教师接口", tags = "课程教师接口")
@RestController
public class CourseTeacherController {

    @Autowired
    private CourseTeacherService courseTeacherService;



    @ApiOperation("课程教师查询")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getCourseTeacher(@PathVariable Long courseId){
        List<CourseTeacher> courseTeacher = courseTeacherService.getCourseTeacher(courseId);

        return courseTeacher;
    }


    @ApiOperation("修改，新增课程教师")
    @PostMapping("/courseTeacher")
    public CourseTeacher saveCourseTeacher(@RequestBody CourseTeacher courseTeacher){
        CourseTeacher teacher = courseTeacherService.saveCourseTeacher(courseTeacher);

        return teacher;
    }


    @ApiOperation("删除课程教师")
    @DeleteMapping("/courseTeacher/course/{courseId}/{id}")
    public void deleteCourseTeacher(@PathVariable Long courseId,@PathVariable Long id){

        courseTeacherService.deleteCourseTeacher(courseId,id);
    }




}
