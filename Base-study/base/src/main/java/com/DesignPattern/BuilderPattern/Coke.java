package com.DesignPattern.BuilderPattern;

/**
 * @author zhouj
 * @create 2023/6/30 20:49
 */

public class Coke extends ColdDrink{
    @Override
    public String name() {
        return "Coke";
    }

    @Override
    public float price() {
        return 5.0f;
    }

}
