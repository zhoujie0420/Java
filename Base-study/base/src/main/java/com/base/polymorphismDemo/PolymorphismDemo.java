package com.base.polymorphismDemo;/**
 * @author zhouj
 * @create 2023/6/16 20:25
 */

public class PolymorphismDemo {
    public static void main(String[] args) {
        Parents p1 = new Daughter();
        p1.say();
        Parents p2 = new Son();
        p2.say();
    }
}
