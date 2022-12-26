package com.zz.dao;

import com.zz.pojo.SruStudent;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface StudentMapper {


    @Insert("insert into student (sid,password,name,sex,dataofbirth,college,major,stuclass)values" +
            "(#{sid},#{password},#{name},#{sex},#{dataofbirth},#{college},#{major},#{stuclass})")
    int addStudent(SruStudent sruStudent);

    @Select("select MAX(sid) from student")
    int getMaxStudentId();

    @Select("select * from student where sid = #{sid}")
    SruStudent getStudentById(@Param("sid") int sid);

    @Select("select * from student")
    List<SruStudent> getAllStudent();

    @Select("select * from student where stuclass = #{stuclass}")
    List<SruStudent> getStudentByStuclass(@Param("stuclass") String stuclass);



    @Update("update student set password=#{password},name=#{name},sex=#{sex},dataofbirth=#{dataofbirth},college=#{college},major=#{major},stuclass=#{stuclass} where sid = #{sid} " )
    int updateStudent(SruStudent sruStudent);

    @Delete("delete from student where sid=#{sid}")
    int deleteStudent(@Param("sid") int sid);

    @Select("select course.cid  , course.cname, teacher.tid,teacher.name,student.sid,student.name,course.stuclass ,score.score from teacher,student,score,course where student.sid = score.sid and teacher.tid = course.tid and course.cid = score.cid and student.sid = #{sid}")
    List<SruStudent> getstuCourseById(@Param("sid") int sid);
}
