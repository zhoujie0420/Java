package com.DesignPattern.AdapterModePattern;

import java.util.concurrent.Callable;

/**
 * @author zhouj
 * @create 2023/7/2 13:57
 */

public class Task implements Callable<Long> {
    private Long num;

    public Task(Long num) {
        this.num = num;
    }

    @Override
    public Long call() throws Exception {
        long r = 0;
        for(long i = 1; i <= this.num; i++) {
            r += i;
        }
        System.out.println("result: " + r);
        return r;
    }
}
