package com.Concurrent.ThreadPoolExecutor;

import java.util.Date;

/**
 * @author zhouj
 * @create 2023/6/27 17:54
 */

public class MyRunnable  implements  Runnable{
    String command;
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " Start.time = " + new Date());
//        processCommand();
        System.out.println(Thread.currentThread().getName() + " End.time = " + new Date());
    }

//    private void processCommand() {
//        try {
//
//        }
//    }
}
