package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
public class TeachplanServiceImpl implements TeachplanService {


    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;


    /**
     * 课程计划查询
     *
     * @param courseId
     * @return
     */
    public List<TeachplanDto> findTeachplanTree(Long courseId) {

        List<TeachplanDto> teachPlanDto = teachplanMapper.selectTreeNodes(courseId);

        return teachPlanDto;
    }

    /**
     * 新增修改课程计划
     *
     * @param saveTeachplanDto
     */
    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        //根据id查询课程计划
        Teachplan teachplan = teachplanMapper.selectById(saveTeachplanDto.getId());

        if (teachplan == null) {
            //新增
            Teachplan teachplanNew = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplanNew);

            ////取出同父同级别的课程计划数量
            Long courseId = saveTeachplanDto.getCourseId();
            Long parentId = saveTeachplanDto.getParentid();
            Integer count = getTeachplanCount(courseId, parentId);

            teachplanNew.setOrderby(count + 1);

            teachplanMapper.insert(teachplanNew);
        } else {
            //更新
            Teachplan teachplanNew = teachplanMapper.selectById(saveTeachplanDto.getId());
            BeanUtils.copyProperties(saveTeachplanDto, teachplanNew);

            teachplanMapper.updateById(teachplanNew);
        }


    }

    /**
     * 根据id删除课程计划
     *
     * @param id
     */
    @Transactional
    public void deleteTeachplan(Long id) {
        //根据id查询课程计划
        Teachplan teachplan = teachplanMapper.selectById(id);

        if (teachplan.getParentid() == 0) {
            //大章节

            //判断大章节下面是否有小章节
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid, id);
            List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);

            if (!teachplans.isEmpty()) {
                //大章节下面有小章节
                throw new XueChengPlusException("课程计划信息还有子级信息，无法操作");
            }

            int delete = teachplanMapper.deleteById(id);
            if (delete <= 0) {
                throw new XueChengPlusException("删除章节失败");
            }

        } else {
            //小章节
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getTeachplanId, id);

            teachplanMediaMapper.delete(queryWrapper);
            int deleted = teachplanMapper.deleteById(id);
            if (deleted <= 0) {
                throw new XueChengPlusException("删除小节失败");
            }
        }

    }

    /**
     * 上移课程计划
     *
     * @param id
     */
    public void moveUpTeachplan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);

        if (teachplan.getParentid() == 0) {
            //大章节
            if (teachplan.getOrderby() > 1) {
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getOrderby, teachplan.getOrderby() - 1);
                queryWrapper.eq(Teachplan::getGrade, teachplan.getGrade());
                queryWrapper.eq(Teachplan::getCourseId, teachplan.getCourseId());

                Teachplan teachplanNew = teachplanMapper.selectOne(queryWrapper);
                teachplanNew.setOrderby(teachplanNew.getOrderby() + 1);

                teachplanMapper.updateById(teachplanNew);

                teachplan.setOrderby(teachplan.getOrderby() - 1);
                int update = teachplanMapper.updateById(teachplan);
                if (update <= 0) {
                    throw new XueChengPlusException("上移章节失败");
                }
            }
        } else {
            //小章节
            if (teachplan.getOrderby() > 1) {
                LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Teachplan::getOrderby, teachplan.getOrderby() - 1);
                queryWrapper.eq(Teachplan::getGrade, teachplan.getGrade());
                queryWrapper.eq(Teachplan::getParentid, teachplan.getParentid());

                Teachplan teachplanNew = teachplanMapper.selectOne(queryWrapper);
                teachplanNew.setOrderby(teachplanNew.getOrderby() + 1);

                teachplanMapper.updateById(teachplanNew);

                teachplan.setOrderby(teachplan.getOrderby() - 1);
                int update = teachplanMapper.updateById(teachplan);
                if (update <= 0) {
                    throw new XueChengPlusException("上移小节失败");
                }
            }
        }
    }

    /**
     * 下移课程计划
     *
     * @param id
     */
    public void moveDownTeachplan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);

        if (teachplan.getParentid() == 0) {
            //大章节
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getOrderby, teachplan.getOrderby() + 1);
            queryWrapper.eq(Teachplan::getGrade, teachplan.getGrade());
            queryWrapper.eq(Teachplan::getCourseId, teachplan.getCourseId());

            Teachplan teachplanNew = teachplanMapper.selectOne(queryWrapper);

            teachplanNew.setOrderby(teachplanNew.getOrderby() - 1);

            teachplanMapper.updateById(teachplanNew);

            teachplan.setOrderby(teachplan.getOrderby() + 1);
            int update = teachplanMapper.updateById(teachplan);
            if (update <= 0) {
                throw new XueChengPlusException("下移章节失败");
            }
        } else {
            //小章节
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getOrderby, teachplan.getOrderby() + 1);
            queryWrapper.eq(Teachplan::getGrade, teachplan.getGrade());
            queryWrapper.eq(Teachplan::getParentid, teachplan.getParentid());

            Teachplan teachplanNew = teachplanMapper.selectOne(queryWrapper);
            teachplanNew.setOrderby(teachplanNew.getOrderby() - 1);

            teachplanMapper.updateById(teachplanNew);

            teachplan.setOrderby(teachplan.getOrderby() + 1);
            int update = teachplanMapper.updateById(teachplan);
            if (update <= 0) {
                throw new XueChengPlusException("下移小节失败");
            }
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
