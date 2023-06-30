package com.DesignPattern.FactoryPattern;/**
 * @author zhouj
 * @create 2023/6/17 18:54
 */

/**
 * @ClassName : ShapeFactory  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/17  18:54
 */

public class ShapeFactory {
    public Shape getShape(String shapeType){
        if(shapeType == null){
            return null;
        }
        if("CIRCLE".equalsIgnoreCase(shapeType)){
            //equalsIgnoreCase() 方法用于将字符串与指定的对象比较，不考虑大小写。
            return new Circle();
        } else if("RECTANGLE".equalsIgnoreCase(shapeType)){
            return new Rectangle();
        } else if("SQUARE".equalsIgnoreCase(shapeType)){
            return new Square();
        }
        return null;
    }
}
