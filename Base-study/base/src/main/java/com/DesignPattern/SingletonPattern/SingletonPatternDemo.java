package com.DesignPattern.SingletonPattern;/**
 * @author zhouj
 * @create 2023/6/17 19:39
 */

/**
 * @ClassName : SingletonPatternDemo  //类名
 * @Description :   //描述
 * @Author : dell //作者
 * @Date: 2023/6/17  19:39
 */

public class SingletonPatternDemo {
    public static void main(String[] args) {
        SingleObject instance = SingleObject.getInstance();
        instance.showMessage();
    }
}
