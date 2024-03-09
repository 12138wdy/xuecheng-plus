package com.xuecheng.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto courseParamsDto) {

        //构造分页查询条件

        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //名称条件查询
        queryWrapper.like(StringUtils.isNotEmpty(courseParamsDto.getCourseName()), CourseBase::getName, courseParamsDto.getCourseName());
        //课程审核状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, courseParamsDto.getAuditStatus());
        //课程发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getPublishStatus()), CourseBase::getStatus, courseParamsDto.getPublishStatus());

        //构造分页参数

        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        Page<CourseBase> coursePage = courseBaseMapper.selectPage(page, queryWrapper);
        long total = coursePage.getTotal();
        List<CourseBase> records = coursePage.getRecords();

        PageResult<CourseBase> pageResult = new PageResult<>(records, total, pageParams.getPageNo(), pageParams.getPageSize());

        return pageResult;
    }


    /**
     * 新增课程
     *
     * @param companyId
     * @param addCourseDto
     * @return
     */
    public CourseBaseInfoDto createCourseBase(long companyId, AddCourseDto addCourseDto) {

        //合法性校验
        /*if (StringUtils.isBlank(addCourseDto.getName())) {
            //throw new RuntimeException("课程名称为空");
            throw new XueChengPlusException("课程名称为空");
        }
        if (StringUtils.isBlank(addCourseDto.getMt())) {
            throw new XueChengPlusException("课程分类为空");
        }

        if (StringUtils.isBlank(addCourseDto.getSt())) {
            throw new XueChengPlusException("课程分类为空");
        }

        if (StringUtils.isBlank(addCourseDto.getGrade())) {
            throw new XueChengPlusException("课程等级为空");
        }

        if (StringUtils.isBlank(addCourseDto.getTeachmode())) {
            throw new XueChengPlusException("教育模式为空");
        }

        if (StringUtils.isBlank(addCourseDto.getUsers())) {
            throw new XueChengPlusException("适应人群为空");
        }

        if (StringUtils.isBlank(addCourseDto.getCharge())) {
            throw new XueChengPlusException("收费规则为空");
        }*/

        //新增课程对象
        CourseBase courseBase = new CourseBase();

        BeanUtils.copyProperties(addCourseDto, courseBase);
        //设置机构id
        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());
        //审核状态，未提交
        courseBase.setAuditStatus("202002");
        //发布状态，未发布
        courseBase.setStatus("203001");

        int insert = courseBaseMapper.insert(courseBase);
        //判断新增结果
        if (insert <= 0) {
            throw new XueChengPlusException("新增课程基本信息失败");
        }

        //新增营销对象
        CourseMarket courseMarket = new CourseMarket();
        Long courseId = courseBase.getId();
        BeanUtils.copyProperties(addCourseDto, courseMarket);
        courseMarket.setId(courseId);
        int saveCourseMarket = saveCourseMarket(courseMarket);

        if (saveCourseMarket <= 0) {
            throw new XueChengPlusException("新增课程营销信息失败");
        }

        //封装数据到 CourseBaseInfoDto
        return getCourseBaseInfo(courseId);
    }

    //根据课程id查询课程基本信息，包括基本信息和营销信息
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {

        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();

        //根据课程id查询课程基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase != null) {
            BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        }
        //根据课程id查询营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }

        //查询分类
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());
        return courseBaseInfoDto;

    }

    /**
     * 修改课程接口
     *
     * @param companyId
     * @param editCourseDto
     * @return
     */
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {

        //参数校验
        Long courseId = editCourseDto.getId();

        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengPlusException.exception("该课程信息不存在");
        }

        //校验本机构只能修改本机构的课程
        if (!companyId.equals(courseBase.getCompanyId())) {
            XueChengPlusException.exception("本机构只能修改本机构的课程");
        }

        //数据库更新
        BeanUtils.copyProperties(editCourseDto,courseBase);
        courseBase.setChangeDate(LocalDateTime.now());

        int i = courseBaseMapper.updateById(courseBase);
        if (i <= 0){
            XueChengPlusException.exception("更新失败");
        }

        //营销对象
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto,courseMarket);
        saveCourseMarket(courseMarket);

        //封装结果
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);

        return courseBaseInfo;
    }

    private int saveCourseMarket(CourseMarket courseMarket) {
        //参数合法校验
        //收费规则
        String charge = courseMarket.getCharge();
        //收费规则没有选择
        if (StringUtils.isBlank(charge)) {
            throw new XueChengPlusException("收费规则没有选择");
        }
        //选择为收费
        if (charge.equals("201001")) {
            if (courseMarket.getPrice() == null || courseMarket.getPrice().floatValue() <= 0) {
                throw new XueChengPlusException("收费价格异常");
            }
        }

        //判断是否有信息，如果有，更新，没有，插入
        CourseMarket market = courseMarketMapper.selectById(courseMarket.getId());
        if (market == null) {
            //插入
            return courseMarketMapper.insert(courseMarket);
        } else {
            //更新
            BeanUtils.copyProperties(market, courseMarket);
            courseMarket.setId(market.getId());
            return courseMarketMapper.updateById(courseMarket);
        }
    }

}
