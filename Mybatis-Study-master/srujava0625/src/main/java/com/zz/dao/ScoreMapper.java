package com.zz.dao;

import com.zz.pojo.SruCourse;
import com.zz.pojo.SruScore;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ScoreMapper {
    @Insert("insert into score (sid,cid,score)values" +
            "(#{sid},#{cid},#{score})")
    int addScore(SruScore sruScore);

    @Select("select sid from Course join Student on Course.stuClass=Student.stuClass where course.cid= #{cid} and course.tid= #{tid}\n" +
            "not in(select Score.sid,name from Score join Student on Score.sid = Student.sid where course.cid= #{cid})")
    List<Integer> getSidByCid(@Param("cid") int cid,@Param("tid") int tid);

    @Select("select avg(score) from score where cid = #{cid} ")
    int getAvg(@Param("cid") int cid);

    @Select("select count(score>90) from score where cid = #{cid} ")
    int getgood(@Param("cid") int cid);

    @Select("select count(*) from score where cid = #{cid} ")
    int getAll(@Param("cid") int cid);

    @Select("select count(score>60) from score where cid = #{cid} ")
    int getqualified(@Param("cid") int cid);
}
