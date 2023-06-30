package com.DesignPattern.FactoryPattern;/**
 * @author zhouj
 * @create 2023/6/17 18:53
 */

/**
 * @ClassName : Rectangle  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/17  18:53
 */

public class Rectangle implements Shape{
    @Override
    public void draw() {
        System.out.println("Inside Rectangle::draw() method.");
    }
}
