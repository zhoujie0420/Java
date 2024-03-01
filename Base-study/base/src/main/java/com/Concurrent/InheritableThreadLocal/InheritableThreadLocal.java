package com.Concurrent.InheritableThreadLocal;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: elk
 * @create: 2024-02-27 14:46
 **/
//解决ThreadLocal在开启子线程时，父线程向子线程值传递问题，源码分析
public class InheritableThreadLocal<S> {


    public static void main(String[] args) throws Exception {
        ThreadLocal<String> threadLocal = new TransmittableThreadLocal<>();

        ExecutorService threadPool = TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(2));
        for (int i = 0; i < 5; i++) {
            threadLocal.set("初始化的值能继承吗？" + i);
            System.out.println("父线程的ThreadLocal值：" + threadLocal.get());
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("子线程到了");
                    System.out.println("=========子线程的ThreadLocal值：" + threadLocal.get());
                }
            });
        }
    }


}


