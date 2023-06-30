package com.DesignPattern.AbstractFactoryPattern;/**
 * @author zhouj
 * @create 2023/6/17 19:06
 */

/**
 * @ClassName : Rectangle  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/17  19:06
 */

public class Rectangle implements Shape {

    @Override
    public void draw() {
        System.out.println("Inside Rectangle::draw() method.");
    }
}
