package com.DesignPattern.AbstractFactoryPattern;/**
 * @author zhouj
 * @create 2023/6/17 19:08
 */

/**
 * @ClassName : AbstractFactory  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/17  19:08
 */

public abstract class AbstractFactory {
    public abstract Color getColor(String color);
    public abstract Shape getShape(String shape) ;
}
