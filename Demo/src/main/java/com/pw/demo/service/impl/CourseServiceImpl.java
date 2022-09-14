package com.pw.demo.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pw.demo.entity.Course;
import com.pw.demo.mapper.CourseMapper;
import com.pw.demo.service.ICourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author p
 * @since 2022-01-13
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Autowired
    private  CourseMapper courseMapper;

    @Override
    public Page<Course> findPage(Page<Course> page, String name) {
        return courseMapper.findPage(page,name);
    }

    @Transactional
    @Override
    public void setStudentCourse(Integer courseId, Integer studentId) {
        courseMapper.delStudentCourse(courseId,studentId);
        courseMapper.setStudentCourse(courseId,studentId);
    }

}
