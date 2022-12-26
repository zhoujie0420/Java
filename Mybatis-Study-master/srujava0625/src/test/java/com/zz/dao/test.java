package com.zz.dao;

import com.zz.pojo.SruAdmin;
import com.zz.pojo.SruCourse;
import com.zz.pojo.SruStudent;
import com.zz.pojo.SruTeacher;
import com.zz.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;
import java.util.Scanner;

public class test {
    Scanner in = new Scanner(System.in);

    @Test
    public void getAllTeacher() {
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
            List<SruTeacher> teacherList = teacherMapper.getAllTeacher();
            for (SruTeacher teacher : teacherList) {
                System.out.println(teacher);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    @Test
    public void getTeacherById() {
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
            SruTeacher teacher = teacherMapper.getTeacherById(6);
            System.out.println(teacher);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    @Test
    public void addTeacher() {
        // 获得sqlsession对象

        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
            teacherMapper.addTeacher(new SruTeacher(0, "a", "周二", "女", "2000-04-20", "b", "b", "b"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    @Test
    public void deleteTeacher() {

        int i = in.nextInt();
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
            teacherMapper.deleteTeacher(i);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    @Test
    public void updateTeacher() {

        Scanner input = new Scanner(System.in);
        int id = input.nextInt();
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);

            teacherMapper.updateTeacher(new SruTeacher(id, "c", "c", "c", "c", "c", "c", "c"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    @Test
    public void addStudent() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            studentMapper.addStudent(new SruStudent(0, "s", "学生2", "男", "1999-04-20", "b", "b", "b"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void getStudentById() {
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            SruStudent student = studentMapper.getStudentById(1);
            System.out.println(student);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    @Test
    public void getAllStudent() {
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            List<SruStudent> studentsList = studentMapper.getAllStudent();
            for (SruStudent student : studentsList) {
                System.out.println(student);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    @Test
    public void getStudentByStuclass() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            List<SruStudent> studentsList = studentMapper.getStudentByStuclass("b");
            for (SruStudent student : studentsList) {
                System.out.println(student);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void deleteStudent() {

        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            studentMapper.deleteStudent(1);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    @Test
    public void updateStudent() {

        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

            studentMapper.updateStudent(new SruStudent(2, "c", "c", "c", "2001-04-20", "c", "c", "c"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }


    @Test
    public void addCourse() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            courseMapper.addCourse(new SruCourse(1, "kexue", 3, "b"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void getCourseById() {
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            SruCourse course = courseMapper.getCourseById(1);
            System.out.println(course);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    @Test
    public void getAllCourse() {
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            List<SruCourse> coursesList = courseMapper.getAllCourse();
            for (SruCourse course : coursesList) {
                System.out.println(course);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    @Test
    public void getCourseByCname() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            List<SruCourse> coursesList = courseMapper.getCourseByCname("kexue");
            for (SruCourse course : coursesList) {
                System.out.println(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void getCourseByTid() {
        int i = in.nextInt();
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            List<SruCourse> coursesList = courseMapper.getCourseByTid(i);
            for (SruCourse course : coursesList) {
                System.out.println(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void getCourseByStuclass() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            List<SruCourse> coursesList = courseMapper.getCourseByStuclass("b");
            for (SruCourse course : coursesList) {
                System.out.println(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void getCourseByCollege() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            List<SruCourse> coursesList = courseMapper.getCourseByCollege("b");
            for (SruCourse course : coursesList) {
                System.out.println(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void deleteCourse() {

        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            courseMapper.deleteCourse(1);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    @Test
    public void updateCourse() {

        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            courseMapper.updateCourse(new SruCourse(1, "c", 4, "c"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }


    @Test
    public void getAllManager() {
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            ManagerMapper managerMapper = sqlSession.getMapper(ManagerMapper.class);
            List<SruAdmin> managersList = managerMapper.getAllManager();
            for (SruAdmin manager : managersList) {
                System.out.println(manager);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }


    @Test
    public void updateManager() {

        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            ManagerMapper managerMapper = sqlSession.getMapper(ManagerMapper.class);
            managerMapper.updateManager(new SruAdmin(1, "b", "b", "b", "2011-04-20"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    @Test
    public void getManagerById() {
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            ManagerMapper managerMapper = sqlSession.getMapper(ManagerMapper.class);
            SruAdmin manager = managerMapper.getManagerById(2);
            System.out.println(manager);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }


}
