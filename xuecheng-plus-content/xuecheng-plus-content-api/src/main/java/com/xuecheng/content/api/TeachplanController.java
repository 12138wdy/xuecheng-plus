package com.xuecheng.content.api;


import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "课程计划接口", tags = "课程计划接口")
@RestController
public class TeachplanController {


    @Autowired
    private TeachplanService teachplanService;


    @ApiOperation("课程计划查询")
    @ApiImplicitParam(value = "courseId",name = "课程Id",required = true,dataType = "Long",paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        List<TeachplanDto> teachplanDto =  teachplanService.findTeachplanTree(courseId);
        return teachplanDto;
    }


    @ApiOperation("修改新增课程计划")
    @PostMapping("/teachplan")
    public void saveTeachplan( @RequestBody SaveTeachplanDto saveTeachplanDto){
        teachplanService.saveTeachplan(saveTeachplanDto);
    }

}
