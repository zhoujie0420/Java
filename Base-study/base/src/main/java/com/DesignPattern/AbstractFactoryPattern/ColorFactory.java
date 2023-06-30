package com.DesignPattern.AbstractFactoryPattern;/**
 * @author zhouj
 * @create 2023/6/17 19:16
 */

/**
 * @ClassName : ColorFactory  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/17  19:16
 */

public class ColorFactory extends AbstractFactory{
    @Override
    public Shape getShape(String shape) {
        return null;
    }
    @Override
    public Color getColor(String color) {
        if(color == null){
            return null;
        }
        if(color.equalsIgnoreCase("RED")){
            return new Red();
        }
        else if(color.equalsIgnoreCase("GREEN")){
            return new Green();
        }
        else if(color.equalsIgnoreCase("BLUE")){
            return new Blue();
        }
        return null;
    }
}
