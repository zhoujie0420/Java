package com.zz.Main;

import java.io.IOException;
import java.util.Scanner;

public class Menu {
    public static void menu() throws IOException {
        Scanner input = new Scanner(System.in);
        while(true){
            System.out.println("=================================");
            System.out.println("浙江树人大学");
            System.out.println("1.管理员登录");
            System.out.println("2.教师登录");
            System.out.println("3.学生登录");
            System.out.println("4.退出系统");
            int flag = input.nextInt();
            switch (flag){
                case 1:
                    action.actadmin();
                    break;
                case 2:
                    action.actteacher();
                    break;
                case 3:
                    action.actstudent();
                    break;
                case 4:System.exit(0);
                default:
                    System.out.println("错误操作");
            }
        }
    }

}
