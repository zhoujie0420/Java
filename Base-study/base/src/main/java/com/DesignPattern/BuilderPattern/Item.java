package com.DesignPattern.BuilderPattern;

/**
 * @author zhouj
 * @create 2023/6/30 20:44
 */

/**
 * 建造者模式
 */
public interface Item {
    public String name();
    public Packing packing();
    public float price();

}
