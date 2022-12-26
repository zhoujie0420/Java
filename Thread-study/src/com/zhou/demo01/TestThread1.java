package com.zhou.demo01;

public class TestThread1 extends Thread{
    public void run(){
        for (int i = 0; i < 200; i++) {
            System.out.println("一"+i);
        }
    }

    public static void main(String[] args) {
        TestThread1 testThread1 = new TestThread1();
        testThread1.start();   //开启线程 同时运行
        testThread1.run();   //依次运行


        for (int i = 0; i < 1000; i++) {
            System.out.println("二"+i);
        }
    }
}
