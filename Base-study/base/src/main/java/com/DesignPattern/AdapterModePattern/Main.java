package com.DesignPattern.AdapterModePattern;

import java.util.concurrent.Callable;

/**
 * @author: jiezhou
 * 适配器模式
 **/

public class Main {
    public static void main(String[] args) {
        Callable<Long> callable = new Task(123L);
        Thread thread = new Thread(new RunnableAdapter(callable));
        thread.start();
    }
}
