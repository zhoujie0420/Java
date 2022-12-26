package com.zz.Main;

import junit.framework.JUnit4TestAdapterCache;

import java.util.Scanner;

public class adminAct {
    static Scanner input = new Scanner(System.in);
    public static void admin1(){
        System.out.println("1.添加教师信息");
        System.out.println("2.查询教师信息");
        System.out.println("3.修改教师信息");
        System.out.println("4.删除教师信息");
        int flag = input.nextInt();
        switch (flag){
            case 1:
                uuaction.addTeacher();
                System.out.print("您的账号为");
                uuaction.getMaxTeacherId();
                System.out.println();
                System.out.println("添加成功");
                break;
            case 2:
                System.out.println("1.查询所有教师");
                System.out.println("2.根据id查询");
                int i = input.nextInt();
                if(i == 1){
                    uuaction.getAllTeacher();
                }else if (i == 2){
                    System.out.println("输入查询id");
                    uuaction.getTeacherById();
                }else {
                    System.out.println("输入编号错误");
                    break;
                }
                break;
            case 3:
                uuaction.updateTeacher();
                System.out.println("修改成功");
                break;
            case 4:
                uuaction.deleteTeacher();
                System.out.println("删除成功");
                break;
            default:
                System.out.println("输入有误。。。");
                break;
        }
    };
    public static void admin2(){
        System.out.println("1.添加学生信息");
        System.out.println("2.查询学生信息");
        System.out.println("3.修改学生信息");
        System.out.println("4.删除学生信息");
        int flag = input.nextInt();
        switch (flag){
            case 1:
                uuaction.addStudent();
                System.out.print("您的账号为");
                uuaction.getMaxStudentId();
                System.out.println();
                System.out.println("添加成功");
                break;
            case 2:
                System.out.println("1.查询所有学生");
                System.out.println("2.根据id查询");
                System.out.println("3.根据班级查询班级学生");
                int i = input.nextInt();
                if(i == 1){
                    uuaction.getAllStudent();
                }else if (i == 2){
                    System.out.println("输入查询id");
                    uuaction.getStudentById();
                }else if (i == 3){
                    System.out.println("输入查询班级");
                    uuaction.getStudentByStuclass();
                }else {
                    System.out.println("输入编号错误");
                    break;
                }
                break;
            case 3:
                uuaction.updateStudent();
                System.out.println("修改成功");
                break;
            case 4:
                uuaction.deleteStudent();
                System.out.println("删除成功");
                break;
            default:
                System.out.println("输入有误。。。");
                break;
        }
    };
    public static void admin3(){
        System.out.println("1.添加课程");
        System.out.println("2.查询课程");
        System.out.println("3.修改课程");
        System.out.println("4.删除课程");
        int flag = input.nextInt();
        switch (flag){
            case 1:
                uuaction.addCourse();
                System.out.println();
                System.out.println("添加成功");
                break;
            case 2:
                System.out.println("1.课程号查询");
                System.out.println("2.课程名查询");
                System.out.println("3.教师工号查询");
                System.out.println("4.班级查询");
                System.out.println("5.学院查询");
                System.out.println("6.所有课程");
                int i = input.nextInt();
                if(i == 1){
                    System.out.println("输入查询课程号");
                    uuaction.getCourseById();
                }else if (i == 2){
                    System.out.println("输入查询课程名");
                    uuaction.getCourseByCname();
                }else if (i == 3){
                    System.out.println("输入查询教师工号");
                    uuaction.getCourseByTid();
                }else if (i == 4){
                    System.out.println("输入查询班级");
                    uuaction.getCourseByStuclass();
                }else if (i == 5){
                    System.out.println("输入查询学院");
                    uuaction.getCourseByCollege();
                }else if (i == 6){
                    uuaction.getAllCourse();
                }else {
                    System.out.println("输入编号错误");
                    break;
                }
                break;
            case 3:
                uuaction.updateCourse();
                System.out.println("修改成功");
                break;
            case 4:
                uuaction.deleteCourse();
                System.out.println("删除成功");
                break;
            default:
                System.out.println("输入有误。。。");
                break;
        }
    };

    public static void admin4(){
        //信息维护
        System.out.println("1.查看管理员信息");
        System.out.println("2.修改管理员信息");
        int flag = input.nextInt();
        switch (flag){
            case 1:
                uuaction.getManagerByMid();
                break;
            case 2:
                uuaction.updateManagerByMid();
                break;
            default:
                System.out.println("输入有误。。。");
                break;
        }
    };
}
