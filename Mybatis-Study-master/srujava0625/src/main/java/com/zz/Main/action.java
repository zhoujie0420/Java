package com.zz.Main;


import com.zz.dao.StudentMapper;
import com.zz.dao.TeacherMapper;
import com.zz.pojo.SruStudent;
import com.zz.pojo.SruTeacher;
import com.zz.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;

import java.io.*;
import java.lang.annotation.Target;
import java.util.Scanner;

public class action {
    static Scanner input = new Scanner(System.in);

    public static void actadmin() throws IOException {
        if (!Login.ManagerLogin()) {
            System.out.println("密码错误");
            Menu.menu();
        } else {
            while (true) {
                System.out.println("1.教师管理");
                System.out.println("2.学生管理");
                System.out.println("3.课程管理");
                System.out.println("4.信息维护");
                System.out.println("5.返回首页");
                int flag = input.nextInt();
                switch (flag) {
                    case 1:
                        adminAct.admin1();
                        break;
                    case 2:
                        adminAct.admin2();
                        break;
                    case 3:
                        adminAct.admin3();
                        break;
                    case 4:
                        adminAct.admin4();
                        break;
                    case 5:
                        Menu.menu();
                    default:
                        System.out.println("错误操作");
                }

            }
        }
    }

    ;

    public static void actteacher() throws IOException {
        if (!Login.TeacherLogin()) {
            System.out.println("密码错误");
            Menu.menu();
        } else {
            while (true) {
                System.out.println("1.录入成绩");
                System.out.println("2.统计成绩");
                System.out.println("3.查看成绩");
                System.out.println("4.信息维护");
                System.out.println("5.打印成绩");
                System.out.println("6.返回首页");
                int flag = input.nextInt();
                switch (flag) {
                    case 1:
                        uuaction.addScore();
                        break;
                    case 2:
                        uuaction.getAllScore();
                        break;
                    case 3:
                        uuaction.getScoreById();
                        break;
                    case 4:
                        System.out.println("1.查看信息");
                        System.out.println("2.修改信息");
                        int i = input.nextInt();
                        switch (i) {
                            case 1:
                                uuaction.TeagetTeacherById();
                                break;
                            case 2:
                                uuaction.teaupdateTeacher();
                                break;
                            default:
                                System.out.println("错误操作");
                        }
                        break;
                    case 5:
                        SqlSession sqlSession = MybatisUtils.getSqlSession();
                        try {
                            // 1.执行 getmapper
                            TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
                            SruTeacher teacher = teacherMapper.getTeacherById(Login.teacherId);

                            File f=new File("C:\\Users\\DELL\\Desktop\\java项目\\Mybatis-Study-master\\srujava0625\\src\\main\\java\\com\\zz\\Main\\b.txt");
                            FileOutputStream fos1=new FileOutputStream(f);
                            OutputStreamWriter dos1=new OutputStreamWriter(fos1);
                            dos1.write(teacher.toString());
                            dos1.close();
                            System.out.println("打印成功");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            //关闭
                            sqlSession.close();
                        }

                        break;
                    case 6:
                       Menu.menu();
                    default:
                        System.out.println("错误操作");
                }
            }

        }
    }

    ;

    public static void actstudent() throws IOException {
        if (!Login.StudentLogin()) {
            System.out.println("密码错误");
            Menu.menu();
        } else {
            while (true) {
                System.out.println("1.查看成绩单");
                System.out.println("2.信息维护");
                System.out.println("3.成绩单打印");
                int flag = input.nextInt();
                switch (flag) {
                    case 1:
                        uuaction.getstuCourseById();
                        break;
                    case 2:
                        System.out.println("1.查看信息");
                        System.out.println("2.修改信息");
                        int i = input.nextInt();
                        switch (i) {
                            case 1:
                                uuaction.stugetStudentById();
                                break;
                            case 2:
                                uuaction.stuupdateStudent();
                                break;
                            default:
                                System.out.println("错误操作");
                        }
                        break;
                    case 3:
                        SqlSession sqlSession = MybatisUtils.getSqlSession();
                        try {
                            // 1.执行 getmapper
                            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
                            SruStudent student = studentMapper.getStudentById(Login.studentId);

                            File f=new File("C:\\Users\\DELL\\Desktop\\java项目\\Mybatis-Study-master\\srujava0625\\src\\main\\java\\com\\zz\\Main\\a.txt");
                            FileOutputStream fos1=new FileOutputStream(f);
                            OutputStreamWriter dos1=new OutputStreamWriter(fos1);
                            dos1.write(student.toString());
                            dos1.close();
                            System.out.println("打印成功");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            //关闭
                            sqlSession.close();
                        }

                        break;
                    default:
                        System.out.println("错误操作");
                }

            }
        }
        ;
    }
}
