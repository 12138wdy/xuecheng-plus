package com.xuecheng.content.service.impl;


import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;


    /**
     * 课程分类查询
     *
     * @param id
     * @return
     */
    public List<CourseCategoryTreeDto> selectTreeNodes(String id) {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);

        //查询得到的courseCategoryTreeDtos转为Map对象
        Map<String, CourseCategoryTreeDto> treeDtoMap = courseCategoryTreeDtos.stream()
                .filter(item -> !id.equals(item.getId())) //排除根节点
                .collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));

        //返回的集合
        List<CourseCategoryTreeDto> courseCategoryTreeDtoList = new ArrayList<>();

        //封装结果
        courseCategoryTreeDtos.stream()
                .filter(item -> !id.equals(item.getId()))//排除根节点
                .forEach(item -> {                          //循环得到数据
                    //添加同级元素，有相同的父节点
                    if (item.getParentid().equals(id)) {
                        courseCategoryTreeDtoList.add(item);
                    }
                    //不是同级元素，找到当前节点的父节点
                    CourseCategoryTreeDto courseCategoryTreeDto = treeDtoMap.get(item.getParentid());
                    //非空判断
                    if (courseCategoryTreeDto != null) {

                        if (courseCategoryTreeDto.getChildrenTreeNodes() == null) {
                            courseCategoryTreeDto.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                        }

                        //下边开始往ChildrenTreeNodes属性中放子节点
                        courseCategoryTreeDto.getChildrenTreeNodes().add(item);
                    }
                });

        return courseCategoryTreeDtoList;

    }
}
