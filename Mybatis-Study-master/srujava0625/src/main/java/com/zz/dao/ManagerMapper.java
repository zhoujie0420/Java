package com.zz.dao;

import com.zz.pojo.SruAdmin;
import com.zz.pojo.SruCourse;
import com.zz.pojo.SruStudent;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ManagerMapper {



    @Select("select * from manager")
    List<SruAdmin> getAllManager();


    @Update("update manager set password=#{password},name=#{name},sex=#{sex},dataofbirth=#{dataofbirth} where mid = #{mid} " )
    int updateManager(SruAdmin sruAdmin);

    @Select("select * from manager where mid = #{mid}")
    SruAdmin getManagerById(@Param("mid") int mid);


}
