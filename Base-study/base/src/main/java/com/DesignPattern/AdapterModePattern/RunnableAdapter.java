package com.DesignPattern.AdapterModePattern;

import java.util.concurrent.Callable;

/**
 * @author zhouj
 * @create 2023/7/2 13:59
 */

public class RunnableAdapter implements Runnable{
    private Callable<?> callable;

    public RunnableAdapter(Callable<?> callable) {
        this.callable = callable;
    }
    @Override
    public void run(){
        try {
            callable.call();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
