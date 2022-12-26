package com.zhou.demo01;

import com.sun.xml.internal.fastinfoset.algorithm.BooleanEncodingAlgorithm;

import java.util.TreeMap;

public class Race implements Runnable{
    private static String winner;

    @Override
    public void run() {
        for (int i = 0; i <= 100; i++) {

            boolean flag = gameOver(i);
            if(flag) {
                break;
            }
            System.out.println(Thread.currentThread().getName() + "跑了" + i  + "步");
        }
    }

    private boolean gameOver(int steps){
        if(winner != null){
            return true;
        }
        else if(steps == 100){
            winner = Thread.currentThread().getName();
            System.out.println("winner"+winner);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Race race = new Race();

        new Thread(race,"tuzi").start();
        new Thread(race,"wugui").start();

    }
}
