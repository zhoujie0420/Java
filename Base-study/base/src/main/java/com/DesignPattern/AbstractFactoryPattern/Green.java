package com.DesignPattern.AbstractFactoryPattern;/**
 * @author zhouj
 * @create 2023/6/17 19:07
 */

/**
 * @ClassName : Green  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/17  19:07
 */

public class Green implements Color{
    @Override
    public void fill() {
        System.out.println("Inside Green::fill() method.");
    }

}
