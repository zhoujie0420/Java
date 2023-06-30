package com.DesignPattern.FactoryPattern;/**
 * @author zhouj
 * @create 2023/6/17 18:54
 */

/**
 * @ClassName : Square  //类名
 * @Description : \  //描述
 * @Author : dell //作者
 * @Date: 2023/6/17  18:54
 */

public class Square implements Shape{
    @Override
    public void draw() {
        System.out.println("Inside Square::draw() method.");
    }
}
