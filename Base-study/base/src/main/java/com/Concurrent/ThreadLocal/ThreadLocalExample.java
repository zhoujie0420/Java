package com.Concurrent.ThreadLocal;/**
 * @author zhouj
 * @create 2023/6/19 17:01
 */

/**
 * @ClassName : ThreadLocalExample  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/19  17:01
 */
import java.text.SimpleDateFormat;
import java.util.Random;
public class ThreadLocalExample implements Runnable {

    private static final ThreadLocal<SimpleDateFormat> formatter = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd HHmm"));

    public static void main(String[] args) throws InterruptedException {
        ThreadLocalExample threadLocalExample = new ThreadLocalExample();
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(threadLocalExample, "Thread" + i);
            Thread.sleep(new Random().nextInt(1000));
            thread.start();
        }
    }
    @Override
    public void run() {
        System.out.println("Thread name = " + Thread.currentThread().getName() + "default Formatter = " + formatter.get().toPattern());
        try {
            Thread.sleep(new Random().nextInt(1000));
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        formatter.set(new SimpleDateFormat());
        System.out.println("Thread name = " + Thread.currentThread().getName() + "default Formatter = " + formatter.get().toPattern());
    }
}
