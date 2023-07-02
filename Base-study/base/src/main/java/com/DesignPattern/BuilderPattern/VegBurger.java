package com.DesignPattern.BuilderPattern;

/**
 * @author zhouj
 * @create 2023/6/30 20:48
 */

public class VegBurger extends Burger{
    @Override
    public String name() {
        return "VegBurger";
    }

    @Override
    public float price() {
        return 25.0f;
    }
}

