package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class TeachplanServiceImpl implements TeachplanService {


    @Autowired
    private TeachplanMapper teachplanMapper;


    /**
     * 课程计划查询
     * @param courseId
     * @return
     */
    public List<TeachplanDto> findTeachplanTree(Long courseId) {

        List<TeachplanDto> teachPlanDto = teachplanMapper.selectTreeNodes(courseId);

        return teachPlanDto;
    }

    /**
     * 新增修改课程计划
     * @param saveTeachplanDto
     */
    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        //根据id查询课程计划
        Teachplan teachplan = teachplanMapper.selectById(saveTeachplanDto.getId());

        if (teachplan == null){
            //新增
            Teachplan teachplanNew = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto,teachplanNew);

            ////取出同父同级别的课程计划数量
            Long courseId = saveTeachplanDto.getCourseId();
            Long parentId = saveTeachplanDto.getParentid();
            Integer count = getTeachplanCount(courseId, parentId);

            teachplanNew.setOrderby(count + 1);

            teachplanMapper.insert(teachplanNew);
        }else {
            //更新
            Teachplan teachplanNew = teachplanMapper.selectById(saveTeachplanDto.getId());
            BeanUtils.copyProperties(saveTeachplanDto,teachplanNew);

            teachplanMapper.updateById(teachplanNew);
        }


    }

    private Integer getTeachplanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }
}
