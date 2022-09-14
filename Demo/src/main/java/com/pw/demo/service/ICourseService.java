package com.pw.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pw.demo.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author p
 * @since 2022-01-13
 */
public interface ICourseService extends IService<Course> {

    Page<Course> findPage(Page<Course> page, String name);

    void setStudentCourse(Integer courseId, Integer studentId);

//    void delStudentCourse(Integer courseId, Integer studentId);
}
