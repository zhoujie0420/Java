package com.zz.Main;

import com.zz.dao.*;
import com.zz.pojo.*;
import com.zz.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Scanner;

public class uuaction {



    static Scanner in = new Scanner(System.in);
//获取最大id
    public static void getMaxTeacherId(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
            int maxTeacherId = teacherMapper.getMaxTeacherId();
                System.out.println(maxTeacherId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    //教师管理
//添加教师信息
    //addTeacher

    public static void addTeacher() {
        // 获得sqlsession对象
        System.out.println("请输入教师密码、姓名、性别、出生日期、所属学院、担任课程、邮箱地址：(','分隔)");
        String line = in.next();
        String[] strings = new String[0];
        try {
            strings = line.split(",");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("请用逗号分隔！");
        }
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
            teacherMapper.addTeacher(new SruTeacher(0,strings[0], strings[1], strings[2], strings[3], strings[4], strings[5], strings[6]));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    //查询教师信息
    //所有
    public static void getAllTeacher() {
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

    //by id
    public static boolean getTeacherById() {
        // 获得sqlsession对象
        int i = in.nextInt();
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
            SruTeacher teacher = teacherMapper.getTeacherById(i);
            if(teacher == null) {
                System.out.println("没有此id");
                return false;
            }
            System.out.println(teacher);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
        return true;
    }

    //修改教师信息
    //updateTeacher
    public static void updateTeacher() {
        // 获得sqlsession对象
        System.out.println("请输入教师账号、密码、姓名、性别、出生日期、所属学院、担任课程、邮箱地址：(','分隔)");
        String line = in.next();
        String[] strings = new String[0];
        try {
            strings = line.split(",");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("请用逗号分隔！");
        }

        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);

            teacherMapper.updateTeacher(new SruTeacher(Integer.parseInt(strings[0]), strings[1], strings[2], strings[3], strings[4], strings[5], strings[6],strings[7]));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    //删除教师信息
    //deleteTeacher
    public static void deleteTeacher() {
        System.out.println("输入删除id");
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



    //获取最大id
    public static void getMaxStudentId(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            int maxStudentId = studentMapper.getMaxStudentId();

            System.out.println(maxStudentId);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }
    //学生管理
//添加学生信息
    //addStudent
    public static void addStudent() {
        System.out.println("请输入学生密码、姓名、性别、出生日期、所属学院、专业、班级：(','分隔)");
        String line = in.next();
        String[] strings = new String[0];
        try {
            strings = line.split(",");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("请用逗号分隔！");
        }


        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            studentMapper.addStudent(new SruStudent(0,strings[0], strings[1], strings[2], strings[3], strings[4], strings[5], strings[6]));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    //查询学生信息
    //getStudent
//单个byid
    public static void getStudentById() {
        int i = in.nextInt();
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            SruStudent student = studentMapper.getStudentById(i);
            if(student == null) {
                System.out.println("没有此id");
            }else {
                System.out.println(student);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    //所有
    public static void getAllStudent() {
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

    //byclass
    public static void getStudentByStuclass() {
        String classname = in.next();
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            List<SruStudent> studentsList = studentMapper.getStudentByStuclass(classname);
            if(studentsList == null) {
                System.out.println("此课程不存在");
            }else {
                for (SruStudent student : studentsList) {
                    System.out.println(student);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    //修改学生信息
    //updateStudent
    public static void updateStudent() {

        System.out.println("请输入学生账号、密码、姓名、性别、出生日期、所属学院、专业、班级：(','分隔)");
        String line = in.next();
        String[] strings = new String[0];
        try {
            strings = line.split(",");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("请用逗号分隔！");
        }

        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

            studentMapper.updateStudent(new SruStudent(Integer.parseInt(strings[0]), strings[1], strings[2], strings[3], strings[4], strings[5], strings[6],strings[7]));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    //删除学生信息
    //deleteStudent
    public static void deleteStudent() {
        System.out.println("输入删除id");
        int i = in.nextInt();
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            studentMapper.deleteStudent(i);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    //课程管理

//添加课程
    //addCourse
    public static void addCourse() {
        System.out.println("请输入课程名、教师工号、班级：(','分隔)");
        String line = in.next();
        String[] strings = new String[0];
        try {
            strings = line.split(",");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("请用逗号分隔！");
        }


        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            courseMapper.addCourse(new SruCourse(0, strings[0], Integer.parseInt(strings[1]), strings[2]));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    //查询课程
    //getCourse
    //buid
    public static void getCourseById() {
        int i = in.nextInt();
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            SruCourse course = courseMapper.getCourseById(i);
            System.out.println(course);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    //all
    public static void getAllCourse() {
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


    //byname
    public static void getCourseByCname() {
        String cname = in.next();
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            List<SruCourse> coursesList = courseMapper.getCourseByCname(cname);
            for (SruCourse course : coursesList) {
                System.out.println(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    //bytid
    public static void getCourseByTid() {
        int id = in.nextInt();
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            List<SruCourse> coursesList = courseMapper.getCourseByTid(id);
            for (SruCourse course : coursesList) {
                System.out.println(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    //bystuclass
    public static  void getCourseByStuclass() {
        String stuclass = in.next();
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            List<SruCourse> coursesList = courseMapper.getCourseByStuclass(stuclass);
            for (SruCourse course : coursesList) {
                System.out.println(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    //by college
    public static void getCourseByCollege() {
        String college = in.next();
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            List<SruCourse> coursesList = courseMapper.getCourseByCollege(college);
            for (SruCourse course : coursesList) {
                System.out.println(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    //修改课程
    //updateCourse
    public static void updateCourse() {

        System.out.println("请输入课程号、课程名、教师工号、班级：(','分隔)");
        String line = in.next();
        String[] strings = new String[0];
        try {
            strings = line.split(",");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("请用逗号分隔！");
        }

        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            courseMapper.updateCourse(new SruCourse(Integer.parseInt(strings[0]),strings[1],Integer.parseInt(strings[2]),strings[3]));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    //删除课程
    //deleteCourse
    public static void deleteCourse() {
        System.out.println("输入删除id");
        int i = in.nextInt();
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            courseMapper.deleteCourse(i);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    //修改管理员信息
    public static void updateManagerByMid() {

        System.out.println("请输入管理员密码、姓名、性别、出生日期：(','分隔)");
        String line = in.next();
        String[] strings = new String[0];
        try {
            strings = line.split(",");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("请用逗号分隔！");
        }

        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            ManagerMapper managerMapper = sqlSession.getMapper(ManagerMapper.class);

            managerMapper.updateManager(new SruAdmin(Login.managerId, strings[0], strings[1], strings[2], strings[3]));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    //查看管理员信息
    public static void getAllManager() {
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            ManagerMapper managerMapper = sqlSession.getMapper(ManagerMapper.class);
            List<SruAdmin> managersList = managerMapper.getAllManager();
            for (SruAdmin manager: managersList) {
                System.out.println(manager);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    public static void getManagerByMid() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            ManagerMapper managerMapper = sqlSession.getMapper(ManagerMapper.class);
            SruAdmin manager = managerMapper.getManagerById(Login.managerId);
                System.out.println(manager);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    //教师模块
//录入成绩
    //addScore
    static int ID = 0;
    public static void addScore(){
        System.out.println("输入录入的课程号ID");
        ID = in.nextInt();
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            ScoreMapper scoreMapper = sqlSession.getMapper(ScoreMapper.class);

            List<Integer> SidList = scoreMapper.getSidByCid(ID,Login.teacherId);

            if(SidList == null) System.out.println("没有学生数据" );
            for (Integer integer : SidList) {
                System.out.print("学号：" + integer + "  "+" 成绩：");
                float score = in.nextFloat();
                try {
                    scoreMapper.addScore(new SruScore(integer, ID, score));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("录入失败，请检查数据库！");
                }
                System.out.println("录入成功！");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }
//统计成绩
    //getAllScore
public static void getAllScore() {
    if (ID == 0) {
        System.out.println("未录入成绩");
    } else {
        System.out.println("ID为" + ID);
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            ScoreMapper scoreMapper = sqlSession.getMapper(ScoreMapper.class);

            int avg = scoreMapper.getAvg(ID);
            float good = scoreMapper.getgood(ID);
            float qualified = scoreMapper.getqualified(ID);
            float all = scoreMapper.getAll(ID);

            System.out.println("平均分为" + avg);
            System.out.println("优秀率" + good / all);
            System.out.println("及格率" + qualified / all);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }
}

//查看成绩
    //getScoreByid
public static void getScoreById() {
    System.out.println("输入课程号ID");
    int i = in.nextInt();
    // 获得sqlsession对象
    SqlSession sqlSession = MybatisUtils.getSqlSession();
    try {
        // 1.执行 getmapper
        ScoreMapper scoreMapper = sqlSession.getMapper(ScoreMapper.class);

        int avg = scoreMapper.getAvg(i);
        float good = scoreMapper.getgood(i);
        float qualified = scoreMapper.getqualified(i);
        float all = scoreMapper.getAll(i);

        System.out.println("平均分为"+avg);
        System.out.println("优秀率"+good/all);
        System.out.println("及格率"+qualified/all);

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        //关闭
        sqlSession.close();
    }
}
//信息维护
    //updateTeacherbytea
public static void getteaCourseById() {
    SqlSession sqlSession = MybatisUtils.getSqlSession();
    try {
        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
        List<SruStudent> studentList = studentMapper.getstuCourseById(Login.studentId);
        for (SruStudent student : studentList) {
            System.out.println(student);
        }

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        sqlSession.close();
    }
}
//信息维护
    //updateStudent

    public static void TeagetTeacherById() {
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);
            SruTeacher teacher = teacherMapper.getTeacherById(Login.teacherId);

            System.out.println(teacher);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    public static void teaupdateTeacher() {

        System.out.println("请输入老师密码、姓名、性别、出生日期、学院、课程、邮箱：(','分隔)");
        String line = in.next();
        String[] strings = new String[0];
        try {
            strings = line.split(",");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("请用逗号分隔！");
        }

        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            TeacherMapper teacherMapper = sqlSession.getMapper(TeacherMapper.class);


            teacherMapper.updateTeacher(new SruTeacher(Login.teacherId, strings[0], strings[1], strings[2], strings[3], strings[4], strings[5],strings[6]));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }
//打印班级成绩单
    //printScorebytea




    //学生模块
//查看成绩单
    //getScorebystudent
    public static void getstuCourseById() {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            List<SruStudent> studentList = studentMapper.getstuCourseById(Login.studentId);
            for (SruStudent student : studentList) {
                System.out.println(student);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }
//信息维护
    //updateStudent

    public static void stugetStudentById() {
        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            SruStudent student = studentMapper.getStudentById(Login.studentId);

                System.out.println(student);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }

    public static void stuupdateStudent() {

        System.out.println("请输入学生密码、姓名、性别、出生日期、所属学院、专业、班级：(','分隔)");
        String line = in.next();
        String[] strings = new String[0];
        try {
            strings = line.split(",");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("请用逗号分隔！");
        }

        // 获得sqlsession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {
            // 1.执行 getmapper
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

            studentMapper.updateStudent(new SruStudent(Login.studentId, strings[0], strings[1], strings[2], strings[3], strings[4], strings[5],strings[6]));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            sqlSession.close();
        }
    }
//成绩单打印
    //printScorebystu
}
