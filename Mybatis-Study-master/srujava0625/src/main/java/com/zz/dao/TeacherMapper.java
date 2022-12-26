package com.zz.dao;

import com.zz.pojo.SruTeacher;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface TeacherMapper {

    @Select("select * from teacher")
    List<SruTeacher> getAllTeacher();

    @Select("select MAX(tid) from teacher")
    int getMaxTeacherId();

    @Select("select * from teacher where tid = #{tid}")
    SruTeacher getTeacherById(@Param("tid") int tid);

    @Insert("insert into teacher (tid,password,name,sex,dataofbirth,college,course,email)values" +
            "(#{tid},#{password},#{name},#{sex},#{dataofbirth},#{college},#{course},#{email})")
    int addTeacher(SruTeacher sruTeacher);

    @Delete("delete from teacher where tid=#{tid}")
    int deleteTeacher(@Param("tid") int tid);

    @Update("update teacher set password=#{password},name=#{name},sex=#{sex},dataofbirth=#{dataofbirth},college=#{college},course=#{course},email=#{email} where tid = #{tid} " )
    int updateTeacher(SruTeacher sruTeacher);

}
