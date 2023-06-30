package com.DesignPattern.AbstractFactoryPattern;/**
 * @author zhouj
 * @create 2023/6/17 19:09
 */

/**
 * @ClassName : ShapeFactory  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/17  19:09
 */

public class ShapeFactory extends AbstractFactory{
    @Override
    public Shape getShape(String shape) {
        if(shape == null){
            return null;
        }
        if(shape.equalsIgnoreCase("CIRCLE")){
            return new Circle();
        }
        else if(shape.equalsIgnoreCase("RECTANGLE")){
            return new Rectangle();
        }
        else if(shape.equalsIgnoreCase("SQUARE")){
            return new Square();
        }
        return null;
    }
    @Override
    public Color getColor(String color) {
        return null;
    }
}

