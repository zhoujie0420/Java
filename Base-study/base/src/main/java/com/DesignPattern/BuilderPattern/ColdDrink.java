package com.DesignPattern.BuilderPattern;

/**
 * @author zhouj
 * @create 2023/6/30 20:47
 */

public abstract class ColdDrink implements Item{
    @Override
    public Packing packing() {
        return new Bottle();
    }
    @Override
    public abstract float price();
}
