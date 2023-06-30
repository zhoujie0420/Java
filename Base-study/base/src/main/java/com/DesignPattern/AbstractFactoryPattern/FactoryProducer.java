package com.DesignPattern.AbstractFactoryPattern;/**
 * @author zhouj
 * @create 2023/6/17 19:17
 */

/**
 * @ClassName : FactoryProducer  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/17  19:17
 */

public class FactoryProducer {
    public static AbstractFactory getFactory(String choice){
        if(choice.equalsIgnoreCase("SHAPE")){
            return new ShapeFactory();
        }
        else if(choice.equalsIgnoreCase("COLOR")){
            return new ColorFactory();
        }
        return null;
    }
}
