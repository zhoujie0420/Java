package com.DesignPattern.BuilderPattern;

/**
 * @author zhouj
 * @create 2023/6/30 20:48
 */

public class ChickenBurger extends Burger{
    @Override
    public String name() {
        return "ChickenBurger";
    }

    @Override
    public float price() {
        return 50.5f;
    }
}

