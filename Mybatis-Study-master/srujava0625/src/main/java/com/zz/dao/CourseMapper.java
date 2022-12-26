package com.zz.dao;

import com.zz.pojo.SruCourse;
import com.zz.pojo.SruStudent;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CourseMapper {

    @Insert("insert into course (cid,cname,tid,stuclass)values" +
            "(#{cid},#{cname},#{tid},#{stuclass})")
    int addCourse(SruCourse sruCourse);


    @Select("select * from course")
    List<SruCourse> getAllCourse();

    @Select("select * from course where cid = #{cid}")
    SruCourse getCourseById(@Param("cid") int cid);

    @Select("select * from course where cname = #{cname}")
    List<SruCourse> getCourseByCname(@Param("cname") String cname);

    @Select("select * from course where tid = #{tid}")
    List<SruCourse> getCourseByTid(@Param("tid") int  tid);

    @Select("select * from course where stuclass = #{stuclass}")
    List<SruCourse> getCourseByStuclass(@Param("stuclass") String stuclass);

    @Select("select course.cid,course.cname,course.tid,teacher.college from course ,teacher where course.tid = teacher.tid and college=#{college}")
    List<SruCourse> getCourseByCollege(@Param("college") String college);


    @Update("update course set cname=#{cname},tid=#{tid},stuclass=#{stuclass} where cid = #{cid} " )
    int updateCourse(SruCourse sruCourse);

    @Delete("delete from course where cid=#{cid}")
    int deleteCourse(@Param("cid") int cid);
}
