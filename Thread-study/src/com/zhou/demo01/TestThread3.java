package com.zhou.demo01;

public class TestThread3 implements Runnable{

    @Override
    public void run() {
        for (int i = 0; i < 200; i++) {
            System.out.println(i);
        }
    }

    public static void main(String[] args) {
        TestThread3 testThread3 = new TestThread3();
        new Thread(testThread3).start();

        for (int i = 0; i < 200; i++) {
            System.out.println(i);
        }
    }
}
