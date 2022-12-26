package com.zz.Main;

import com.zz.dao.ManagerMapper;
import com.zz.dao.StudentMapper;
import com.zz.dao.TeacherMapper;
import com.zz.pojo.SruAdmin;
import com.zz.pojo.SruStudent;
import com.zz.pojo.SruTeacher;
import com.zz.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;

import java.util.Scanner;



public class Login {

    static Scanner input = new Scanner(System.in);
    public static int managerId;
    public static int teacherId;
    public static int studentId;
    public static boolean ManagerLogin(){
        System.out.println("输入用户名");
        managerId = input.nextInt();
        boolean flag = false;
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            ManagerMapper managerMapper = sqlSession.getMapper(ManagerMapper.class);
            SruAdmin manager = managerMapper.getManagerById(managerId);
            if(manager == null) {
                System.out.println("没有此id");
            }else{
                System.out.println("输入密码");
                String password = input.next();
                if(password.equals(manager.getPassword())){
                    flag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
        return flag;
    }

    public static boolean TeacherLogin(){
        System.out.println("输入用户名");
        teacherId = input.nextInt();
        boolean flag = false;
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
            SruTeacher teacher = teacherMapper.getTeacherById(teacherId);
            if(teacher == null) {
                System.out.println("没有此id");
            }else{
                System.out.println("输入密码");
                String password = input.next();
                if(password.equals(teacher.getPassword())){
                    flag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
        return flag;
    }

    public static boolean StudentLogin(){
        System.out.println("输入用户名");
        studentId = input.nextInt();
        boolean flag = false;
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            SruStudent student = studentMapper.getStudentById(studentId);
            if(student == null) {
                System.out.println("没有此id");
            }else{
                System.out.println("输入密码");
                String password = input.next();
                if(password.equals(student.getPassword())){
                    flag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
        return flag;
    }

}
