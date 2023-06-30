package com.Concurrent.ThreadLocal;/**
 * @author zhouj
 * @create 2023/6/19 15:17
 */


import java.lang.reflect.Field;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName : ThreadLocalDemo  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/19  15:17
 */

public class ThreadLocalDemo {
    private static final Object o1 = new Object();
    private static final Object o2 = new Object();

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 10, null, null);
        threadPoolExecutor.submit(()->{
            synchronized (o1){
                System.out.println(Thread.currentThread() + "get o1");
                try {
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread() + "wait o2");
                synchronized (o2){
                    System.out.println(Thread.currentThread() + "get o2");
                }
            }
        });
        threadPoolExecutor.submit(()->{
            synchronized (o2){
                System.out.println(Thread.currentThread() + "get o2");
                try {
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread() + "wait o1");
                synchronized (o1){
                    System.out.println(Thread.currentThread() + "get o1");
                }
            }
        });
    }
}