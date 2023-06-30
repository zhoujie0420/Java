package com.Concurrent;/**
 * @author zhouj
 * @create 2023/6/19 14:49
 */

/**
 * @ClassName : MyThread  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/19  14:49
 */

class MyThread implements Runnable{
@Override
    public void run(){
        for (int i = 0; i < 100; i++) {
            if(i % 2 == 0){
                System.out.println(Thread.currentThread().getName()+":"+i);
            }
        }
    }
}
public class ThreadTest{
    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        Thread thread = new Thread(myThread);
        thread.start();
        Thread thread1 = new Thread(myThread);
        thread1.start();
    }
}