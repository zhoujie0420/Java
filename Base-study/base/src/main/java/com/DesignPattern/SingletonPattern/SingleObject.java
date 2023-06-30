package com.DesignPattern.SingletonPattern;/**
 * @author zhouj
 * @create 2023/6/17 19:39
 */

/**
 * @ClassName : SingleObject  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/17  19:39
 */

public class SingleObject {
    private static SingleObject instance = new SingleObject();
    private SingleObject(){}
    public static SingleObject getInstance(){
        return instance;
    }
    public void showMessage(){
        System.out.println("Hello World!");
    }
}
