package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@ToString
public class TeachplanDto extends Teachplan {

    //媒资信息
    private TeachplanMedia teachplanMedia;

    //课程计划list
    private List<TeachplanDto> teachPlanTreeNodes;
}
