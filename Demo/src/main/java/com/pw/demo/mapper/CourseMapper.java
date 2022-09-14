package com.pw.demo.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pw.demo.entity.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author p
 * @since 2022-01-13
 */
@Repository
public interface CourseMapper extends BaseMapper<Course> {

    Page<Course> findPage(Page<Course> page,@Param("name")String name);

    void delStudentCourse(@Param("courseId") Integer courseId, @Param("studentId") Integer studentId);
    void setStudentCourse(@Param("courseId") Integer courseId, @Param("studentId") Integer studentId);
}
