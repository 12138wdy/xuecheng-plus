package com.xuecheng.content;



import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class teachPlanMapperTests {


    @Autowired
    private TeachplanMapper teachplanMapper;

    @Test
    public void testCourseBaseMapper(){

        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(117L);
        System.out.println(teachplanDtos);

    }

}
